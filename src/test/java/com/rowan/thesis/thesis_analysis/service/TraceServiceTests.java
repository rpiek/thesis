package com.rowan.thesis.thesis_analysis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Edge;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.model.trace.Vertex;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
public class TraceServiceTests {

    private final TraceService traceService = new TraceService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void Convert_input_to_trace() throws IOException {
        ClassPathResource resource = new ClassPathResource("/TraceServiceTests/example_traces_1.json");
        List<List<Span>> input = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        HashMap<String, Set<String>> readEndpointMap = new HashMap<>();
        readEndpointMap.put("ts-route-service", new HashSet<>(Collections.singleton("get /api/v1/routeservice/routes/{routeid}")));
        Model expected = new Model(new ArrayList<>(Collections.singleton(getTrace())), readEndpointMap, new HashMap<>());

        Model actual = traceService.tracesToModel(input);

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private static Vertex getTrace() {

        Vertex root = new Vertex("ts-travel-service", new ArrayList<>());
        Vertex databaseRoot = new Vertex("ts-travel-service", new ArrayList<>());

        Vertex vertex1 = new Vertex("ts-route-service", new ArrayList<>());
        Vertex databaseVertex1 = new Vertex("ts-route-service", new ArrayList<>());

        root.addEdge(new Edge("database", "database", databaseRoot));
        root.addEdge(new Edge("get /api/v1/routeservice/routes/{routeid}", "GET", vertex1));
        vertex1.addEdge(new Edge("database", "database", databaseVertex1));

        return root;
    }

}
