package com.rowan.thesis.thesis_analysis.model.metric;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataDependsMetric {

    private DataDependsType type;
    private String service;
    private String endpoint;
    private double value;

}
