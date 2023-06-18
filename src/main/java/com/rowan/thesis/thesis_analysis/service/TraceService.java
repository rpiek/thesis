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
import org.springframework.stereotype.Service;

@Service
public class TraceService {

    private final Map<String, Set<String>> readEndpointMap = new HashMap<>();
    private final Map<String, Set<String>> writeEndpointMap = new HashMap<>();

    private final Set<String> spanIdSet = new HashSet<>();

    Set<String> readMethods = new HashSet<>(Arrays.asList(ModelConstants.DATABASE_NAME, ModelConstants.GET_STRING));
    Set<String> writeMethods = new HashSet<>(Arrays.asList(ModelConstants.DATABASE_NAME, ModelConstants.POST_STRING, ModelConstants.PUT_STRING));


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

    public Trace getTree(List<Span> spans) {
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
            Vertex child;
            if (childSpan.getPath().equals(ModelConstants.DATABASE_NAME)) {
                child = new Vertex(childSpan.getSpanId(), ModelConstants.DATABASE_NAME);
            } else {
                child = new Vertex(childSpan.getSpanId(), childSpan.getLocalEndpoint().getServiceName());
            }

            if (parent.getName().equals(child.getName())) {
                // Merge child vertex into parent vertex
                vertices.remove(child);
                child = parent;
            } else {
                vertices.add(child);
            }

            if (!child.equals(parent)) {
                mutateMap(childSpan);
                // Add edge if child vertex is not the same as parent vertex
                if (childSpan.getPath().equals(ModelConstants.DATABASE_NAME)) {
                    edges.add(new Edge(ModelConstants.DATABASE_NAME, ModelConstants.DATABASE_NAME, parent, child));
                } else {
                    edges.add(new Edge(childSpan.getPath(), childSpan.getTags().getMethod(), parent, child));
                }
            }

            createTree(child, childSpan, spans, vertices, edges);
        }
    }

    private List<Trace> getSubGraphs(Trace trace, Set<String> methods) {
        Set<Edge> filteredEdges = new HashSet<>();
        for (Edge edge : trace.getEdges()) {
            if (methods.contains(edge.getMethod())) {
                filteredEdges.add(edge);
            }
        }

        Trace filteredTrace = new Trace(trace.getVertices(), filteredEdges);

        List<Trace> connectedGraphs = new ArrayList<>();
        Set<Vertex> visitedVertices = new HashSet<>();

        for (Vertex vertex : filteredTrace.getVertices()) {
            if (!visitedVertices.contains(vertex)) {
                Set<Vertex> connectedVertices = new HashSet<>();
                Set<Edge> connectedEdges = new HashSet<>();

                performDFS(vertex, filteredTrace, visitedVertices, connectedVertices, connectedEdges);

                Trace connectedGraph = new Trace(connectedVertices, connectedEdges);
                if (connectedGraph.getEdges().stream().anyMatch(edge -> !edge.getMethod().equals(ModelConstants.DATABASE_NAME))) {
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
            if (edge.getSource().equals(vertex) || edge.getTarget().equals(vertex)) {
                connectedEdges.add(edge);
                Vertex adjacentVertex = (edge.getSource().equals(vertex)) ? edge.getTarget() : edge.getSource();
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
