package com.rowan.thesis.thesis_analysis.helper;

import com.rowan.thesis.thesis_analysis.model.trace.Node;
import com.rowan.thesis.thesis_analysis.model.trace.Trace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class ExampleTraces {


    public static Trace Get_example_get_trace() {
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

    @Test
    public static Map<String, Set<String>> Get_example_get_trace_endpoint_map() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("service1", new HashSet<>(Collections.singleton("a")));
        map.put("service2", new HashSet<>(Collections.singleton("x")));
        map.put("service3", new HashSet<>(Collections.singleton("y")));

        return map;
    }

}
