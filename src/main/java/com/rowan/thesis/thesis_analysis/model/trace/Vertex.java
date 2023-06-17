package com.rowan.thesis.thesis_analysis.model.trace;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vertex {

    private String name;
    private List<Edge> edges;

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

}
