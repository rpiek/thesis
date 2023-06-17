package com.rowan.thesis.thesis_analysis.helper;

import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExampleTraces {


    public static Node Get_example_simple_get_trace() {
        Node rootNode = new Node(
                "service1",
                "a",
                ModelConstants.GET_STRING,
                new ArrayList<>()
        );

        Node rootChildNode1 = new Node(
                "service2",
                "x",
                ModelConstants.GET_STRING,
                new ArrayList<>()
        );

        Node rootChildNode1DatabaseNode = new Node(
                "service2",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node rootChildNode1ChildNode = new Node(
                "service3",
                "y",
                ModelConstants.GET_STRING,
                new ArrayList<>()
        );

        Node rootChildNode1ChildNodeDatabaseNode = new Node(
                "service3",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        rootNode.addChild(rootChildNode1);

        rootChildNode1.addChild(rootChildNode1ChildNode);
        rootChildNode1.addChild(rootChildNode1DatabaseNode);

        ArrayList<Node> rootChildChildChildren = new ArrayList<>();
        rootChildChildChildren.add(rootChildNode1ChildNodeDatabaseNode);
        rootChildNode1ChildNode.setChildren(rootChildChildChildren);

        return rootNode;
    }

    public static Node Get_example_simple_post_trace() {
        Node rootNode = new Node(
                "service1",
                "a",
                ModelConstants.ROOT_METHOD_STRING,
                new ArrayList<>()
        );

        Node rootChildNode1 = new Node(
                "service2",
                "x",
                ModelConstants.POST_STRING,
                new ArrayList<>()
        );

        Node rootChildNode1DatabaseNode = new Node(
                "service2",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node rootChildNode1ChildNode = new Node(
                "service3",
                "y",
                ModelConstants.POST_STRING,
                new ArrayList<>()
        );

        Node rootChildNode1ChildNodeDatabaseNode = new Node(
                "service3",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        rootNode.addChild(rootChildNode1);

        rootChildNode1.addChild(rootChildNode1ChildNode);
        rootChildNode1.addChild(rootChildNode1DatabaseNode);

        rootChildNode1ChildNode.addChild(rootChildNode1ChildNodeDatabaseNode);

        return rootNode;
    }


    public static Map<String, Set<String>> Get_example_simple_trace_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service1", new HashSet<>(Collections.singleton("a")));
        map.put("service2", new HashSet<>(Collections.singleton("x")));
        map.put("service3", new HashSet<>(Collections.singleton("y")));

        return map;
    }

    public static Node Get_example_complex_trace() {
        Node rootNode = new Node(
                "service1",
                "a",
                ModelConstants.ROOT_METHOD_STRING,
                new ArrayList<>()
        );

        Node node2 = new Node(
                "service2",
                "x",
                ModelConstants.POST_STRING,
                new ArrayList<>()
        );

        Node rootNodeDatabaseNode = new Node(
                "service1",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node2DatabaseNode = new Node(
                "service2",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node2DatabaseNode2 = new Node(
                "service2",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node3 = new Node(
                "service3",
                "y",
                ModelConstants.PUT_STRING,
                new ArrayList<>()
        );

        Node node3DatabaseNode = new Node(
                "service3",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node4 = new Node(
                "service4",
                "z",
                ModelConstants.GET_STRING,
                new ArrayList<>()
        );

        Node node4DatabaseNode = new Node(
                "service4",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        ArrayList<Node> rootChildren = new ArrayList<>();
        rootChildren.add(node2);
        rootChildren.add(rootNodeDatabaseNode);
        rootNode.setChildren(rootChildren);

        node2.addChild(node3);
        node2.addChild(node2DatabaseNode);
        node2.addChild(node2DatabaseNode2);

        node3.addChild(node3DatabaseNode);
        node3.addChild(node4);

        node4.addChild(node4DatabaseNode);

        return rootNode;
    }

    public static Map<String, Set<String>> Get_example_complex_trace_read_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service1", new HashSet<>(Collections.singleton("a")));
        map.put("service4", new HashSet<>(Collections.singleton("z")));

        return map;
    }

    public static Map<String, Set<String>> Get_example_complex_trace_write_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service2", new HashSet<>(Collections.singleton("x")));
        map.put("service3", new HashSet<>(Collections.singleton("y")));

        return map;
    }

    public static Node Get_example_parallel_trace() {
        Node rootNode = new Node(
                "service1",
                "i1",
                ModelConstants.ROOT_METHOD_STRING,
                new ArrayList<>()
        );

        Node node2 = new Node(
                "service2",
                "i2",
                ModelConstants.GET_STRING,
                new ArrayList<>()
        );

        Node rootNodeDatabaseNode = new Node(
                "service1",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node2DatabaseNode = new Node(
                "service2",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node2DatabaseNode2 = new Node(
                "service2",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node3 = new Node(
                "service3",
                "i3",
                ModelConstants.PUT_STRING,
                new ArrayList<>()
        );

        Node node3DatabaseNode = new Node(
                "service3",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node4 = new Node(
                "service4",
                "i4",
                ModelConstants.GET_STRING,
                new ArrayList<>()
        );

        Node node4DatabaseNode = new Node(
                "service4",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node5 = new Node(
                "service5",
                "i5",
                ModelConstants.POST_STRING,
                new ArrayList<>()
        );

        Node node5DatabaseNode = new Node(
                "service5",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        Node node6 = new Node(
                "service2",
                "i2",
                ModelConstants.GET_STRING,
                new ArrayList<>()
        );

        Node node6DatabaseNode = new Node(
                "service2",
                ModelConstants.DATABASE_NAME,
                null,
                new ArrayList<>()
        );

        ArrayList<Node> rootChildren = new ArrayList<>();
        rootChildren.add(node2);
        rootChildren.add(rootNodeDatabaseNode);
        rootNode.setChildren(rootChildren);

        node2.addChild(node3);
        node2.addChild(node4);
        node2.addChild(node2DatabaseNode);
        node2.addChild(node2DatabaseNode);
        node2.addChild(node2DatabaseNode);
        node2.addChild(node2DatabaseNode);
        node2.addChild(node2DatabaseNode2);

        node3.addChild(node3DatabaseNode);
        node3.addChild(node6);

        node4.addChild(node4DatabaseNode);
        node4.addChild(node5);

        node5.addChild(node5DatabaseNode);

        node6.addChild(node6DatabaseNode);

        return rootNode;
    }

    public static Map<String, Set<String>> Get_example_parallel_trace_read_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service1", new HashSet<>(Collections.singleton("i1")));
        map.put("service2", new HashSet<>(Collections.singleton("i2")));
        map.put("service4", new HashSet<>(Collections.singleton("i4")));

        return map;
    }

    public static Map<String, Set<String>> Get_example_parallel_trace_write_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service3", new HashSet<>(Collections.singleton("i3")));
        map.put("service5", new HashSet<>(Collections.singleton("i5")));

        return map;
    }

}
