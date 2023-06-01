package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
        List<Trace> trees = new ArrayList<>();
        readEndpointMap.clear();
        writeEndpointMap.clear();
        for (List<Span> spans : traces) {
            trees.add(traceToTree(spans));
        }

        return new Model(trees);
    }

    private Trace traceToTree(List<Span> spans) {
        Span beginSpan = spans.stream().filter(span -> span.getParentId() == null).toList().get(0);
        spans.remove(beginSpan);
        mutateMap(beginSpan);
        Node beginNode = new Node(
                beginSpan.getLocalEndpoint().getServiceName(),
                beginSpan.getPath(),
                ModelConstants.ROOT_METHOD_STRING,
                beginSpan.getTimeStamp(),
                null
        );
        beginNode.setChildren(traceToTreeRec(beginSpan.getSpanId(), spans));
        beginNode.setChildren(beginNode.getChildren().stream().filter(node -> (
                node.getEndpoint().equals(ModelConstants.DATABASE_NAME)
                || !node.getName().equals(beginNode.getName()))).collect(Collectors.toList()));

        return new Trace(beginNode);
    }

    private List<Node> traceToTreeRec(String id, List<Span> spans) {
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
                    span.getTimeStamp(),
                    null);
            List<Node> children = traceToTreeRec(span.getSpanId(), spans);
            if (children != null && children.size() >= 1) {
                children.sort(Comparator.comparing(Node::getTimeStamp));
            }
            node.setChildren(children);

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

}
