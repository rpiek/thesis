package com.rowan.thesis.thesis_analysis.model.trace;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Node {

    private String name;
    private String endpoint;
    private String method;
    private Long timeStamp;

    private List<Node> children;

}
