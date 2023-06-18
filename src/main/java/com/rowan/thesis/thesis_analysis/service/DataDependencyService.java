package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.metric.DataDependsType;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsMetric;
import com.rowan.thesis.thesis_analysis.model.metric.Result;
import com.rowan.thesis.thesis_analysis.model.trace.Edge;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
import com.rowan.thesis.thesis_analysis.model.trace.Vertex;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DataDependencyService {

    ArrayList<DataDependsMetric> results = new ArrayList<>();
    Set<String> services = new HashSet<>();

    public Result getDataDependsScore(Model model) {
        setLocalState(model);
        Map<String, Double> dataDependsReadMap = new HashMap<>();
        Map<String, Double> dataDependsWriteMap = new HashMap<>();

        for (String service : services) {
            dataDependsReadMap.put(service, dataDepends(service, model.getReadTraces(), model.getReadEndpointMap(), DataDependsType.DATA_DEPENDS_READ));
            dataDependsWriteMap.put(service, dataDepends(service, model.getWriteTraces(), model.getWriteEndpointMap(), DataDependsType.DATA_DEPENDS_WRITE));
        }

        return new Result(dataDependsReadMap, dataDependsWriteMap, results, new ArrayList<>());
    }

    public Result getDataDependsReadScore(Model model) {
        setLocalState(model);
        Map<String, Double> dataDependsReadMap = new HashMap<>();
        for (String service : services) {
            dataDependsReadMap.put(service, dataDepends(service, model.getReadTraces(), model.getReadEndpointMap(), DataDependsType.DATA_DEPENDS_READ));
        }
        return new Result(dataDependsReadMap, new HashMap<>(), results, new ArrayList<>());
    }

    public Result getDataDependsWriteScore(Model model) {
        setLocalState(model);
        Map<String, Double> dataDependsWriteMap = new HashMap<>();
        for (String service : services) {
            dataDependsWriteMap.put(service, dataDepends(service, model.getWriteTraces(), model.getWriteEndpointMap(), DataDependsType.DATA_DEPENDS_WRITE));
        }
        return new Result(new HashMap<>(), dataDependsWriteMap, results, new ArrayList<>());
    }

    private double dataDepends(String serviceName, List<Trace> traces, Map<String, Set<String>> map, DataDependsType dataDependsType) {
        double result = 0;
        if (map.containsKey(serviceName)) {
            for (String path : map.get(serviceName)) {
                result += dataDependsOnEndpoint(serviceName, path, traces, dataDependsType);
            }
        }

        return result;
    }

    private double dataDependsOnEndpoint(String serviceName, String endpoint, List<Trace> traces, DataDependsType dataDependsType) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();

        for (String serviceCallee : services) {
            if (!serviceCallee.equals(serviceName)) {
                int value = reachableDependencies(serviceName, endpoint, traces, valuesPerService, serviceCallee);
                valuesPerService.add(value);
            }
        }

        double result = euclidianNorm(valuesPerService);
        results.add(new DataDependsMetric(dataDependsType, serviceName, endpoint, result));

        return result;
    }
    private int reachableDependencies(String serviceName, String endpoint, List<Trace> traces, ArrayList<Integer> valuesPerService, String serviceCallee) {
        int value = 0;
        for (Trace trace : traces) {
            List<Vertex> vertices = findApplicableVertices(trace, serviceCallee, endpoint, serviceName);
            for (Vertex vertex : vertices) {
                int intraDataDependency = intraDataDependency(vertex, trace);
                Set<Vertex> vertexSet = findLongestPath(vertex, trace);
                value += intraDataDependency * vertexSet.size();
            }
        }
        return value;
    }

    private List<Vertex> findApplicableVertices(Trace trace, String sourceName, String endpoint, String targetName) {
        List<Edge> edges = trace.getEdges().stream().filter(edge -> edge.getSource().getName().equals(sourceName) &&
                                                                    edge.getEndpoint().equals(endpoint) &&
                                                                    edge.getTarget().getName().equals(targetName)).toList();

        return edges.stream().map(Edge::getTarget).collect(Collectors.toList());
    }

    private double euclidianNorm(ArrayList<Integer> values) {
        double sum = 0.0;

        for (int value : values) {
            sum += value * value;
        }

        return Math.sqrt(sum);
    }

    private int intraDataDependency(Vertex vertex, Trace trace) {
        return trace.getEdges().stream().filter(edge -> edge.getSource().equals(vertex) && edge.getMethod().equals(ModelConstants.DATABASE_NAME)).toList().size();
    }

    public static Set<Vertex> findLongestPath(Vertex startVertex, Trace trace) {
        Set<Vertex> visited = new HashSet<>();
        LinkedList<Vertex> path = new LinkedList<>();
        Set<Vertex> longestPath = new HashSet<>();

        dfs(startVertex, trace, visited, path, longestPath);

        return longestPath.stream().filter(vertex -> !vertex.getName().equals(startVertex.getName())).collect(Collectors.toSet());
    }

    private static void dfs(Vertex currentVertex, Trace trace, Set<Vertex> visited, LinkedList<Vertex> path, Set<Vertex> longestPath) {
        visited.add(currentVertex);
        path.addLast(currentVertex);

        if (path.size() > longestPath.size()) {
            longestPath.clear();
            longestPath.addAll(path);
        }

        for (Edge edge : trace.getEdges()) {
            if (edge.getTarget().equals(currentVertex) && !visited.contains(edge.getSource())) {
                dfs(edge.getSource(), trace, visited, path, longestPath);
            }
        }

        path.removeLast();
        visited.remove(currentVertex);
    }

    private void setLocalState(Model model) {
        results.clear();
        services.clear();
        services.addAll(model.getReadTraces().stream()
                .flatMap(trace -> trace.getVertices().stream())
                .map(Vertex::getName)
                .collect(Collectors.toSet()));
        services.addAll(model.getWriteTraces().stream()
                .flatMap(trace -> trace.getVertices().stream())
                .map(Vertex::getName)
                .collect(Collectors.toSet()));
        if (services.contains(ModelConstants.DATABASE_NAME)) {
            services.remove(ModelConstants.DATABASE_NAME);
        }
    }

}