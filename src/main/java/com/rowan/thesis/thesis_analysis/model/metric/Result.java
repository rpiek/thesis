package com.rowan.thesis.thesis_analysis.model.metric;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {

    Map<String, Integer> dataDependsReadMap;
    Map<String, Integer> dataDependsWriteMap;
    List<DataDependsMetric> dataDependsMetrics;
    List<DataDependsNeedMetric> dataDependsNeedMetrics;

}
