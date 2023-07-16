package com.rowan.thesis.thesis_analysis.model.trace;

import com.rowan.thesis.thesis_analysis.utility.ModelConstants;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
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

    public Vertex findRootNode() {
        Set<Vertex> targetVertices = new HashSet<>();

        // Collect all target vertices from edges
        for (Edge edge : edges) {
            targetVertices.add(edge.getTarget());
        }

        // Find the root node among the vertices
        for (Vertex vertex : vertices) {
            if (!targetVertices.contains(vertex)) {
                return vertex;
            }
        }

        return null; // No root node found
    }

    public void removeRootNodeDatabaseCalls() {
        Vertex vertex = this.findRootNode();
        Set<Edge> databaseEdges = this.edges.stream().filter(edge -> edge.getSource().equals(vertex) && edge.getTarget().getName().equals(ModelConstants.DATABASE_NAME)).collect(Collectors.toSet());
        Set<Vertex> databaseVertices = databaseEdges.stream().map(Edge::getTarget).collect(Collectors.toSet());

        this.vertices.removeAll(databaseVertices);
        this.edges.removeAll(databaseEdges);
    }
}
