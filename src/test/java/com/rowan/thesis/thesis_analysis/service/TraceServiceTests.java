package com.rowan.thesis.thesis_analysis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Edge;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
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
        Model expected = new Model(new ArrayList<>(Collections.singleton(getReadTraces())), new ArrayList<>(), readEndpointMap, new HashMap<>(), 1);

        Model actual = traceService.tracesToModel(input);

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private static Trace getReadTraces() {
        Set<Edge> edgeSet = new HashSet<>();

        Vertex root = new Vertex("deaeac66066efe4f", "ts-travel-service");
        Vertex databaseRoot = new Vertex("cfac5441aca32b4d", "database");

        Vertex vertex1 = new Vertex("218716907746edb3", "ts-route-service");
        Vertex databaseVertex1 = new Vertex("9c8123070fc810de", "database");

        Set<Vertex> vertexSet = new HashSet<>(List.of(root, databaseRoot, vertex1, databaseVertex1));

        edgeSet.add(new Edge("database-read", "database", root, databaseRoot));
        edgeSet.add(new Edge("get /api/v1/routeservice/routes/{routeid}", "READ", root, vertex1));
        edgeSet.add(new Edge("database-read", "database", vertex1, databaseVertex1));

        return new Trace(vertexSet, edgeSet);
    }

    @Test
    public void Convert_input_to_trace_complex() throws IOException {
        ClassPathResource resource = new ClassPathResource("/TraceServiceTests/example_traces_2.json");
        List<List<Span>> input = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        HashMap<String, Set<String>> readEndpointMap = new HashMap<>();
        HashMap<String, Set<String>> writeEndpointMap = new HashMap<>();
        readEndpointMap.put("service2", new HashSet<>(Collections.singleton("y")));
        readEndpointMap.put("service4", new HashSet<>(Collections.singleton("y")));
        writeEndpointMap.put("service5", new HashSet<>(Collections.singleton("y")));
        writeEndpointMap.put("service3", new HashSet<>(Collections.singleton("z")));
        Model expected = new Model(getReadTraces2(), getWriteTraces2(), readEndpointMap, writeEndpointMap, 1);

        Model actual = traceService.tracesToModel(input);

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    private static List<Trace> getReadTraces2() {
        Vertex vertex1 = new Vertex("1", "service1");
        Vertex vertex2 = new Vertex("2", "database");
        Vertex vertex3 = new Vertex("3", "service2");
        Vertex vertex4 = new Vertex("4", "database");
        Vertex vertex5 = new Vertex("5", "database");
        Vertex vertex6 = new Vertex("6", "service3");
        Vertex vertex7 = new Vertex("7", "database");
        Vertex vertex8 = new Vertex("8", "service2");
        Vertex vertex9 = new Vertex("9", "database");
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");

        Set<Edge> edgeSet1 = new HashSet<>();

        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex1, vertex3));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex3, vertex10));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));

        Set<Vertex> vertexSet1 = new HashSet<>(List.of(vertex1, vertex2, vertex3, vertex4, vertex5, vertex10, vertex11));

        Trace trace1 = new Trace(vertexSet1, edgeSet1);

        Set<Edge> edgeSet2 = new HashSet<>();
        edgeSet2.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex6, vertex7));
        edgeSet2.add(new Edge("y", ModelConstants.READ_STRING, vertex6, vertex8));
        edgeSet2.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex8, vertex9));

        Set<Vertex> vertexSet2 = new HashSet<>(List.of(vertex6, vertex7, vertex8, vertex9));

        Trace trace2 = new Trace(vertexSet2, edgeSet2);

        return List.of(trace1, trace2);
    }

    private static List<Trace> getWriteTraces2() {
        Vertex vertex3 = new Vertex("3", "service2");
        Vertex vertex4 = new Vertex("4", "database");
        Vertex vertex5 = new Vertex("5", "database");
        Vertex vertex6 = new Vertex("6", "service3");
        Vertex vertex7 = new Vertex("7", "database");
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");
        Vertex vertex12 = new Vertex("12", "service5");
        Vertex vertex13 = new Vertex("13", "database");

        Set<Edge> edgeSet1 = new HashSet<>();

        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet1.add(new Edge("z", ModelConstants.WRITE_STRING, vertex3, vertex6));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex6, vertex7));

        Set<Vertex> vertexSet1 = new HashSet<>(List.of(vertex3, vertex4, vertex5, vertex6, vertex7));

        Trace trace1 = new Trace(vertexSet1, edgeSet1);

        Set<Edge> edgeSet2 = new HashSet<>();
        edgeSet2.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));
        edgeSet2.add(new Edge("y", ModelConstants.WRITE_STRING, vertex10, vertex12));
        edgeSet2.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex12, vertex13));

        Set<Vertex> vertexSet2 = new HashSet<>(List.of(vertex10, vertex11, vertex12, vertex13));

        Trace trace2 = new Trace(vertexSet2, edgeSet2);

        return List.of(trace1, trace2);
    }

    @Test
    public void Convert_input_to_trace_complex_dup() throws IOException {
        ClassPathResource resource = new ClassPathResource("/TraceServiceTests/example_traces_3_dup.json");
        List<List<Span>> input = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        HashMap<String, Set<String>> readEndpointMap = new HashMap<>();
        HashMap<String, Set<String>> writeEndpointMap = new HashMap<>();
        readEndpointMap.put("service2", new HashSet<>(Collections.singleton("y")));
        readEndpointMap.put("service4", new HashSet<>(Collections.singleton("y")));
        readEndpointMap.put("service3", new HashSet<>(Collections.singleton("p")));
        writeEndpointMap.put("service5", new HashSet<>(Collections.singleton("y")));
        Model expected = new Model(getReadTracesDup(), getWriteTracesDup(), readEndpointMap, writeEndpointMap, 1);

        Model actual = traceService.tracesToModel(input);

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    private static List<Trace> getReadTracesDup() {
        Vertex vertex1 = new Vertex("1", "service1");
        Vertex vertex2 = new Vertex("2", "database");
        Vertex vertex3 = new Vertex("3", "service2");
        Vertex vertex4 = new Vertex("4", "database");
        Vertex vertex5 = new Vertex("5", "database");
        Vertex vertex7 = new Vertex("7", "database");
        Vertex vertex8 = new Vertex("8", "service3");
        Vertex vertex9 = new Vertex("9", "database");
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");

        Set<Edge> edgeSet1 = new HashSet<>();

        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex1, vertex3));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex7));
        edgeSet1.add(new Edge("p", ModelConstants.READ_STRING, vertex3, vertex8));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex8, vertex9));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex3, vertex10));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));

        Set<Vertex> vertexSet = Set.of(vertex1, vertex2, vertex3, vertex4, vertex5, vertex7, vertex8, vertex9, vertex10, vertex11);

        return List.of(new Trace(vertexSet, edgeSet1));
    }

    private static List<Trace> getWriteTracesDup() {
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");
        Vertex vertex12 = new Vertex("12", "service5");
        Vertex vertex13 = new Vertex("13", "database");

        Set<Edge> edgeSet2 = new HashSet<>();
        edgeSet2.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));
        edgeSet2.add(new Edge("y", ModelConstants.WRITE_STRING, vertex10, vertex12));
        edgeSet2.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex12, vertex13));

        Set<Vertex> vertexSet2 = new HashSet<>(List.of(vertex10, vertex11, vertex12, vertex13));

        Trace trace2 = new Trace(vertexSet2, edgeSet2);

        return List.of(trace2);
    }

    @Test
    public void Convert_input_to_trace_complex_send_read() throws IOException {
        ClassPathResource resource = new ClassPathResource("/TraceServiceTests/example_traces_4_read_send.json");
        List<List<Span>> input = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        HashMap<String, Set<String>> readEndpointMap = new HashMap<>();
        HashMap<String, Set<String>> writeEndpointMap = new HashMap<>();
        readEndpointMap.put("service2", new HashSet<>(Collections.singleton("y")));
        readEndpointMap.put("service4", new HashSet<>(Collections.singleton("y")));
        readEndpointMap.put("service3", new HashSet<>(Collections.singleton("z")));
        writeEndpointMap.put("service5", new HashSet<>(Collections.singleton("y")));
        Model expected = new Model(getReadTracesReadSend(), getWriteTracesReadSend(), readEndpointMap, writeEndpointMap, 1);

        Model actual = traceService.tracesToModel(input);

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    private static List<Trace> getReadTracesReadSend() {
        Vertex vertex1 = new Vertex("1", "service1");
        Vertex vertex2 = new Vertex("2", "database");
        Vertex vertex3 = new Vertex("3", "service2");
        Vertex vertex4 = new Vertex("4", "database");
        Vertex vertex5 = new Vertex("5", "database");
        Vertex vertex6 = new Vertex("6", "service3");
        Vertex vertex7 = new Vertex("7", "database");
        Vertex vertex8 = new Vertex("8", "service2");
        Vertex vertex9 = new Vertex("9", "database");
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");

        Set<Edge> edgeSet1 = new HashSet<>();

        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex1, vertex3));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex3, vertex10));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));
        edgeSet1.add(new Edge("z", ModelConstants.SEND_READ_STRING, vertex3, vertex6));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex6, vertex7));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex6, vertex8));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex8, vertex9));

        Set<Vertex> vertexSet1 = new HashSet<>(List.of(vertex1, vertex2, vertex3, vertex4, vertex5, vertex10, vertex11, vertex6, vertex7, vertex8, vertex9));

        return List.of(new Trace(vertexSet1, edgeSet1));
    }

    private static List<Trace> getWriteTracesReadSend() {
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");
        Vertex vertex12 = new Vertex("12", "service5");
        Vertex vertex13 = new Vertex("13", "database");

        Set<Edge> edgeSet = new HashSet<>();
        edgeSet.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));
        edgeSet.add(new Edge("y", ModelConstants.WRITE_STRING, vertex10, vertex12));
        edgeSet.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex12, vertex13));

        Set<Vertex> vertexSet = new HashSet<>(List.of(vertex10, vertex11, vertex12, vertex13));

        Trace trace = new Trace(vertexSet, edgeSet);

        return List.of(trace);
    }

    @Test
    public void Convert_input_to_trace_complex_send_read2() throws IOException {
        ClassPathResource resource = new ClassPathResource("/TraceServiceTests/example_traces_4_read_send2.json");
        List<List<Span>> input = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        HashMap<String, Set<String>> readEndpointMap = new HashMap<>();
        HashMap<String, Set<String>> writeEndpointMap = new HashMap<>();
        readEndpointMap.put("service2", new HashSet<>(Collections.singleton("y")));
        readEndpointMap.put("service4", new HashSet<>(Collections.singleton("y")));
        readEndpointMap.put("service3", new HashSet<>(Collections.singleton("z")));
        writeEndpointMap.put("service3", new HashSet<>(Collections.singleton("z")));
        writeEndpointMap.put("service2", new HashSet<>(Collections.singleton("y")));
        writeEndpointMap.put("service5", new HashSet<>(Collections.singleton("y")));
        writeEndpointMap.put("service6", new HashSet<>(Collections.singleton("a")));
        Model expected = new Model(getReadTracesReadSend2(), getWriteTracesReadSend2(), readEndpointMap, writeEndpointMap, 1);

        Model actual = traceService.tracesToModel(input);

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    private static List<Trace> getReadTracesReadSend2() {
        Vertex vertex1 = new Vertex("1", "service1");
        Vertex vertex2 = new Vertex("2", "database");
        Vertex vertex3 = new Vertex("3", "service2");
        Vertex vertex4 = new Vertex("4", "database");
        Vertex vertex5 = new Vertex("5", "database");
        Vertex vertex6 = new Vertex("6", "service3");
        Vertex vertex7 = new Vertex("7", "database");
        Vertex vertex8 = new Vertex("8", "service2");
        Vertex vertex9 = new Vertex("9", "database");
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");

        Set<Edge> edgeSet1 = new HashSet<>();

        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex1, vertex3));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet1.add(new Edge("y", ModelConstants.READ_STRING, vertex3, vertex10));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));
        edgeSet1.add(new Edge("z", ModelConstants.SEND_READ_STRING, vertex3, vertex6));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex6, vertex7));
        edgeSet1.add(new Edge("y", ModelConstants.SEND_READ_STRING, vertex6, vertex8));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex8, vertex9));

        Set<Vertex> vertexSet1 = new HashSet<>(List.of(vertex1, vertex2, vertex3, vertex4, vertex5, vertex10, vertex11, vertex6, vertex7, vertex8, vertex9));

        return List.of(new Trace(vertexSet1, edgeSet1));
    }

    private static List<Trace> getWriteTracesReadSend2() {
        Vertex vertex3 = new Vertex("3", "service2");
        Vertex vertex4 = new Vertex("4", "database");
        Vertex vertex5 = new Vertex("5", "database");
        Vertex vertex6 = new Vertex("6", "service3");
        Vertex vertex8 = new Vertex("8", "service2");
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");
        Vertex vertex12 = new Vertex("12", "service5");
        Vertex vertex13 = new Vertex("13", "database");
        Vertex vertex14 = new Vertex("14", "service6");
        Vertex vertex15 = new Vertex("15", "database");

        Set<Edge> edgeSet1 = new HashSet<>();

        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet1.add(new Edge("z", ModelConstants.SEND_READ_STRING, vertex3, vertex6));
        edgeSet1.add(new Edge("y", ModelConstants.SEND_READ_STRING, vertex6, vertex8));
        edgeSet1.add(new Edge("a", ModelConstants.WRITE_STRING, vertex8, vertex14));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex14, vertex15));

        Set<Vertex> vertexSet1 = new HashSet<>(List.of(vertex3, vertex4, vertex5, vertex6, vertex8, vertex14, vertex15));

        Trace trace1 = new Trace(vertexSet1, edgeSet1);

        Set<Edge> edgeSet2 = new HashSet<>();
        edgeSet2.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));
        edgeSet2.add(new Edge("y", ModelConstants.WRITE_STRING, vertex10, vertex12));
        edgeSet2.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex12, vertex13));

        Set<Vertex> vertexSet2 = new HashSet<>(List.of(vertex10, vertex11, vertex12, vertex13));

        Trace trace2 = new Trace(vertexSet2, edgeSet2);

        return List.of(trace1, trace2);
    }

}
