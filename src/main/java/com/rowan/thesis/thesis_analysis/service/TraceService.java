package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.tree.Model;
import com.rowan.thesis.thesis_analysis.model.tree.Node;
import com.rowan.thesis.thesis_analysis.model.tree.Tree;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TraceService {

    private final Map<String, Set<String>> readEndpointMap = new HashMap<>();
    private final Map<String, Set<String>> writeEndpointMap = new HashMap<>();

    public Map<String, Set<String>> getReadEndpointMap() {
        return readEndpointMap;
    }

    public Map<String, Set<String>> getWriteEndpointMap() {
        return writeEndpointMap;
    }

    public Model tracesToModel(List<List<Span>> traces) {
        List<Tree> trees = new ArrayList<>();
        readEndpointMap.clear();
        writeEndpointMap.clear();
        for (List<Span> spans : traces) {
            trees.add(traceToTree(spans));
        }

        return new Model(trees);
    }

    private Tree traceToTree(List<Span> spans) {
        Span beginSpan = spans.stream().filter(span -> span.getParentId() == null).toList().get(0);
        spans.remove(beginSpan);
        mutateMap(beginSpan);
        Node beginNode = spanToNode(beginSpan);
        beginNode.setChildren(traceToTreeRec(beginSpan.getSpanId(), spans, beginNode));
        beginNode.setChildren(beginNode.getChildren().stream().filter(node -> (
                node.getEndpoint().equals(ModelConstants.DATABASE_NAME)
                || !node.getName().equals(beginNode.getName()))).collect(Collectors.toList()));

        return new Tree(beginNode);
    }

    private List<Node> traceToTreeRec(String id, List<Span> spans, Node parent) {
        if (spans.isEmpty()) {
            return null;
        }

        List<Span> childSpans = spans.stream().filter(span -> span.getParentId().equals(id)).toList();
        List<Node> nodes = new ArrayList<>();
        for (Span span : childSpans) {
            if (span.getKind() == null || !span.getKind().equals("CLIENT")) {
                mutateMap(span);
            }
            Node node = new Node(
                    span.getLocalEndpoint().getServiceName(),
                    span.getPath(),
                    span.getTags().getMethod(),
                    null,
                    parent);
            node.setChildren(traceToTreeRec(span.getSpanId(), spans, node));
            nodes.add(node);
        }

        return nodes;
    }

    private void mutateMap(Span span) {
        String serviceName = span.getLocalEndpoint().getServiceName();
        String[] strings = span.getPath().split(" ");
        if (strings[0].equals(ModelConstants.GET_STRING)) {
            fillMap(span, serviceName, readEndpointMap);
        } else if (strings[0].equals(ModelConstants.POST_STRING) || strings[0].equals(ModelConstants.PUT_STRING)) {
            fillMap(span, serviceName, writeEndpointMap);
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

    private Node spanToNode(Span span) {
        return new Node(
                span.getLocalEndpoint().getServiceName(),
                span.getPath(),
                span.getTags().getMethod(),
                null,
                null
        );
    }

}
