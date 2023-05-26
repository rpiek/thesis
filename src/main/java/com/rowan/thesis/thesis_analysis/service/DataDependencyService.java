package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.metric.DataDependsType;
import com.rowan.thesis.thesis_analysis.model.metric.Metric;
import com.rowan.thesis.thesis_analysis.model.tree.Model;
import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.tree.Node;
import com.rowan.thesis.thesis_analysis.model.tree.Tree;
import java.util.ArrayList;
import java.util.Collections;
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
public class DataDependencyService {

    Map<String, Set<String>> readEndpointMap = new HashMap<>();
    Map<String, Set<String>> writeEndpointMap = new HashMap<>();
    ArrayList<Metric> results = new ArrayList<>();

    final String DATABASE_NAME = "database";
    final String GET_STRING = "get";
    final String POST_STRING = "post";
    final String PUT_STRING = "put";

    public void getDataAutonomyScore(List<List<Span>> traces) {
        Model model = tracesToModel(traces);
        Map<String, Integer> dataDependsReadMap = new HashMap<String, Integer>();
        Map<String, Integer> dataDependsWriteMap = new HashMap<String, Integer>();
        for (String service : readEndpointMap.keySet()) {
            dataDependsReadMap.put(service, dataDependsRead(service, model));
        }
        for (String service : writeEndpointMap.keySet()) {
            dataDependsWriteMap.put(service, dataDependsWrite(service, model));
        }
        log.info("EOP");
    }

    private int dataDependsRead(String serviceName, Model model) {
        int result = 0;
        for (String path : readEndpointMap.get(serviceName)) {
            result += dataDependsReadOnEndpoint(serviceName, path, model);
        }

        return result;
    }

    private int dataDependsWrite(String serviceName, Model model) {
        int result = 0;
        for (String path : writeEndpointMap.get(serviceName)) {
            result += dataDependsWriteOnEndpoint(serviceName, path, model);
        }

        return result;
    }

    private int dataDependsReadOnEndpoint(String serviceName, String path, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();

        for (String serviceCallee : readEndpointMap.keySet()) {
            if (!serviceCallee.equals(serviceName)) {
            int value = 0;
            for (Tree tree : model.getTrees()) {
                List<Node> longestPath = new ArrayList<>();
                findLongestPath(serviceName, serviceCallee, path, tree.getNode(), new HashSet<>(Collections.singleton(GET_STRING)), new ArrayList<>(), longestPath);
                if (longestPath.size() > 1) {
                    value += longestPath.size() - 1;
                }
            }
                valuesPerService.add(value);
            }
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new Metric(DataDependsType.DATA_DEPENDS_READ, serviceName, path, result));

        return  result;
    }

    private int dataDependsWriteOnEndpoint(String serviceName, String path, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();
        Set<String> methods = new HashSet<>();
        methods.add(POST_STRING);
        methods.add(PUT_STRING);

        for (String serviceCallee : writeEndpointMap.keySet()) {
            if (!serviceCallee.equals(serviceName)) {
                int value = 0;
                for (Tree tree : model.getTrees()) {
                    List<Node> longestPath = new ArrayList<>();
                    findLongestPath(serviceName, serviceCallee, path, tree.getNode(), methods, new ArrayList<>(), longestPath);
                    if (longestPath.size() > 1) {
                        value += longestPath.size() - 1;
                    }
                }
                valuesPerService.add(value);
            }
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new Metric(DataDependsType.DATA_DEPENDS_WRITE, serviceName, path, result));

        return  result;
    }

    private int euclidianNorm(ArrayList<Integer> values) {
        int sum = 0;

        for (int value : values) {
            sum += value * value;
        }

        return (int) Math.sqrt(sum);
    }

    private void findLongestPath(String serviceName, String serviceCalleename, String path, Node node,
                                 Set<String> methods, List<Node> currentPath, List<Node> longestPath) {
        currentPath.add(node);

        if (node.getName().equals(serviceName) && node.getEndpoint().equals(path)
            && currentPath.size() >= 2
            && currentPath.get(currentPath.size() - 2).getName().equals(serviceCalleename)) {
            longestPath.clear();
            longestPath.addAll(currentPath);
        }

        for (Node childNode : node.getChildren()) {
            if (!childNode.getName().equals(DATABASE_NAME) || !methods.contains(childNode.getMethod())) {
                findLongestPath(serviceName, serviceCalleename, path, childNode, methods, currentPath, longestPath);
            }
        }

        currentPath.remove(node);
    }

    private Model tracesToModel(List<List<Span>> traces) {
        List<Tree> trees = new ArrayList<>();
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
        beginNode.setChildren(beginNode.getChildren().stream().filter(node -> (node.getEndpoint().equals(DATABASE_NAME)
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
            nodes.add(new Node(
                    span.getLocalEndpoint().getServiceName(),
                    span.getPath(),
                    span.getTags().getMethod(),
                    traceToTreeRec(span.getSpanId(), spans, parent),
                    parent));
        }

        return nodes;
    }

    private void mutateMap(Span span) {
        String serviceName = span.getLocalEndpoint().getServiceName();
        String[] strings = span.getPath().split(" ");
        if (strings[0].equals(GET_STRING)) {
            fillMap(span, serviceName, readEndpointMap);
        } else if (strings[0].equals(POST_STRING) || strings[0].equals(PUT_STRING)) {
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