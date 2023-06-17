package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Edge;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.model.trace.Vertex;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
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

    private final Set<String> spanIdSet = new HashSet<>();

    public Model tracesToModel(List<List<Span>> traces) {
        List<Vertex> vertices = new ArrayList<>();
        readEndpointMap.clear();
        writeEndpointMap.clear();
        for (List<Span> spans : traces) {
            vertices.add(getTree(spans));
        }

        return new Model(vertices, readEndpointMap, writeEndpointMap);
    }

    public Vertex getTree(List<Span> spans) {
        Span beginSpan = spans.stream().filter(span -> span.getParentId() == null).toList().get(0);
        Span clientSpan = spans.stream().filter(span -> span.getKind() != null && span.getKind().equals("CLIENT")).toList().get(0);
        spans.remove(beginSpan);
        spans.remove(clientSpan);
        Vertex root = new Vertex(beginSpan.getLocalEndpoint().getServiceName(), new ArrayList<>());
        List<Span> spanList = spans.stream().filter(span -> span.getParentId().equals(beginSpan.getSpanId())).toList();
        for (Span span : spanList) {
            Vertex child = createTree(span, spans);
            if (span.getPath().equals(ModelConstants.DATABASE_NAME)) {
                root.addEdge(new Edge(ModelConstants.DATABASE_NAME, ModelConstants.DATABASE_NAME, child));
            } else {
                root.addEdge(new Edge(span.getPath(), span.getTags().getMethod(), child));
            }
        }

        return root;
    }

    private Vertex createTree(Span span, List<Span> spans) {
        Vertex root = new Vertex(span.getLocalEndpoint().getServiceName(), new ArrayList<>());
        List<Span> spanList = spans.stream().filter(span1 -> span1.getParentId().equals(span.getSpanId())).toList();
        mutateMap(span);
        for (Span childSpan : spanList) {
            Vertex child = createTree(childSpan, spans);
            if (childSpan.getPath().equals(ModelConstants.DATABASE_NAME)) {
                root.addEdge(new Edge(ModelConstants.DATABASE_NAME, ModelConstants.DATABASE_NAME, child));
            } else {
                root.addEdge(new Edge(childSpan.getPath(), childSpan.getTags().getMethod(), child));
            }
        }
        return root;
    }

    public List<Vertex> getSubGraphs(Vertex vertex, Set<String> methods) {
        List<Vertex> result = new ArrayList<>();

        

        return result;
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
