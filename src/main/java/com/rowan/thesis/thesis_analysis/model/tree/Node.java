package com.rowan.thesis.thesis_analysis.model.tree;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Node {

    private String name;
    private String endpoint;
    private String method;

    private List<Node> children;
    private Node parent;

}
