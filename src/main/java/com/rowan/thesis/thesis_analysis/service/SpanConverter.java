package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpanConverter {

    public List<Node> createForest(Node root) {
        List<Node> forest = new ArrayList<>();
        createForestHelper(root, forest);
        return forest;
    }

    private static boolean isValidNode(Node node) {
        // Check if the node is valid based on the specified conditions
        return (node.getEndpoint().equals("database") || node.getMethod().equals("GET"));
    }

    private static List<Node> createForestHelper(Node node, List<Node> forest) {
        if (node == null) {
            return new ArrayList<>();
        }

        List<Node> largestSubtree = new ArrayList<>();
        boolean isValidRoot = isValidNode(node);

        for (Node child : node.getChildren()) {
            List<Node> subtree = createForestHelper(child, forest);
            if (isValidRoot || isValidNode(child)) {
                largestSubtree.addAll(subtree);
            } else {
                // Add the largest subtree found so far to the forest
                if (largestSubtree.size() > forest.size()) {
                    forest.clear();
                    forest.addAll(largestSubtree);
                }
                largestSubtree.clear();
            }
        }

        if (isValidRoot) {
            largestSubtree.add(node);
        }

        if (largestSubtree.size() > forest.size()) {
            forest.clear();
            forest.addAll(largestSubtree);
        }

        return largestSubtree;
    }
}

