package com.rowan.thesis.thesis_analysis.model.trace;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {

    private String endpoint;
    private String method;
    private Vertex target;

}
