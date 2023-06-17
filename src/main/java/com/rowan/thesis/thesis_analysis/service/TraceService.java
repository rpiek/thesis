package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
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

    private final Map<String, Set<String>> readEndpointMap = new HashMap<>();
    private final Map<String, Set<String>> writeEndpointMap = new HashMap<>();

    public Model tracesToModel(List<List<Span>> traces) {
        List<Node> nodes = new ArrayList<>();
        readEndpointMap.clear();
        writeEndpointMap.clear();
        for (List<Span> spans : traces) {
            nodes.add(traceToTree(spans));
        }

        return new Model(nodes, readEndpointMap, writeEndpointMap);
    }

    private Node traceToTree(List<Span> spans) {
        Span beginSpan = spans.stream().filter(span -> span.getParentId() == null).toList().get(0);
        spans.remove(beginSpan);
        mutateMap(beginSpan);
        Node beginNode = spanToNode(beginSpan);
        beginNode.setMethod(ModelConstants.ROOT_METHOD_STRING);
        beginNode.setChildren(traceToTreeRecursive(beginSpan.getSpanId(), spans));
        beginNode.setChildren(beginNode.getChildren().stream().filter(node -> (node.getEndpoint().equals(ModelConstants.DATABASE_NAME) || !node.getName().equals(beginNode.getName()))).collect(Collectors.toList()));

        return beginNode;
    }

    private List<Node> traceToTreeRecursive(String id, List<Span> spans) {
        if (spans.isEmpty()) {
            return null;
        }

        List<Span> childSpans = spans.stream().filter(span -> span.getParentId().equals(id)).toList();
        List<Node> nodes = new ArrayList<>();
        for (Span span : childSpans) {
            if (span.getKind() != null && !span.getKind().equals("CLIENT")) {
                mutateMap(span);
            }
            Node node = spanToNode(span);
            List<Node> children = traceToTreeRecursive(span.getSpanId(), spans);
            node.setChildren(children);

            nodes.add(node);
        }

        return nodes;
    }

    private void mutateMap(Span span) {
        if (span.getTags().getMethod() != null) {
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

    private Node spanToNode(Span span) {
        return new Node(span.getLocalEndpoint().getServiceName(), span.getPath(), span.getTags().getMethod(),null);
    }

}
