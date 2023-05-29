package com.rowan.thesis.thesis_analysis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        List<List<Span>> input = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<List<Span>>>() {});
        Model expected = new Model(new ArrayList<>(Collections.singleton(getTrace())));

        Model actual = traceService.tracesToModel(input);

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private static Trace getTrace() {
        Node rootNode = new Node(
            "ts-travel-service",
            "get /api/v1/travelservice/routes/{tripid}",
            "GET",
                1684139555535039L,
            new ArrayList<>()
        );

        Node rootChildNode1 = new Node(
            "ts-travel-service",
                "database",
                null,
                1684139555537154L,
                new ArrayList<>()
        );

        Node rootChildNode2 = new Node(
                "ts-route-service",
                "get /api/v1/routeservice/routes/{routeid}",
                "GET",
                1684139555542078L,
                new ArrayList<>()
        );

        Node rootChildNode1Child = new Node(
                "ts-route-service",
                "database",
                null,
                1684139555544302L,
                new ArrayList<>()
        );

        rootChildNode2.setChildren(new ArrayList<>(Collections.singleton(rootChildNode1Child)));
        ArrayList<Node> rootChildren = new ArrayList<>();
        rootChildren.add(rootChildNode1);
        rootChildren.add(rootChildNode2);
        rootNode.setChildren(rootChildren);

        return new Trace(rootNode);
    }

}
