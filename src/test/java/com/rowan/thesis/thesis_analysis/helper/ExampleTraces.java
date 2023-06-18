package com.rowan.thesis.thesis_analysis.helper;

import com.rowan.thesis.thesis_analysis.model.trace.Edge;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.model.trace.Vertex;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExampleTraces {
//
//
//    private static final Vertex DATABASE_VERTEX = new Vertex("database", new ArrayList<>());
//    private static final Edge DATABASE_EDGE = new Edge("database", "database", DATABASE_VERTEX);
//
//
//    public static Vertex Get_example_simple_get_trace() {
//
//        Vertex root = new Vertex("service1", new ArrayList<>());
//        Vertex vertex1 = new Vertex("service2", new ArrayList<>());
//        Vertex vertex2 = new Vertex("service3", new ArrayList<>());
//
//        root.addEdge(new Edge("x", ModelConstants.GET_STRING, vertex1));
//        root.addEdge(DATABASE_EDGE);
//
//        vertex1.addEdge(new Edge("y", ModelConstants.GET_STRING, vertex2));
//        vertex1.addEdge(DATABASE_EDGE);
//
//        vertex2.addEdge(DATABASE_EDGE);
//
//        return root;
//    }
//
//    public static Vertex Get_example_simple_post_trace() {
//        Vertex root = new Vertex("service1", new ArrayList<>());
//        Vertex vertex1 = new Vertex("service2", new ArrayList<>());
//        Vertex vertex2 = new Vertex("service3", new ArrayList<>());
//
//        root.addEdge(new Edge("x", ModelConstants.POST_STRING, vertex1));
//        root.addEdge(DATABASE_EDGE);
//
//        vertex1.addEdge(new Edge("y", ModelConstants.POST_STRING, vertex2));
//        vertex1.addEdge(DATABASE_EDGE);
//
//        vertex2.addEdge(DATABASE_EDGE);
//
//        return root;
//    }
//
//
//    public static Map<String, Set<String>> Get_example_simple_trace_endpoint_map() {
//        Map<String, Set<String>> map = new HashMap<>();
//
//        map.put("service2", new HashSet<>(Collections.singleton("x")));
//        map.put("service3", new HashSet<>(Collections.singleton("y")));
//
//        return map;
//    }
//
//    public static Vertex Get_example_complex_trace() {
//        Vertex root = new Vertex("service1", new ArrayList<>());
//        Vertex vertex1 = new Vertex("service2", new ArrayList<>());
//        Vertex vertex2 = new Vertex("service3", new ArrayList<>());
//        Vertex vertex3 = new Vertex("service4", new ArrayList<>());
//
//        root.addEdge(new Edge("x", ModelConstants.POST_STRING, vertex1));
//        root.addEdge(DATABASE_EDGE);
//
//        vertex1.addEdge(new Edge("y", ModelConstants.GET_STRING, vertex2));
//        vertex1.addEdge(DATABASE_EDGE);
//        vertex1.addEdge(DATABASE_EDGE);
//
//        vertex2.addEdge(DATABASE_EDGE);
//        vertex2.addEdge(new Edge("z", ModelConstants.GET_STRING, vertex3));
//
//        vertex3.addEdge(DATABASE_EDGE);
//
//        return root;
//    }
//
//    public static Map<String, Set<String>> Get_example_complex_trace_read_endpoint_map() {
//        Map<String, Set<String>> map = new HashMap<>();
//
//        map.put("service4", new HashSet<>(Collections.singleton("z")));
//
//        return map;
//    }
//
//    public static Map<String, Set<String>> Get_example_complex_trace_write_endpoint_map() {
//        Map<String, Set<String>> map = new HashMap<>();
//
//        map.put("service2", new HashSet<>(Collections.singleton("x")));
//        map.put("service3", new HashSet<>(Collections.singleton("y")));
//
//        return map;
//    }

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
