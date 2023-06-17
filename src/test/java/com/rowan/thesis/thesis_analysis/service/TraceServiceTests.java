package com.rowan.thesis.thesis_analysis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
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
        readEndpointMap.put("ts-travel-service", new HashSet<>(Collections.singleton("get /api/v1/travelservice/routes/{tripid}")));
        readEndpointMap.put("ts-route-service", new HashSet<>(Collections.singleton("get /api/v1/routeservice/routes/{routeid}")));
        Model expected = new Model(new ArrayList<>(Collections.singleton(getTrace())), readEndpointMap, new HashMap<>());

        Model actual = traceService.tracesToModel(input);

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private static Node getTrace() {
        Node rootNode = new Node(
            "ts-travel-service",
            "get /api/v1/travelservice/routes/{tripid}",
                ModelConstants.ROOT_METHOD_STRING,
            new ArrayList<>()
        );

        Node rootChildNode1 = new Node(
            "ts-travel-service",
                "database",
                null,
                new ArrayList<>()
        );

        Node rootChildNode2 = new Node(
                "ts-route-service",
                "get /api/v1/routeservice/routes/{routeid}",
                ModelConstants.GET_STRING,
                new ArrayList<>()
        );

        Node rootChildNode1Child = new Node(
                "ts-route-service",
                "database",
                null,
                new ArrayList<>()
        );

        rootChildNode2.setChildren(new ArrayList<>(Collections.singleton(rootChildNode1Child)));
        rootNode.addChild(rootChildNode1);
        rootNode.addChild(rootChildNode2);

        return rootNode;
    }

}
