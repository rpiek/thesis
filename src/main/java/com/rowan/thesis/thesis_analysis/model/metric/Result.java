package com.rowan.thesis.thesis_analysis.model.metric;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {

    double dataDependsSystemRead;
    double dataDependsSystemWrite;
    Map<String, Double> dataDependsRead;
    Map<String, Double> dataDependsWrite;
    List<DataDependsMetric> dataDependsSubMetrics;
    List<DataDependsNeedMetric> dataDependsNeedMetrics;

}
