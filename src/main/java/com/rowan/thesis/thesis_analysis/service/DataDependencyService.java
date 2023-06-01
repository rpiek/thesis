package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.metric.DataDependsType;
import com.rowan.thesis.thesis_analysis.model.metric.Metric;
import com.rowan.thesis.thesis_analysis.model.metric.Result;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
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
public class DataDependencyService {

    ArrayList<Metric> results = new ArrayList<>();
    Set<String> services = new HashSet<>();

    private final TraceService traceService;

    public DataDependencyService(TraceService traceService) {
        this.traceService = traceService;
    }

    public Result getDataDependsScore(Model model) {
        setLocalState();
        Map<String, Integer> dataDependsReadMap = new HashMap<>();
        Map<String, Integer> dataDependsWriteMap = new HashMap<>();

        for (String service : services) {
            if (traceService.getReadEndpointMap().containsKey(service)) {
                dataDependsReadMap.put(service, dataDependsRead(service, model));
            } else {
                dataDependsReadMap.put(service, 0);
            }
            if (traceService.getWriteEndpointMap().containsKey(service)) {
                dataDependsWriteMap.put(service, dataDependsWrite(service, model));
            } else {
                dataDependsWriteMap.put(service, 0);
            }
        }

        return new Result(dataDependsReadMap, dataDependsWriteMap, results);
    }

    public Result getDataDependsReadScore(Model model) {
        setLocalState();
        Map<String, Integer> dataDependsReadMap = new HashMap<>();
        for (String service : traceService.getReadEndpointMap().keySet()) {
            dataDependsReadMap.put(service, dataDependsRead(service, model));
        }
        return new Result(dataDependsReadMap, null, results);
    }

    public Result getDataDependsWriteScore(Model model) {
        setLocalState();
        Map<String, Integer> dataDependsWriteMap = new HashMap<>();
        for (String service : traceService.getWriteEndpointMap().keySet()) {
            dataDependsWriteMap.put(service, dataDependsWrite(service, model));
        }

        return new Result(null, dataDependsWriteMap, results);
    }

    private int dataDependsRead(String serviceName, Model model) {
        int result = 0;
        for (String path : traceService.getReadEndpointMap().get(serviceName)) {
            result += dataDependsReadOnEndpoint(serviceName, path, model);
        }

        return result;
    }

    private int dataDependsWrite(String serviceName, Model model) {
        int result = 0;
        for (String path : traceService.getWriteEndpointMap().get(serviceName)) {
            result += dataDependsWriteOnEndpoint(serviceName, path, model);
        }

        return result;
    }

    private int dataDependsReadOnEndpoint(String serviceName, String endpoint, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();
        Set<String> methods = new HashSet<>(Collections.singleton(ModelConstants.GET_STRING));

        for (String serviceCallee : services) {
            reachableReadsOrWrites(serviceName, endpoint, model, valuesPerService, methods, serviceCallee);
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new Metric(DataDependsType.DATA_DEPENDS_READ, serviceName, endpoint, result));

        return result;
    }

    private int dataDependsWriteOnEndpoint(String serviceName, String endpoint, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();
        Set<String> methods = new HashSet<>();
        methods.add(ModelConstants.POST_STRING);
        methods.add(ModelConstants.PUT_STRING);

        for (String serviceCallee : services) {
            reachableReadsOrWrites(serviceName, endpoint, model, valuesPerService, methods, serviceCallee);
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new Metric(DataDependsType.DATA_DEPENDS_WRITE, serviceName, endpoint, result));

        return result;
    }

    private void reachableReadsOrWrites(String serviceName, String endpoint, Model model, ArrayList<Integer> valuesPerService, Set<String> methods, String serviceCallee) {
        if (!serviceCallee.equals(serviceName)) {
            int value = 0;
            for (Trace trace : model.getTraces()) {
                List<List<Node>> paths = new ArrayList<>();
                longestPaths(serviceName, serviceCallee, endpoint, trace.getNode(), methods, new ArrayList<>(), new ArrayList<>(), paths);
                for (List<Node> path : paths) {
                    // Retrieve the node for which we want to calculate the data dependency
                    Node targetNode = path.get(path.size() - 1);
                    path.remove(path.size() - 1);
                    // Filter out all vertices which equal the service for which we want to measure the dependency
                    path = path.stream().filter(node -> !node.getName().equals(serviceName)).collect(Collectors.toList());
                    if (!path.isEmpty()) {
                        value += path.size() * intraDataDependency(targetNode);
                    }
                }
            }
            valuesPerService.add(value);
        }
    }

    private int euclidianNorm(ArrayList<Integer> values) {
        int sum = 0;

        for (int value : values) {
            sum += value * value;
        }

        return (int) Math.sqrt(sum);
    }

    private void longestPaths(String target, String source, String endpoint, Node node, Set<String> methods, List<Node> currentPath, List<Node> longestPath, List<List<Node>> result) {

        if (node.getMethod() != null && (node.getMethod().equals(ModelConstants.ROOT_METHOD_STRING) || methods.contains(node.getMethod()))) {
            currentPath.add(node);
        }

        if (node.getName().equals(target) && node.getEndpoint().equals(endpoint) && currentPath.size() >= 2 && currentPath.get(currentPath.size() - 2).getName().equals(source)) {
            longestPath.clear();
            longestPath.addAll(currentPath);
            result.add(longestPath);
        }

        for (Node childNode : node.getChildren()) {
            if (!childNode.getName().equals(ModelConstants.DATABASE_NAME) || !methods.contains(childNode.getMethod())) {
                longestPaths(target, source, endpoint, childNode, methods, currentPath, longestPath, result);
            }
        }

        currentPath.remove(node);
    }

    private int intraDataDependency(Node node) {
        return node.getChildren().stream().filter(child -> child.getEndpoint().equals(ModelConstants.DATABASE_NAME)).toList().size();
    }

    private void setLocalState() {
        results.clear();
        services.clear();
        services.addAll(traceService.getReadEndpointMap().keySet());
        services.addAll(traceService.getWriteEndpointMap().keySet());
    }

}