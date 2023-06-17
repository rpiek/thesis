package com.rowan.thesis.thesis_analysis.model.trace;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Trace {

    Set<Vertex> vertices;
    Set<Edge> edges;

}
