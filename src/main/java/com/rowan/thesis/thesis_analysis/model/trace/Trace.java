package com.rowan.thesis.thesis_analysis.model.trace;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trace {

    Set<Vertex> vertices;
    Set<Edge> edges;


    public void addVertices(Set<Vertex> vertices) {
        this.vertices.addAll(vertices);
    }

    public void addEdges(Set<Edge> edges) {
        this.edges.addAll(edges);
    }
}
