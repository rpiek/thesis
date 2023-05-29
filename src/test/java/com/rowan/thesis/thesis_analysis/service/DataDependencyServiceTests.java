package com.rowan.thesis.thesis_analysis.service;


import static org.mockito.Mockito.when;

import com.rowan.thesis.thesis_analysis.helper.ExampleTraces;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsType;
import com.rowan.thesis.thesis_analysis.model.metric.Metric;
import com.rowan.thesis.thesis_analysis.model.metric.Result;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class DataDependencyServiceTests {

    @Mock
    TraceService traceService;

    @InjectMocks
    private DataDependencyService dataDependencyService;


    @Test
    public void Test_get_data_depends_read() {
        ArrayList<Metric> metrics = new ArrayList<>();
        Metric metric1 = new Metric(DataDependsType.DATA_DEPENDS_READ, "service1", "a", 0);
        Metric metric2 = new Metric(DataDependsType.DATA_DEPENDS_READ, "service2", "x", 1);
        Metric metric3 = new Metric(DataDependsType.DATA_DEPENDS_READ, "service3", "y", 2);
        metrics.add(metric1);
        metrics.add(metric2);
        metrics.add(metric3);
        Map<String, Integer> map = new HashMap<>();
        map.put("service1", 0);
        map.put("service2", 1);
        map.put("service3", 2);
        Result expected = new Result(map, null, null, metrics);


        when(traceService.getReadEndpointMap()).thenReturn(ExampleTraces.Get_example_simple_trace_endpoint_map());
        Result actual = dataDependencyService.getDataDependsReadScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_simple_get_trace()))));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void Test_get_data_depends_write() {
        ArrayList<Metric> metrics = new ArrayList<>();
        Metric metric1 = new Metric(DataDependsType.DATA_DEPENDS_WRITE, "service1", "a", 0);
        Metric metric2 = new Metric(DataDependsType.DATA_DEPENDS_WRITE, "service2", "x", 1);
        Metric metric3 = new Metric(DataDependsType.DATA_DEPENDS_WRITE, "service3", "y", 2);
        metrics.add(metric1);
        metrics.add(metric2);
        metrics.add(metric3);
        Map<String, Integer> map = new HashMap<>();
        map.put("service1", 0);
        map.put("service2", 1);
        map.put("service3", 2);
        Result expected = new Result(null, map, null, metrics);

        when(traceService.getWriteEndpointMap()).thenReturn(ExampleTraces.Get_example_simple_trace_endpoint_map());
        Result actual = dataDependencyService.getDataDependsWriteScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_simple_post_trace()))));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void Test_get_data_depends_need() {
        ArrayList<Metric> metrics = new ArrayList<>();
        Metric metric1 = new Metric(DataDependsType.DATA_DEPENDS_NEED, "service1", "a", 0);
        Metric metric2 = new Metric(DataDependsType.DATA_DEPENDS_NEED, "service2", "x", 0);
        Metric metric3 = new Metric(DataDependsType.DATA_DEPENDS_NEED, "service3", "y", 1);
        metrics.add(metric1);
        metrics.add(metric2);
        metrics.add(metric3);
        Map<String, Integer> map = new HashMap<>();
        map.put("service1", 0);
        map.put("service2", 0);
        map.put("service3", 1);
        Result expected = new Result(null, null, map, metrics);

        when(traceService.getWriteEndpointMap()).thenReturn(ExampleTraces.Get_example_simple_trace_endpoint_map());
        Result actual = dataDependencyService.getDataDependsNeedScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_simple_post_trace()))));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

}
