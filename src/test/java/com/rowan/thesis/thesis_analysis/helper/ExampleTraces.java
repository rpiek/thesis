package com.rowan.thesis.thesis_analysis.helper;

import com.rowan.thesis.thesis_analysis.model.trace.Edge;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
import com.rowan.thesis.thesis_analysis.model.trace.Vertex;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExampleTraces {


    private static final Vertex DATABASE_VERTEX = new Vertex(ModelConstants.DATABASE_NAME, ModelConstants.DATABASE_NAME);


    public static Trace Get_example_simple_get_trace() {
        Set<Edge> edges = new HashSet<>();

        Vertex root = new Vertex("1", "service1");
        Vertex vertex1 = new Vertex("2","service2");
        Vertex vertex2 = new Vertex("3", "service3");

        edges.add(new Edge("x", ModelConstants.READ_STRING, root, vertex1));
        edges.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, root, DATABASE_VERTEX));
        edges.add(new Edge("y", ModelConstants.READ_STRING, vertex1, vertex2));
        edges.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex1, DATABASE_VERTEX));
        edges.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex2, DATABASE_VERTEX));

        Set<Vertex> vertexSet = Set.of(root, vertex1, vertex2);

        return new Trace(vertexSet, edges);
    }

    public static Trace Get_example_simple_post_trace() {
        Set<Edge> edges = new HashSet<>();

        Vertex root = new Vertex("1", "service1");
        Vertex vertex1 = new Vertex("2","service2");
        Vertex vertex2 = new Vertex("3", "service3");

        edges.add(new Edge("x", ModelConstants.WRITE_STRING, root, vertex1));
        edges.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, root, DATABASE_VERTEX));
        edges.add(new Edge("y", ModelConstants.WRITE_STRING, vertex1, vertex2));
        edges.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex1, DATABASE_VERTEX));
        edges.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex2, DATABASE_VERTEX));

        Set<Vertex> vertexSet = Set.of(root, vertex1, vertex2);

        return new Trace(vertexSet, edges);
    }


    public static Map<String, Set<String>> Get_example_simple_trace_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service2", new HashSet<>(Collections.singleton("x")));
        map.put("service3", new HashSet<>(Collections.singleton("y")));

        return map;
    }

    public static Trace Get_example_complex_read_trace() {
        Vertex vertex1 = new Vertex("2","service2");
        Vertex vertex2 = new Vertex("3", "service3");
        Vertex vertex3 = new Vertex("5", "service4");

        Set<Edge> edges = new HashSet<>();

        edges.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex2, DATABASE_VERTEX));
        edges.add(new Edge("z", ModelConstants.READ_STRING, vertex2, vertex3));
        edges.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, DATABASE_VERTEX));

        Set<Vertex> vertexSet = Set.of(vertex1, vertex2, vertex3);

        return new Trace(vertexSet, edges);
    }

    public static Trace Get_example_complex_write_trace() {
        Vertex root = new Vertex("1", "service1");
        Vertex vertex1 = new Vertex("2","service2");
        Vertex vertex2 = new Vertex("3", "service3");

        Set<Edge> edges = new HashSet<>();

        edges.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, root, DATABASE_VERTEX));
        edges.add(new Edge("x", ModelConstants.WRITE_STRING, root, vertex1));
        edges.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex1, DATABASE_VERTEX));
        edges.add(new Edge(ModelConstants.DATABASE_NAME + "1", ModelConstants.DATABASE_NAME, vertex1, DATABASE_VERTEX));
        edges.add(new Edge("y", ModelConstants.WRITE_STRING, vertex1, vertex2));
        edges.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex2, DATABASE_VERTEX));

        Set<Vertex> vertexSet = Set.of(root, vertex1);

        return new Trace(vertexSet, edges);
    }

    public static Map<String, Set<String>> Get_example_complex_trace_read_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service4", new HashSet<>(Collections.singleton("z")));

        return map;
    }

    public static Map<String, Set<String>> Get_example_complex_trace_write_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service2", new HashSet<>(Collections.singleton("x")));
        map.put("service3", new HashSet<>(Collections.singleton("y")));

        return map;
    }

    public static List<Trace> Get_example_parallel_read_trace() {
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
        edgeSet2.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex6, vertex7));
        edgeSet2.add(new Edge("y", ModelConstants.READ_STRING, vertex6, vertex8));
        edgeSet2.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex8, vertex9));

        Set<Vertex> vertexSet2 = new HashSet<>(List.of(vertex6, vertex7, vertex8, vertex9));

        Trace trace2 = new Trace(vertexSet2, edgeSet2);

        return List.of(trace1, trace2);
    }

    public static List<Trace> Get_example_parallel_write_trace() {
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

        edgeSet1.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet1.add(new Edge("z", ModelConstants.WRITE_STRING, vertex3, vertex6));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex6, vertex7));

        Set<Vertex> vertexSet1 = new HashSet<>(List.of(vertex3, vertex4, vertex5, vertex6, vertex7));

        Trace trace1 = new Trace(vertexSet1, edgeSet1);

        Set<Edge> edgeSet2 = new HashSet<>();
        edgeSet2.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex10, vertex11));
        edgeSet2.add(new Edge("y", ModelConstants.WRITE_STRING, vertex10, vertex12));
        edgeSet2.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex12, vertex13));

        Set<Vertex> vertexSet2 = new HashSet<>(List.of(vertex10, vertex11, vertex12, vertex13));

        Trace trace2 = new Trace(vertexSet2, edgeSet2);

        return List.of(trace1, trace2);
    }

    public static Map<String, Set<String>> Get_example_parallel_read_trace_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service2", new HashSet<>(Collections.singleton("y")));
        map.put("service4", new HashSet<>(Collections.singleton("y")));

        return map;
    }

    public static Map<String, Set<String>> Get_example_parallel_write_trace_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service5", new HashSet<>(Collections.singleton("y")));
        map.put("service3", new HashSet<>(Collections.singleton("z")));

        return map;
    }

    public static List<Trace> Get_example_parallel_read_trace_with_dup() {
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

        Set<Edge> edgeSet = new HashSet<>();

        edgeSet.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet.add(new Edge("y", ModelConstants.READ_STRING, vertex1, vertex3));
        edgeSet.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet.add(new Edge("z", ModelConstants.READ_STRING, vertex3, vertex6));
        edgeSet.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex6, vertex7));
        edgeSet.add(new Edge("y", ModelConstants.READ_STRING, vertex6, vertex8));
        edgeSet.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex8, vertex9));
        edgeSet.add(new Edge("y", ModelConstants.READ_STRING, vertex3, vertex10));
        edgeSet.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex10, vertex11));

        Set<Vertex> vertexSet = new HashSet<>(List.of(vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8, vertex9, vertex10, vertex11));

        return List.of(new Trace(vertexSet, edgeSet));
    }

    public static List<Trace> Get_example_parallel_write_trace_with_dup() {
        Vertex vertex10 = new Vertex("10", "service4");
        Vertex vertex11 = new Vertex("11", "database");
        Vertex vertex12 = new Vertex("12", "service5");
        Vertex vertex13 = new Vertex("13", "database");


        Set<Edge> edgeSet = new HashSet<>();
        edgeSet.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex10, vertex11));
        edgeSet.add(new Edge("y", ModelConstants.WRITE_STRING, vertex10, vertex12));
        edgeSet.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex12, vertex13));

        Set<Vertex> vertexSet2 = new HashSet<>(List.of(vertex10, vertex11, vertex12, vertex13));

        Trace trace = new Trace(vertexSet2, edgeSet);

        return List.of(trace);
    }

    public static Map<String, Set<String>> Get_example_parallel_read_trace_endpoint_map_with_dup() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service2", new HashSet<>(Collections.singleton("y")));
        map.put("service3", new HashSet<>(Collections.singleton("z")));
        map.put("service4", new HashSet<>(Collections.singleton("y")));

        return map;
    }

    public static Map<String, Set<String>> Get_example_parallel_write_trace_endpoint_map_with_dup() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service5", new HashSet<>(Collections.singleton("y")));

        return map;
    }

    public static List<Trace> Get_example_read_traces_short() {
        Vertex vertex1 = new Vertex("1", "service1");
        Vertex vertex2 = new Vertex("2", "database");
        Vertex vertex3 = new Vertex("3", "service2");
        Vertex vertex4 = new Vertex("4", "database");
        Vertex vertex5 = new Vertex("5", "database");
        Vertex vertex6 = new Vertex("6", "service3");
        Vertex vertex7 = new Vertex("7", "database");

        Set<Edge> edgeSet1 = new HashSet<>();
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet1.add(new Edge("i1", ModelConstants.READ_STRING, vertex1, vertex3));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));


        Set<Vertex> vertexSet1 = Set.of(vertex1, vertex2, vertex3, vertex4, vertex5);
        Trace trace1 = new Trace(vertexSet1, edgeSet1);

        Set<Edge> edgeSet2 = new HashSet<>();
        edgeSet2.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet2.add(new Edge("i2", ModelConstants.READ_STRING, vertex1, vertex3));
        edgeSet2.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));


        Set<Vertex> vertexSet2 = Set.of(vertex1, vertex2, vertex3, vertex4, vertex5);
        Trace trace2 = new Trace(vertexSet2, edgeSet2);

        Set<Edge> edgeSet3 = new HashSet<>();
        edgeSet3.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet3.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet3.add(new Edge("i3", ModelConstants.READ_STRING, vertex3, vertex6));
        edgeSet3.add(new Edge(ModelConstants.DATABASE_READ, ModelConstants.DATABASE_NAME, vertex6, vertex6));


        Set<Vertex> vertexSet3 = Set.of(vertex3, vertex4, vertex5, vertex6, vertex7);
        Trace trace3 = new Trace(vertexSet3, edgeSet3);

        return List.of(trace1, trace2, trace3);
    }

    public static List<Trace> Get_example_write_traces_short() {
        Vertex vertex1 = new Vertex("1", "service1");
        Vertex vertex2 = new Vertex("2", "database");
        Vertex vertex3 = new Vertex("3", "service2");
        Vertex vertex4 = new Vertex("4", "database");
        Vertex vertex5 = new Vertex("5", "database");
        Vertex vertex6 = new Vertex("6", "service3");
        Vertex vertex7 = new Vertex("7", "database");

        Set<Edge> edgeSet1 = new HashSet<>();
        edgeSet1.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet1.add(new Edge("i4", ModelConstants.WRITE_STRING, vertex1, vertex3));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet1.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex3, vertex5));


        Set<Vertex> vertexSet1 = Set.of(vertex1, vertex2, vertex3, vertex4, vertex5);
        Trace trace1 = new Trace(vertexSet1, edgeSet1);

        Set<Edge> edgeSet2 = new HashSet<>();
        edgeSet2.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex1, vertex2));
        edgeSet2.add(new Edge("i4", ModelConstants.WRITE_STRING, vertex1, vertex3));
        edgeSet2.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex3, vertex5));


        Set<Vertex> vertexSet2 = Set.of(vertex1, vertex2, vertex3, vertex4, vertex5);
        Trace trace2 = new Trace(vertexSet2, edgeSet2);

        Set<Edge> edgeSet3 = new HashSet<>();
        edgeSet3.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex3, vertex4));
        edgeSet3.add(new Edge(ModelConstants.DATABASE_WRITE, ModelConstants.DATABASE_NAME, vertex3, vertex5));
        edgeSet3.add(new Edge("i5", ModelConstants.POST_STRING, vertex3, vertex6));


        Set<Vertex> vertexSet3 = Set.of(vertex3, vertex4, vertex5, vertex6, vertex7);
        Trace trace3 = new Trace(vertexSet3, edgeSet3);

        return List.of(trace1, trace2, trace3);
    }

    public static Map<String, Set<String>> Get_example_parallel_read_trace_endpoint_map_short() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service2", new HashSet<>(Set.of("i1", "i2")));
        map.put("service3", new HashSet<>(Collections.singleton("i3")));

        return map;
    }

    public static Map<String, Set<String>> Get_example_parallel_write_trace_endpoint_map_short() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service2", new HashSet<>(Collections.singleton("i4")));
        map.put("service3", new HashSet<>(Collections.singleton("i5")));

        return map;
    }


//    public static Node Get_example_parallel_trace() {
//        Vertex root = new Vertex("service1", new ArrayList<>());
//        Vertex vertex1 = new Vertex("service2", new ArrayList<>());
//        Vertex vertex2 = new Vertex("service3", new ArrayList<>());
//        Vertex vertex3 = new Vertex("service4", new ArrayList<>());
//        Vertex vertex4 = new Vertex("service5", new ArrayList<>());
//
//        root.addEdge(new Edge("i2", ModelConstants.GET_STRING, vertex1));
//        root.addEdge(DATABASE_EDGE);
//
//        vertex1.addEdge(new Edge("i3", ModelConstants.GET_STRING, vertex2));
//        vertex1.addEdge(DATABASE_EDGE);
//        vertex1.addEdge(DATABASE_EDGE);
//
//        vertex2.addEdge(DATABASE_EDGE);
//        vertex2.addEdge(new Edge("z", ModelConstants.GET_STRING, vertex3));
//
//        vertex3.addEdge(DATABASE_EDGE);
//
//
//        Node node2 = new Node(
//                "service2",
//                "i2",
//                ModelConstants.GET_STRING,
//                new ArrayList<>()
//        );
//
//        Node rootNodeDatabaseNode = new Node(
//                "service1",
//                ModelConstants.DATABASE_NAME,
//                null,
//                new ArrayList<>()
//        );
//
//        Node node2DatabaseNode = new Node(
//                "service2",
//                ModelConstants.DATABASE_NAME,
//                null,
//                new ArrayList<>()
//        );
//
//        Node node2DatabaseNode2 = new Node(
//                "service2",
//                ModelConstants.DATABASE_NAME,
//                null,
//                new ArrayList<>()
//        );
//
//        Node node3 = new Node(
//                "service3",
//                "i3",
//                ModelConstants.PUT_STRING,
//                new ArrayList<>()
//        );
//
//        Node node3DatabaseNode = new Node(
//                "service3",
//                ModelConstants.DATABASE_NAME,
//                null,
//                new ArrayList<>()
//        );
//
//        Node node4 = new Node(
//                "service4",
//                "i4",
//                ModelConstants.GET_STRING,
//                new ArrayList<>()
//        );
//
//        Node node4DatabaseNode = new Node(
//                "service4",
//                ModelConstants.DATABASE_NAME,
//                null,
//                new ArrayList<>()
//        );
//
//        Node node5 = new Node(
//                "service5",
//                "i5",
//                ModelConstants.POST_STRING,
//                new ArrayList<>()
//        );
//
//        Node node5DatabaseNode = new Node(
//                "service5",
//                ModelConstants.DATABASE_NAME,
//                null,
//                new ArrayList<>()
//        );
//
//        Node node6 = new Node(
//                "service2",
//                "i2",
//                ModelConstants.GET_STRING,
//                new ArrayList<>()
//        );
//
//        Node node6DatabaseNode = new Node(
//                "service2",
//                ModelConstants.DATABASE_NAME,
//                null,
//                new ArrayList<>()
//        );
//
//        ArrayList<Node> rootChildren = new ArrayList<>();
//        rootChildren.add(node2);
//        rootChildren.add(rootNodeDatabaseNode);
//        rootNode.setChildren(rootChildren);
//
//        node2.addChild(node3);
//        node2.addChild(node4);
//        node2.addChild(node2DatabaseNode);
//        node2.addChild(node2DatabaseNode);
//        node2.addChild(node2DatabaseNode);
//        node2.addChild(node2DatabaseNode);
//        node2.addChild(node2DatabaseNode2);
//
//        node3.addChild(node3DatabaseNode);
//        node3.addChild(node6);
//
//        node4.addChild(node4DatabaseNode);
//        node4.addChild(node5);
//
//        node5.addChild(node5DatabaseNode);
//
//        node6.addChild(node6DatabaseNode);
//
//        return rootNode;
//    }
//
//    public static Map<String, Set<String>> Get_example_parallel_trace_read_endpoint_map() {
//        Map<String, Set<String>> map = new HashMap<>();
//
//        map.put("service2", new HashSet<>(Collections.singleton("i2")));
//        map.put("service4", new HashSet<>(Collections.singleton("i4")));
//
//        return map;
//    }
//
//    public static Map<String, Set<String>> Get_example_parallel_trace_write_endpoint_map() {
//        Map<String, Set<String>> map = new HashMap<>();
//
//        map.put("service3", new HashSet<>(Collections.singleton("i3")));
//        map.put("service5", new HashSet<>(Collections.singleton("i5")));
//
//        return map;
//    }

}
