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
        List<Trace> traces = new ArrayList<>();
        readEndpointMap.clear();
        writeEndpointMap.clear();
        for (List<Span> spans : spanLists) {
            traces.add(getTree(spans));
        }

        return new Model(traces, readEndpointMap, writeEndpointMap);
    }

    public Trace getTree(List<Span> spans) {
        Set<Vertex> vertices = new HashSet<>();
        Set<Edge> edges = new HashSet<>();
        Span beginSpan = spans.stream().filter(span -> span.getParentId() == null).toList().get(0);
        Span clientSpan = spans.stream().filter(span -> span.getKind() != null && span.getKind().equals("CLIENT")).toList().get(0);
        spans.remove(beginSpan);
        spans.remove(clientSpan);
        Vertex root = new Vertex(beginSpan.getSpanId(), beginSpan.getLocalEndpoint().getServiceName());
        vertices.add(root);
        List<Span> spanList = spans.stream().filter(span -> span.getParentId().equals(beginSpan.getSpanId())).toList();
        for (Span span : spanList) {
            Vertex child = createTree(span, spans, vertices, edges);
            if (span.getPath().equals(ModelConstants.DATABASE_NAME)) {
                edges.add(new Edge(ModelConstants.DATABASE_NAME, ModelConstants.DATABASE_NAME, root, child));
            } else {
                edges.add(new Edge(span.getPath(), span.getTags().getMethod(), root, child));
            }
        }

        return new Trace(vertices, edges);
    }

    private Vertex createTree(Span span, List<Span> spans, Set<Vertex> vertices, Set<Edge> edges) {
        Vertex root;
        if (span.getPath().equals(ModelConstants.DATABASE_NAME)) {
            root = new Vertex(span.getSpanId(), ModelConstants.DATABASE_NAME);
        } else {
            root = new Vertex(span.getSpanId(), span.getLocalEndpoint().getServiceName());
        }
        vertices.add(root);
        List<Span> spanList = spans.stream().filter(span1 -> span1.getParentId().equals(span.getSpanId())).toList();
        mutateMap(span);
        for (Span childSpan : spanList) {
            Vertex child = createTree(childSpan, spans, vertices, edges);
            if (childSpan.getPath().equals(ModelConstants.DATABASE_NAME)) {
                edges.add(new Edge(ModelConstants.DATABASE_NAME, ModelConstants.DATABASE_NAME, root, child));
            } else {
                edges.add(new Edge(childSpan.getPath(), childSpan.getTags().getMethod(), root, child));
            }
        }
        return root;
    }

    public List<Trace> getSubGraphs(Trace trace, Set<String> methods) {
        Set<Edge> filteredEdges = new HashSet<>();
        for (Edge edge : trace.getEdges()) {
            if (readMethods.contains(edge.getMethod())) {
                filteredEdges.add(edge);
            }
        }

        trace.setEdges(filteredEdges);

        List<Trace> connectedGraphs = new ArrayList<>();
        Set<Vertex> visitedVertices = new HashSet<>();

        for (Vertex vertex : trace.getVertices()) {
            if (!visitedVertices.contains(vertex)) {
                Set<Vertex> connectedVertices = new HashSet<>();
                Set<Edge> connectedEdges = new HashSet<>();

                performDFS(vertex, trace, visitedVertices, connectedVertices, connectedEdges);

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

//    public List<Vertex> getGraphs(List<Span> spans) {
//        List<Vertex> trees = new ArrayList<>();
//        spanIdSet.clear();
//
//        Span beginSpan = spans.stream().filter(span -> span.getParentId() == null).toList().get(0);
//        beginSpan.setParentId("qewqeweqwqeweqw");
//        Span clientSpan = spans.stream().filter(span -> span.getKind() != null && span.getKind().equals("CLIENT")).toList().get(0);
//        spans.remove(clientSpan);
//
//        for (Span span : spans) {
//            mutateMap(span);
//            Vertex vertex = createGraph(span, spans);
//            if (vertex != null && !vertex.getEdges().isEmpty()) {
//                trees.add(vertex);
//            }
//        }
//
//        return trees;
//    }
//
//    private Vertex createGraph(Span span, List<Span> spans) {
//        Vertex root = new Vertex(span.getLocalEndpoint().getServiceName(), new ArrayList<>());
//        List<Span> spanList = spans.stream().filter(span1 -> span1.getParentId().equals(span.getSpanId())).toList();
//        for (Span childSpan : spanList) {
//            Vertex child = createGraph(childSpan, spans);
//            if (isValidEdge(childSpan) && !spanIdSet.contains(childSpan.getSpanId())) {
//                if (!span.getPath().equals(ModelConstants.DATABASE_NAME)) {
//                    spanIdSet.add(span.getSpanId());
//                }
//                root.addEdge(new Edge(childSpan.getPath(), childSpan.getTags().getMethod(), child));
//            }
//        }
//        return root;
//    }

    private boolean isValidEdge(Span span) {
        String method = span.getTags().getMethod();
        return method == null || (method.equals("GET") || method.equals("database"));
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
