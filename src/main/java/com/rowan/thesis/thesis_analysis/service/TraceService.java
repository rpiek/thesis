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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TraceService {

    private final Map<String, Set<String>> readEndpointMap = new HashMap<>();
    private final Map<String, Set<String>> writeEndpointMap = new HashMap<>();

    Set<String> readMethods = new HashSet<>(Arrays.asList(ModelConstants.DATABASE_NAME, ModelConstants.SEND_READ_STRING, ModelConstants.READ_STRING));
    Set<String> writeMethods = new HashSet<>(Arrays.asList(ModelConstants.DATABASE_NAME, ModelConstants.SEND_READ_STRING, ModelConstants.WRITE_STRING));


    public Model tracesToModel(List<List<Span>> spanLists) {
        List<Trace> readTraces = new ArrayList<>();
        List<Trace> writeTraces = new ArrayList<>();
        readEndpointMap.clear();
        writeEndpointMap.clear();
        for (List<Span> spans : spanLists) {
            final Trace trace = getTree(spans);
            readTraces.addAll(getSubGraphs(trace, readMethods));
            writeTraces.addAll(getSubGraphs(trace, writeMethods));
        }

        return new Model(readTraces, writeTraces, readEndpointMap, writeEndpointMap);
    }

    private Trace getTree(List<Span> spans) {
        Set<Vertex> vertices = new HashSet<>();
        Set<Edge> edges = new HashSet<>();
        Span beginSpan = spans.stream().filter(span -> span.getParentId() == null).findFirst().orElse(null);
        Span clientSpan = spans.stream().filter(span -> span.getKind() != null && span.getKind().equals("CLIENT")).findFirst().orElse(null);

        if (beginSpan == null || clientSpan == null) {
            // Handle case where beginSpan or clientSpan is not found
            return new Trace(vertices, edges);
        }

        Vertex root = new Vertex(beginSpan.getSpanId(), beginSpan.getLocalEndpoint().getServiceName());
        vertices.add(root);
        spans.remove(beginSpan);
        spans.remove(clientSpan);

        createTree(root, beginSpan, spans, vertices, edges);

        return new Trace(vertices, edges);
    }

    private void createTree(Vertex parent, Span parentSpan, List<Span> spans, Set<Vertex> vertices, Set<Edge> edges) {
        List<Span> spanList = spans.stream().filter(span -> span.getParentId().equals(parentSpan.getSpanId())).toList();

        for (Span childSpan : spanList) {
            if (childSpan.getKind() != null && childSpan.getKind().equals("CLIENT")) continue;
            Vertex child;
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
                mutateMap(childSpan);
                if (childSpan.getPath().equals(ModelConstants.DATABASE_READ) || childSpan.getPath().equals(ModelConstants.DATABASE_WRITE)) {
                    edges.add(new Edge(childSpan.getPath(), ModelConstants.DATABASE_NAME, parent, child));
                } else {
                    if (childSpan.getTags().getMethod().equals(ModelConstants.POST_STRING) || childSpan.getTags().getMethod().equals(ModelConstants.PUT_STRING)) {
                        if (spans.stream().anyMatch(span -> span.getParentId().equals(childSpan.getSpanId()) && span.getPath().equals(ModelConstants.DATABASE_WRITE))) {
                            edges.add(new Edge(childSpan.getPath(), ModelConstants.WRITE_STRING, parent, child));
                        } else {
                            String serviceName = childSpan.getLocalEndpoint().getServiceName();
                            fillMap(childSpan, serviceName, readEndpointMap);
                            edges.add(new Edge(childSpan.getPath(), ModelConstants.SEND_READ_STRING, parent, child));
                        }
                    } else {
                        edges.add(new Edge(childSpan.getPath(), ModelConstants.READ_STRING, parent, child));
                    }
                }
            }
            createTree(child, childSpan, spans, vertices, edges);
        }
    }

    private List<Trace> getSubGraphs(Trace trace, Set<String> methods) {
        Set<Edge> filteredEdges = new HashSet<>();
        // Put in all edges were the method equals the given set of allowed methods
        for (Edge edge : trace.getEdges()) {
            if (methods.contains(edge.getMethod())) {
                filteredEdges.add(edge);
            }
        }

        Trace filteredTrace = new Trace(trace.getVertices(), filteredEdges);

        List<Trace> connectedGraphs = new ArrayList<>();
        Set<Vertex> visitedVertices = new HashSet<>();

        for (Vertex vertex : filteredTrace.getVertices()) {
            // Check if we did not visit the vertex
            if (!visitedVertices.contains(vertex)) {
                Set<Vertex> connectedVertices = new HashSet<>();
                Set<Edge> connectedEdges = new HashSet<>();

                performDFS(vertex, filteredTrace, visitedVertices, connectedVertices, connectedEdges);

                // If we want to create a set of read traces we must filter out the vertices where a post request is made, but where only reads or no
                // database calls are made
                if (methods.contains(ModelConstants.WRITE_STRING)) {
                    Set<Vertex> readSendVertices = connectedEdges.stream().filter(edge -> edge.getMethod().equals(ModelConstants.SEND_READ_STRING)).map(Edge::getTarget).collect(Collectors.toSet());
                    readSendVertices.forEach(vertex1 -> connectedEdges.removeIf(edge -> edge.getSource() == vertex1 && edge.getMethod().equals(ModelConstants.DATABASE_NAME)) );
                    connectedVertices.removeIf(vertex1 -> connectedEdges.stream().noneMatch(edge -> edge.getSource().equals(vertex1) || edge.getTarget().equals(vertex1)));
                }

                Trace connectedGraph = new Trace(connectedVertices, connectedEdges);

                // The trace should not exist of only one vertex representing a service (i.e. we check if there are edges to other services)
                if (connectedGraph.getEdges().stream().anyMatch(edge -> !edge.getMethod().equals(ModelConstants.DATABASE_NAME)) && connectedGraph.getEdges().stream().anyMatch(edge -> edge.getMethod().equals(ModelConstants.DATABASE_NAME))) {
                    connectedGraphs.add(connectedGraph);
                }
            }
        }

        return connectedGraphs;
    }

    private static void performDFS(Vertex vertex, Trace trace, Set<Vertex> visited, Set<Vertex> connectedVertices, Set<Edge> connectedEdges) {
        visited.add(vertex);
        connectedVertices.add(vertex);

        for (Edge edge : trace.getEdges()) {
            // If connected, the vertex should be in the source or the target of an edge
            if (edge.getSource().equals(vertex) || edge.getTarget().equals(vertex)) {
                connectedEdges.add(edge);
                // Return either the source or the target of the edge as the adjacent vertex
                Vertex adjacentVertex = (edge.getSource().equals(vertex)) ? edge.getTarget() : edge.getSource();
                // If we haven't visited this adjacent vertex, perform DFS on this as well
                if (!visited.contains(adjacentVertex)) {
                    performDFS(adjacentVertex, trace, visited, connectedVertices, connectedEdges);
                }
            }

        }
    }

    private void mutateMap(Span span) {
        if (span.getTags() != null && span.getTags().getMethod() != null) {
            String serviceName = span.getLocalEndpoint().getServiceName();
            String method = span.getTags().getMethod();
            if (method.equals(ModelConstants.GET_STRING)) {
                fillMap(span, serviceName, readEndpointMap);
            } else if (method.equals(ModelConstants.POST_STRING) || method.equals(ModelConstants.PUT_STRING)) {
                fillMap(span, serviceName, writeEndpointMap);
            }
        }
    }

    private void fillMap(Span span, String serviceName, Map<String, Set<String>> readEndpointMap) {
        if (readEndpointMap.containsKey(serviceName)) {
            Set<String> value = readEndpointMap.get(serviceName);
            value.add(span.getPath());
            readEndpointMap.put(serviceName, value);
        } else {
            readEndpointMap.put(serviceName, new HashSet<>(Collections.singleton(span.getPath())));
        }
    }

}
