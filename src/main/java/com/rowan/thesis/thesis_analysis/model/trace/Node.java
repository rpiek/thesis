package com.rowan.thesis.thesis_analysis.model.trace;

import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.ArrayList;
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

    public void addChild(Node node) {
        this.children.add(node);
    }

    public void addChildren(List<Node> nodes) {
        this.children.addAll(nodes);
    }

}
