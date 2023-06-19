package com.rowan.thesis.thesis_analysis.model.metric;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataDependsNeedScore {

    private String target;
    private String endpoint;
    private double value;

}
