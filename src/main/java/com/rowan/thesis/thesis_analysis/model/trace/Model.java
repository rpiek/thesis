package com.rowan.thesis.thesis_analysis.model.trace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Model {

    private List<Vertex> vertices;

    private Map<String, Set<String>> readEndpointMap;

    private Map<String, Set<String>> writeEndpointMap;

}
