package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.metric.DataDependsType;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsMetric;
import com.rowan.thesis.thesis_analysis.model.metric.Result;
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
public class DataDependencyService {

    ArrayList<DataDependsMetric> results = new ArrayList<>();
    Set<String> services = new HashSet<>();
    final Set<String> readMethods = Set.of(ModelConstants.GET_STRING);
    final Set<String> writeMethods = Set.of(ModelConstants.POST_STRING, ModelConstants.PUT_STRING);


    public Result getDataDependsScore(Model model) {
        setLocalState(model);
        Map<String, Integer> dataDependsReadMap = new HashMap<>();
        Map<String, Integer> dataDependsWriteMap = new HashMap<>();

        for (String service : services) {
            if (model.getReadEndpointMap().containsKey(service)) {
                dataDependsReadMap.put(service, dataDependsRead(service, model));
            }
            if (model.getWriteEndpointMap().containsKey(service)) {
                dataDependsWriteMap.put(service, dataDependsWrite(service, model));
            }
        }

        return new Result(dataDependsReadMap, dataDependsWriteMap, results, null);
    }

    public Result getDataDependsReadScore(Model model) {
        setLocalState(model);
        Map<String, Integer> dataDependsReadMap = new HashMap<>();
        for (String service : model.getReadEndpointMap().keySet()) {
            dataDependsReadMap.put(service, dataDependsRead(service, model));
        }
        return new Result(dataDependsReadMap, null, results, null);
    }

    public Result getDataDependsWriteScore(Model model) {
        setLocalState(model);
        Map<String, Integer> dataDependsWriteMap = new HashMap<>();
        for (String service : model.getWriteEndpointMap().keySet()) {
            dataDependsWriteMap.put(service, dataDependsWrite(service, model));
        }

        return new Result(null, dataDependsWriteMap, results, null);
    }

    private int dataDependsRead(String serviceName, Model model) {
        int result = 0;
        for (String path : model.getReadEndpointMap().get(serviceName)) {
            result += dataDependsReadOnEndpoint(serviceName, path, model);
        }

        return result;
    }

    private int dataDependsWrite(String serviceName, Model model) {
        int result = 0;
        for (String path : model.getWriteEndpointMap().get(serviceName)) {
            result += dataDependsWriteOnEndpoint(serviceName, path, model);
        }

        return result;
    }

    private int dataDependsReadOnEndpoint(String serviceName, String endpoint, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();

        for (String serviceCallee : services) {
            reachableDependencies(serviceName, endpoint, model, valuesPerService, readMethods, serviceCallee);
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, serviceName, endpoint, result));

        return result;
    }

    private int dataDependsWriteOnEndpoint(String serviceName, String endpoint, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();

        for (String serviceCallee : services) {
            reachableDependencies(serviceName, endpoint, model, valuesPerService, writeMethods, serviceCallee);
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, serviceName, endpoint, result));

        return result;
    }

    private void reachableDependencies(String serviceName, String endpoint, Model model, ArrayList<Integer> valuesPerService, Set<String> methods, String serviceCallee) {
        if (!serviceCallee.equals(serviceName)) {
            int value = 0;
            for (Node node : model.getTraces()) {
                List<List<Node>> paths = new ArrayList<>();
                longestPaths(serviceName, serviceCallee, endpoint, node, methods, new ArrayList<>(), new ArrayList<>(), paths);
                for (List<Node> path : paths) {
                    // Retrieve the node for which we want to calculate the data dependency
                    Node targetNode = path.get(path.size() - 1);
                    // Filter out all vertices which equal the service for which we want to measure the dependency
                    path = path.stream().filter(node1 -> !node1.getName().equals(serviceName)).collect(Collectors.toList());
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
        // Database vertices can't be in the path
        if (currentPath.isEmpty() || (!node.getEndpoint().equals(ModelConstants.DATABASE_NAME) && methods.contains(node.getMethod()))) {
            currentPath.add(node);
        } else {
            currentPath.clear();
            currentPath.add(node);
        }

        // Check if the current node matches the target and endpoint, and the path has at least two vertices
        if (node.getEndpoint().equals(endpoint) && node.getName().equals(target) && currentPath.size() >= 2 && currentPath.get(currentPath.size() - 2).getName().equals(source)) {
            longestPath.clear();
            longestPath.addAll(currentPath);
            result.add(new ArrayList<>(longestPath)); // Add a copy of the longest path to the result list
        }

        for (Node childNode : node.getChildren()) {
            if (!childNode.getEndpoint().equals(ModelConstants.DATABASE_NAME)) {
                List<Node> currentPathCopy = currentPath;
                longestPaths(target, source, endpoint, childNode, methods, currentPathCopy, longestPath, result);
            }
        }

        currentPath.remove(node);
    }

    private int intraDataDependency(Node node) {
        return node.getChildren().stream().filter(child -> child.getEndpoint().equals(ModelConstants.DATABASE_NAME)).toList().size();
    }

    private void setLocalState(Model model) {
        results.clear();
        services.clear();
        services.addAll(model.getReadEndpointMap().keySet());
        services.addAll(model.getWriteEndpointMap().keySet());
    }

}