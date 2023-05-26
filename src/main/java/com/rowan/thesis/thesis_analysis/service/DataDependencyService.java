package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.metric.DataDependsType;
import com.rowan.thesis.thesis_analysis.model.metric.Metric;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.input.Span;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataDependencyService {

    ArrayList<Metric> results = new ArrayList<>();

    TraceService traceService = new TraceService();

    public void getDataAutonomyScore(List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        Map<String, Integer> dataDependsReadMap = new HashMap<>();
        Map<String, Integer> dataDependsWriteMap = new HashMap<>();
        for (String service :traceService.getReadEndpointMap().keySet()) {
            dataDependsReadMap.put(service, dataDependsRead(service, model));
        }
        for (String service : traceService.getWriteEndpointMap().keySet()) {
            dataDependsWriteMap.put(service, dataDependsWrite(service, model));
        }
        log.info("EOP");
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

    private int dataDependsNeed(String serviceName, Model model) {
        int result = 0;
        for (String path : traceService.getWriteEndpointMap().get(serviceName)) {
            result += dataDependsWriteOnEndpoint(serviceName, path, model);
        }

        return result;
    }

    private int dataDependsReadOnEndpoint(String serviceName, String endpoint, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();
        Set<String> methods = new HashSet<>(Collections.singleton(ModelConstants.GET_STRING));

        for (String serviceCallee : traceService.getReadEndpointMap().keySet()) {
            if (!serviceCallee.equals(serviceName)) {
                int value = 0;
                for (Trace trace : model.getTraces()) {
                    List<List<Node>> paths = new ArrayList<>();
                    longestPath(serviceName, serviceCallee, endpoint, trace.getNode(), methods, new ArrayList<>(), new ArrayList<>(), paths);
                    for (List<Node> path : paths) {
                        if (path.size() > 1) {
                            value += (path.size() - 1) * intraDataDependency(path.get(path.size() - 1));
                        }
                    }
                }
                valuesPerService.add(value);
            }
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new Metric(DataDependsType.DATA_DEPENDS_READ, serviceName, endpoint, result));

        return  result;
    }

    private int dataDependsWriteOnEndpoint(String serviceName, String endpoint, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();
        Set<String> methods = new HashSet<>();
        methods.add(ModelConstants.POST_STRING);
        methods.add(ModelConstants.PUT_STRING);

        for (String serviceCallee : traceService.getWriteEndpointMap().keySet()) {
            if (!serviceCallee.equals(serviceName)) {
                int value = 0;
                for (Trace trace : model.getTraces()) {
                    List<List<Node>> paths = new ArrayList<>();
                    longestPath(serviceName, serviceCallee, endpoint, trace.getNode(), methods, new ArrayList<>(), new ArrayList<>(), paths);
                    for (List<Node> path : paths) {
                        if (path.size() > 1) {
                            value += (path.size() - 1) * intraDataDependency(path.get(path.size() - 1));
                        }
                    }
                }
                valuesPerService.add(value);
            }
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new Metric(DataDependsType.DATA_DEPENDS_WRITE, serviceName, endpoint, result));

        return  result;
    }

    private int dataDependsNeedOnEndpoint(String serviceName, String endpoint, Model model) {
        ArrayList<Integer> valuesPerService = new ArrayList<>();
        Set<String> methods = new HashSet<>();
        methods.add(ModelConstants.POST_STRING);
        methods.add(ModelConstants.PUT_STRING);

        for (String serviceCallee : traceService.getWriteEndpointMap().keySet()) {
            if (!serviceCallee.equals(serviceName)) {
                int value = 0;
                for (Trace trace : model.getTraces()) {
                    List<List<Node>> paths = new ArrayList<>();
                    longestPath(serviceName, serviceCallee, endpoint, trace.getNode(), methods, new ArrayList<>(), new ArrayList<>(), paths);
                    for (List<Node> path : paths) {
                        if (path.size() > 1) {
                            value += (path.size() - 1) * intraDataDependency(path.get(path.size() - 1));
                        }
                    }
                }
                valuesPerService.add(value);
            }
        }

        int result = euclidianNorm(valuesPerService);
        results.add(new Metric(DataDependsType.DATA_DEPENDS_NEED, serviceName, endpoint, result));

        return  result;
    }

    private int euclidianNorm(ArrayList<Integer> values) {
        int sum = 0;

        for (int value : values) {
            sum += value * value;
        }

        return (int) Math.sqrt(sum);
    }

    private void longestPath(String serviceName, String serviceCalleeName, String endpoint, Node node,
                             Set<String> methods, List<Node> currentPath, List<Node> longestPath, List<List<Node>> result) {
        currentPath.add(node);

        if (node.getName().equals(serviceName) && node.getEndpoint().equals(endpoint)
            && currentPath.size() >= 2
            && currentPath.get(currentPath.size() - 2).getName().equals(serviceCalleeName)) {
            longestPath.clear();
            longestPath.addAll(currentPath);
            result.add(longestPath);
        }

        for (Node childNode : node.getChildren()) {
            if (!childNode.getName().equals(ModelConstants.DATABASE_NAME) || !methods.contains(childNode.getMethod())) {
                longestPath(serviceName, serviceCalleeName, endpoint, childNode, methods, currentPath, longestPath, result);
            }
        }

        currentPath.remove(node);
    }

    private int intraDataDependency(Node node) {
        return node.getChildren().stream().filter(child -> child.getEndpoint().equals(ModelConstants.DATABASE_NAME)).toList().size();
    }

}