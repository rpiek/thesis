package com.rowan.thesis.thesis_analysis.helper;

import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExampleTraces {


    public static Trace Get_example_simple_get_trace() {
        Node rootNode = new Node(
                "service1",
                "a",
                "GET",
                1L,
                new ArrayList<>()
        );

        Node rootChildNode1 = new Node(
                "service2",
                "x",
                "GET",
                2L,
                new ArrayList<>()
        );

        Node rootChildNode1DatabaseNode = new Node(
                "service2",
                "database",
                null,
                3L,
                new ArrayList<>()
        );

        Node rootChildNode1ChildNode = new Node(
                "service3",
                "y",
                "GET",
                3L,
                new ArrayList<>()
        );

        Node rootChildNode1ChildNodeDatabaseNode = new Node(
                "service3",
                "database",
                null,
                4L,
                new ArrayList<>()
        );

        ArrayList<Node> rootChildren = new ArrayList<>();
        rootChildren.add(rootChildNode1);
        rootNode.setChildren(rootChildren);

        ArrayList<Node> rootChildChildren = new ArrayList<>();
        rootChildChildren.add(rootChildNode1ChildNode);
        rootChildChildren.add(rootChildNode1DatabaseNode);
        rootChildNode1.setChildren(rootChildChildren);

        ArrayList<Node> rootChildChildChildren = new ArrayList<>();
        rootChildChildChildren.add(rootChildNode1ChildNodeDatabaseNode);
        rootChildNode1ChildNode.setChildren(rootChildChildChildren);

        return new Trace(rootNode);
    }

    public static Trace Get_example_simple_post_trace() {
        Node rootNode = new Node(
                "service1",
                "a",
                ModelConstants.ROOT_METHOD_STRING,
                1L,
                new ArrayList<>()
        );

        Node rootChildNode1 = new Node(
                "service2",
                "x",
                "POST",
                2L,
                new ArrayList<>()
        );

        Node rootChildNode1DatabaseNode = new Node(
                "service2",
                "database",
                null,
                3L,
                new ArrayList<>()
        );

        Node rootChildNode1ChildNode = new Node(
                "service3",
                "y",
                "POST",
                3L,
                new ArrayList<>()
        );

        Node rootChildNode1ChildNodeDatabaseNode = new Node(
                "service3",
                "database",
                null,
                4L,
                new ArrayList<>()
        );

        ArrayList<Node> rootChildren = new ArrayList<>();
        rootChildren.add(rootChildNode1);
        rootNode.setChildren(rootChildren);

        ArrayList<Node> rootChildChildren = new ArrayList<>();
        rootChildChildren.add(rootChildNode1ChildNode);
        rootChildChildren.add(rootChildNode1DatabaseNode);
        rootChildNode1.setChildren(rootChildChildren);

        ArrayList<Node> rootChildChildChildren = new ArrayList<>();
        rootChildChildChildren.add(rootChildNode1ChildNodeDatabaseNode);
        rootChildNode1ChildNode.setChildren(rootChildChildChildren);

        return new Trace(rootNode);
    }


    public static Map<String, Set<String>> Get_example_simple_trace_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service1", new HashSet<>(Collections.singleton("a")));
        map.put("service2", new HashSet<>(Collections.singleton("x")));
        map.put("service3", new HashSet<>(Collections.singleton("y")));

        return map;
    }

    public static Trace Get_example_complex_trace() {
        Node rootNode = new Node(
                "service1",
                "a",
                ModelConstants.ROOT_METHOD_STRING,
                1L,
                new ArrayList<>()
        );

        Node node2 = new Node(
                "service2",
                "x",
                "POST",
                2L,
                new ArrayList<>()
        );

        Node rootNodeDatabaseNode = new Node(
                "service1",
                "database",
                null,
                1L,
                new ArrayList<>()
        );

        Node node2DatabaseNode = new Node(
                "service2",
                "database",
                null,
                3L,
                new ArrayList<>()
        );

        Node node2DatabaseNode2 = new Node(
                "service2",
                "database",
                null,
                3L,
                new ArrayList<>()
        );

        Node node3 = new Node(
                "service3",
                "y",
                "PUT",
                3L,
                new ArrayList<>()
        );

        Node node3DatabaseNode = new Node(
                "service3",
                "database",
                null,
                4L,
                new ArrayList<>()
        );

        Node node4 = new Node(
                "service4",
                "z",
                "GET",
                5L,
                new ArrayList<>()
        );

        Node node4DatabaseNode = new Node(
                "service4",
                "database",
                null,
                6L,
                new ArrayList<>()
        );

        ArrayList<Node> rootChildren = new ArrayList<>();
        rootChildren.add(node2);
        rootChildren.add(rootNodeDatabaseNode);
        rootNode.setChildren(rootChildren);

        ArrayList<Node> node2Children = new ArrayList<>();
        node2Children.add(node3);
        node2Children.add(node2DatabaseNode);
        node2Children.add(node2DatabaseNode2);
         node2.setChildren(node2Children);

        ArrayList<Node> node3Children = new ArrayList<>();
        node3Children.add(node3DatabaseNode);
        node3Children.add(node4);
        node3.setChildren(node3Children);

        ArrayList<Node> node4Children = new ArrayList<>();
        node4Children.add(node4DatabaseNode);
        node4.setChildren(node4Children);

        return new Trace(rootNode);
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



}
