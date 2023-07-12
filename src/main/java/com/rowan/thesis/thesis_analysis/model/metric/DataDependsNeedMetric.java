package com.rowan.thesis.thesis_analysis.model.metric;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DataDependsNeedMetric {

    private String source;
    private DataDependsType dataDependsType;
    private int sum;
    private Set<DataDependsNeedScore> scores;

    public void addScore(DataDependsNeedScore score) {
        this.scores.add(score);
    }

}
