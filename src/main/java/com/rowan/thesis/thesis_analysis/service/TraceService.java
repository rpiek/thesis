package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Edge;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
import com.rowan.thesis.thesis_analysis.model.trace.Vertex;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TraceService {

    final Set<String> readMethods = new HashSet<>(Arrays.asList(ModelConstants.DATABASE_NAME, ModelConstants.SEND_READ_STRING, ModelConstants.READ_STRING));
    final Set<String> writeMethods = new HashSet<>(Arrays.asList(ModelConstants.DATABASE_NAME, ModelConstants.SEND_READ_STRING, ModelConstants.WRITE_STRING));

    public Model tracesToModel(List<List<Span>> spanLists) {
        List<Trace> readTraces = new ArrayList<>();
        List<Trace> writeTraces = new ArrayList<>();
        Map<String, Set<String>> readEndpointMap = new HashMap<>();
        Map<String, Set<String>> writeEndpointMap = new HashMap<>();
        for (List<Span> spans : spanLists) {
            final Trace trace = createTrace(spans);
            List<Trace> readSubTraces = cutTraces(trace, readMethods);
            List<Trace> writeSubTraces = cutTraces(trace, writeMethods);
            // Add the traces into their respective lists
            readTraces.addAll(readSubTraces);
            writeTraces.addAll(writeSubTraces);
            // Add the interfaces of the edges into their respective endpoint maps
            readSubTraces.forEach(trace1 -> trace1.getEdges().forEach(edge -> fillMap(edge, readEndpointMap)));
            writeSubTraces.forEach(trace1 -> trace1.getEdges().forEach(edge -> fillMap(edge, writeEndpointMap)));
        }

        return new Model(readTraces, writeTraces, readEndpointMap, writeEndpointMap, spanLists.size());
    }

    private Trace createTrace(List<Span> spans) {
        Set<Vertex> vertices = new HashSet<>();
        Set<Edge> edges = new HashSet<>();
        Span beginSpan = spans.stream().filter(span -> span.getParentId() == null).findFirst().orElse(null);

        if (beginSpan == null) {
            // Handle case where beginSpan or clientSpan is not found
            return new Trace(vertices, edges);
        }

        Vertex root = new Vertex(beginSpan.getSpanId(), beginSpan.getLocalEndpoint().getServiceName());
        vertices.add(root);
        spans.remove(beginSpan);

        return createTraceChildren(root, beginSpan, spans, vertices, edges);
    }

    private Trace createTraceChildren(Vertex parent, Span parentSpan, List<Span> spans, Set<Vertex> vertices, Set<Edge> edges) {
        List<Span> spanList = spans.stream().filter(span -> span.getParentId().equals(parentSpan.getSpanId())).toList();

        for (Span childSpan : spanList) {
            // Ignore the client spans from zipkin
            if (childSpan.getKind() != null && childSpan.getKind().equals("CLIENT")) continue;
            Vertex child;
            // If it's a database call create a vertex with constant name for databases
            if (childSpan.getPath().equals(ModelConstants.DATABASE_READ) || childSpan.getPath().equals(ModelConstants.DATABASE_WRITE)) {
                child = new Vertex(childSpan.getSpanId(), ModelConstants.DATABASE_NAME);
            } else {
                child = new Vertex(childSpan.getSpanId(), childSpan.getLocalEndpoint().getServiceName());
            }

            // Check if child vertex equals the name of the parent vertex
            if (parent.getName().equals(child.getName())) {
                // Merge child vertex into parent vertex
                child = parent;
            } else {
                vertices.add(child);
            }

            // Add edge if child vertex is not the same as parent vertex
            if (!child.equals(parent)) {
                if (childSpan.getPath().equals(ModelConstants.DATABASE_READ) || childSpan.getPath().equals(ModelConstants.DATABASE_WRITE)) {
                    edges.add(new Edge(childSpan.getPath(), ModelConstants.DATABASE_NAME, parent, child));
                } else {
                    if (childSpan.getTags().getMethod().equals(ModelConstants.POST_STRING) || childSpan.getTags().getMethod().equals(ModelConstants.PUT_STRING)) {
                        if (spans.stream().anyMatch(span -> span.getParentId().equals(childSpan.getSpanId()) && span.getPath().equals(ModelConstants.DATABASE_WRITE))) {
                            edges.add(new Edge(childSpan.getPath(), ModelConstants.WRITE_STRING, parent, child));
                        } else {
                            edges.add(new Edge(childSpan.getPath(), ModelConstants.SEND_READ_STRING, parent, child));
                        }
                    } else {
                        edges.add(new Edge(childSpan.getPath(), ModelConstants.READ_STRING, parent, child));
                    }
                }
            }
            // Vertices and edges are being added by calling this method recursively
            createTraceChildren(child, childSpan, spans, vertices, edges);
        }

        return new Trace(vertices, edges);
    }

    private List<Trace> cutTraces(Trace trace, Set<String> methods) {
        // Put in all edges were the method equals the given set of allowed methods
        Set<Edge> filteredEdges = trace.getEdges().stream().filter(edge -> methods.contains(edge.getMethod())).collect(Collectors.toSet());

        Trace filteredTrace = new Trace(trace.getVertices(), filteredEdges);

        List<Trace> connectedGraphs = new ArrayList<>();
        Set<Vertex> visitedVertices = new HashSet<>();

        for (Vertex vertex : filteredTrace.getVertices()) {
            // Check if we did not visit the vertex, to overcome overlapping traces
            if (!visitedVertices.contains(vertex)) {

                Trace connectedTrace = dfs(vertex, filteredTrace.getEdges(), visitedVertices, new HashSet<>(), new HashSet<>());

                // If we want to create a set of write traces we must filter out database relationships as result of a read/send relation
                if (methods.contains(ModelConstants.WRITE_STRING)) {
                    Set<Vertex> readSendVertices = connectedTrace.getEdges().stream().filter(edge -> edge.getMethod().equals(ModelConstants.SEND_READ_STRING)).map(Edge::getTarget).collect(Collectors.toSet());
                    readSendVertices.forEach(vertex1 -> connectedTrace.getEdges().removeIf(edge -> edge.getSource() == vertex1 && edge.getMethod().equals(ModelConstants.DATABASE_NAME)) );
                    connectedTrace.getVertices().removeIf(vertex1 -> connectedTrace.getEdges().stream().noneMatch(edge -> edge.getSource().equals(vertex1) || edge.getTarget().equals(vertex1)));
                }

                // The trace should not exist of only one vertex representing a service (i.e. we check if there are edges to other services)
                // This is done to decrease the load when calculating the metrics
                if (connectedTrace.getEdges().stream().anyMatch(edge -> !edge.getMethod().equals(ModelConstants.DATABASE_NAME))) {
                    if(methods.contains(ModelConstants.WRITE_STRING)) {
                        // If we are creating write traces there must be at least one write relation between services, done
                        // to decrease the load when calculating the metrics
                        if (connectedTrace.getEdges().stream().anyMatch(edge -> edge.getMethod().equals(ModelConstants.WRITE_STRING))) {
                            connectedGraphs.add(connectedTrace);
                        }
                    } else {
                        connectedGraphs.add(connectedTrace);
                    }
                }
            }
        }

        return connectedGraphs;
    }

    private Trace dfs(Vertex vertex, Set<Edge> edges, Set<Vertex> visited, Set<Vertex> connectedVertices, Set<Edge> connectedEdges) {
        visited.add(vertex);
        connectedVertices.add(vertex);
        Trace connectedTrace = new Trace(new HashSet<>(), new HashSet<>());

        for (Edge edge : edges) {
            // If connected, the vertex should be in the source or the target of an edge
            if (edge.getSource().equals(vertex) || edge.getTarget().equals(vertex)) {
                connectedEdges.add(edge);
                // Return either the source or the target of the edge as the adjacent vertex
                Vertex adjacentVertex = (edge.getSource().equals(vertex)) ? edge.getTarget() : edge.getSource();
                // If we haven't visited this adjacent vertex, perform DFS on this as well
                if (!visited.contains(adjacentVertex)) {
                   // Mutate connectedVertices and connectedEdges recursively
                   dfs(adjacentVertex, edges, visited, connectedVertices, connectedEdges);
                }
            }
        }

        connectedTrace.addVertices(connectedVertices);
        connectedTrace.addEdges(connectedEdges);
        return connectedTrace;
    }

    private void fillMap(Edge edge, Map<String, Set<String>> map) {
        if (edge.getTarget().getName().equals(ModelConstants.DATABASE_NAME)) return;
        if (map.containsKey(edge.getTarget().getName())) {
            Set<String> value = map.get(edge.getTarget().getName());
            value.add(edge.getEndpoint());
            map.put(edge.getTarget().getName(), value);
        } else {
            map.put(edge.getTarget().getName(), new HashSet<>(Collections.singleton(edge.getEndpoint())));
        }
    }

}
