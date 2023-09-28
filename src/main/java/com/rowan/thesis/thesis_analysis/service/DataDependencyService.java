package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.metric.DataDependsNeedMetric;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsNeedScore;
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

    public Result getDataDependsScore(Model model) {
        results.clear();
        Map<String, Double> dataDependsReadMap = new HashMap<>();
        Map<String, Double> dataDependsWriteMap = new HashMap<>();
        Set<String> services = getServices(model);

        for (String service : services) {
            dataDependsReadMap.put(service, dataDepends(service, services, model.getReadTraces(), model.getReadEndpointMap(), DataDependsType.DATA_DEPENDS_READ, model.getClientRequests()));
            dataDependsWriteMap.put(service, dataDepends(service, services, model.getWriteTraces(), model.getWriteEndpointMap(), DataDependsType.DATA_DEPENDS_WRITE, model.getClientRequests()));
        }

        double dataDependsSystemRead = dataDependsReadMap.values().stream().mapToDouble(Double::doubleValue).sum();
        double dataDependsSystemWrite = dataDependsWriteMap.values().stream().mapToDouble(Double::doubleValue).sum();

        return new Result(dataDependsSystemRead, dataDependsSystemWrite, dataDependsReadMap, dataDependsWriteMap, new ArrayList<>(results), new ArrayList<>());
    }

    public Result getDataDependsReadScore(Model model) {
        results.clear();
        Set<String> services = getServices(model);
        Map<String, Double> dataDependsReadMap = new HashMap<>();
        for (String service : services) {
            dataDependsReadMap.put(service, dataDepends(service, services, model.getReadTraces(), model.getReadEndpointMap(), DataDependsType.DATA_DEPENDS_READ, model.getClientRequests()));
        }

        double dataDependsSystemRead = dataDependsReadMap.values().stream().mapToDouble(Double::doubleValue).sum();

        return new Result(dataDependsSystemRead, 0.0, dataDependsReadMap, new HashMap<>(), results, new ArrayList<>());
    }

    public Result getDataDependsWriteScore(Model model) {
        results.clear();
        Set<String> services = getServices(model);
        Map<String, Double> dataDependsWriteMap = new HashMap<>();
        for (String service : services) {
            dataDependsWriteMap.put(service, dataDepends(service, services, model.getWriteTraces(), model.getWriteEndpointMap(), DataDependsType.DATA_DEPENDS_WRITE, model.getClientRequests()));
        }

        double dataDependsSystemWrite = dataDependsWriteMap.values().stream().mapToDouble(Double::doubleValue).sum();


        return new Result(0.0, dataDependsSystemWrite, new HashMap<>(), dataDependsWriteMap, results, new ArrayList<>());
    }

    private double dataDepends(String serviceName, Set<String> services, List<Trace> traces, Map<String, Set<String>> map, DataDependsType dataDependsType, int clientRequests) {
        double result = 0;
        if (map.containsKey(serviceName)) {
            for (String path : map.get(serviceName)) {
                result += dataDependsOnEndpoint(serviceName, services, path, traces, dataDependsType, clientRequests);
            }
        }

        return result;
    }

    private double dataDependsOnEndpoint(String serviceName, Set<String> services, String endpoint, List<Trace> traces, DataDependsType dataDependsType, int clientRequests) {
        Map<String, Double> valuesPerService = new HashMap<>();

        double result = 0;
        for (String serviceCallee : services) {
            if (!serviceCallee.equals(serviceName)) {
                double value = reachableDependencies(serviceName, endpoint, traces, serviceCallee);
                if (value != 0) {
                    value = value / clientRequests;
                    valuesPerService.put(serviceCallee, value);
                    result += value;
                }
            }
        }

        results.add(new DataDependsMetric(dataDependsType, serviceName, endpoint, valuesPerService, result));

        return result;
    }
    private double reachableDependencies(String serviceName, String endpoint, List<Trace> traces, String serviceCallee) {
        int value = 0;
        for (Trace trace : traces) {
            Set<Vertex> vertices = findApplicableVertices(trace, serviceCallee, endpoint, serviceName);
            for (Vertex vertex : vertices) {
                int intraDataDependency = data(vertex, trace);
                Set<Vertex> vertexSet = path(vertex, trace);
                value += intraDataDependency * vertexSet.size();
            }
        }
        return value;
    }

    private Set<Vertex> findApplicableVertices(Trace trace, String sourceName, String endpoint, String targetName) {
        List<Edge> edges = trace.getEdges().stream().filter(edge -> edge.getSource().getName().equals(sourceName) &&
                                                                    edge.getEndpoint().equals(endpoint) &&
                                                                    edge.getTarget().getName().equals(targetName)).toList();

        return edges.stream().map(Edge::getTarget).collect(Collectors.toSet());
    }

    // Equivalent to data function from model
    private int data(Vertex vertex, Trace trace) {
        return trace.getEdges().stream().filter(edge -> edge.getSource().equals(vertex) && edge.getMethod().equals(ModelConstants.DATABASE_NAME)).toList().size();
    }

    // Equivalent to path function from model
    private Set<Vertex> path(Vertex startVertex, Trace trace) {
        Set<Vertex> visited = new HashSet<>();
        LinkedList<Vertex> path = new LinkedList<>();
        Set<Vertex> longestPath = new HashSet<>();

        longestPath = dfs(startVertex, trace, visited, path, longestPath);

        return longestPath.stream().filter(vertex -> !vertex.getName().equals(startVertex.getName())).collect(Collectors.toSet());
    }

    private Set<Vertex> dfs(Vertex currentVertex, Trace trace, Set<Vertex> visited, LinkedList<Vertex> path, Set<Vertex> longestPath) {
        visited.add(currentVertex);
        path.addLast(currentVertex);

        if (path.size() > longestPath.size()) {
            longestPath.clear();
            longestPath.addAll(path);
        }

        for (Edge edge : trace.getEdges()) {
            if (edge.getTarget().equals(currentVertex) && !visited.contains(edge.getSource())) {
                longestPath = dfs(edge.getSource(), trace, visited, path, longestPath);
            }
        }

//        path.removeLast();
//        visited.remove(currentVertex);
        return longestPath;
    }

    public List<DataDependsNeedMetric> calculateDataDependsNeedMetrics(Model model) {
        results.clear();
        Set<String> services = getServices(model);
        List<DataDependsNeedMetric> result = new ArrayList<>();
        for (String service : services) {
            result.add(getDataDependsNeedMetric(model.getReadTraces(), services, service, model.getReadEndpointMap(), DataDependsType.DATA_DEPENDS_READ));
            result.add(getDataDependsNeedMetric(model.getWriteTraces(), services, service, model.getWriteEndpointMap(), DataDependsType.DATA_DEPENDS_WRITE));
        }

        return result;
    }

    private DataDependsNeedMetric getDataDependsNeedMetric(List<Trace> traces, Set<String> services, String serviceName, Map<String, Set<String>> map, DataDependsType dataDependsType) {
        DataDependsNeedMetric dataDependsNeedMetric = new DataDependsNeedMetric(serviceName, dataDependsType, 0, new HashSet<>());
        int sum = 0;

        for (String service : services) {
            if (!service.equals(serviceName) && map.containsKey(service)) {
                for (String endpoint : map.get(service)) {
                    double result = 0.0;
                    for (Trace trace : traces) {
                        Set<Vertex> vertexSet = findApplicableVertices(trace, serviceName, endpoint, service);
                        for (Vertex vertex : vertexSet) {
                            result += data(vertex, trace);
                        }
                    }
                    if (result != 0) {
                        sum += result;
                        dataDependsNeedMetric.addScore(new DataDependsNeedScore(service, endpoint, result));
                    }
                }
            }
        }

        dataDependsNeedMetric.setSum(sum);

        if (sum != 0) {
            for (DataDependsNeedScore dataDependsNeedScore : dataDependsNeedMetric.getScores()) {
                int val = (int) dataDependsNeedScore.getValue();
                dataDependsNeedScore.setValue((double) val / sum);
            }
        }

        return dataDependsNeedMetric;
    }

    private Set<String> getServices(Model model) {
        Set<String> services = new HashSet<>();
        services.addAll(model.getReadTraces().stream()
                .flatMap(trace -> trace.getVertices().stream())
                .map(Vertex::getName)
                .collect(Collectors.toSet()));
        services.addAll(model.getWriteTraces().stream()
                .flatMap(trace -> trace.getVertices().stream())
                .map(Vertex::getName)
                .collect(Collectors.toSet()));
        services.remove(ModelConstants.DATABASE_NAME);

        return services;
    }

}