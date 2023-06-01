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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
        Result expected = new Result(map, null, metrics);


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
        Result expected = new Result(null, map, metrics);

        when(traceService.getWriteEndpointMap()).thenReturn(ExampleTraces.Get_example_simple_trace_endpoint_map());
        Result actual = dataDependencyService.getDataDependsWriteScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_simple_post_trace()))));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void Test_get_data_depends() {
        ArrayList<Metric> metrics = new ArrayList<>();
        Metric dataDependsReadMetric1 = new Metric(DataDependsType.DATA_DEPENDS_READ, "service1", "a", 0);
        Metric dataDependsReadMetric4 = new Metric(DataDependsType.DATA_DEPENDS_READ, "service4", "z", 1);
        Metric dataDependsWriteMetric2 = new Metric(DataDependsType.DATA_DEPENDS_WRITE, "service2", "x", 2);
        Metric dataDependsWriteMetric3 = new Metric(DataDependsType.DATA_DEPENDS_WRITE, "service3", "y", 2);
        metrics.add(dataDependsReadMetric1);
        metrics.add(dataDependsReadMetric4);
        metrics.add(dataDependsWriteMetric2);
        metrics.add(dataDependsWriteMetric3);
        Map<String, Integer> dataDependsReadMap = new HashMap<>();
        Map<String, Integer> dataDependsWriteMap = new HashMap<>();
        dataDependsReadMap.put("service1", 0);
        dataDependsReadMap.put("service2", 0);
        dataDependsReadMap.put("service3", 0);
        dataDependsReadMap.put("service4", 1);
        dataDependsWriteMap.put("service1", 0);
        dataDependsWriteMap.put("service2", 2);
        dataDependsWriteMap.put("service3", 2);
        dataDependsWriteMap.put("service4", 0);

        Result expected = new Result(dataDependsReadMap, dataDependsWriteMap, metrics);

        when(traceService.getReadEndpointMap()).thenReturn(ExampleTraces.Get_example_complex_trace_read_endpoint_map());
        when(traceService.getWriteEndpointMap()).thenReturn(ExampleTraces.Get_example_complex_trace_write_endpoint_map());
        Result actual = dataDependencyService.getDataDependsScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_complex_trace()))));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

}
