package com.rowan.thesis.thesis_analysis.service;


import static org.mockito.Mockito.when;

import com.rowan.thesis.thesis_analysis.helper.ExampleTraces;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsType;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsMetric;
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

//    @Test
//    public void Test_get_data_depends_read() {
//        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
//        DataDependsMetric dataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service2", "x", 1);
//        DataDependsMetric dataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service3", "y", 2);
//        dataDependsMetrics.add(dataDependsMetric2);
//        dataDependsMetrics.add(dataDependsMetric3);
//        Map<String, Integer> map = new HashMap<>();
//        map.put("service1", 0);
//        map.put("service2", 1);
//        map.put("service3", 2);
//        Result expected = new Result(map, null, dataDependsMetrics, null);
//
//
//        Result actual = dataDependencyService.getDataDependsReadScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_simple_get_trace())), ExampleTraces.Get_example_simple_trace_endpoint_map(), new HashMap<>()));
//
//        Assertions.assertThat(actual)
//                .usingRecursiveComparison()
//                .ignoringCollectionOrder()
//                .isEqualTo(expected);
//    }
//
//    @Test
//    public void Test_get_data_depends_write() {
//        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
//        DataDependsMetric dataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service2", "x", 1);
//        DataDependsMetric dataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service3", "y", 2);
//        dataDependsMetrics.add(dataDependsMetric2);
//        dataDependsMetrics.add(dataDependsMetric3);
//        Map<String, Integer> map = new HashMap<>();
//        map.put("service1", 0);
//        map.put("service2", 1);
//        map.put("service3", 2);
//        Result expected = new Result(null, map, dataDependsMetrics, null);
//
//        Result actual = dataDependencyService.getDataDependsWriteScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_simple_post_trace())), new HashMap<>(), ExampleTraces.Get_example_simple_trace_endpoint_map()));
//
//        Assertions.assertThat(actual)
//                .usingRecursiveComparison()
//                .ignoringCollectionOrder()
//                .isEqualTo(expected);
//    }
//
//    @Test
//    public void Test_get_data_depends() {
//        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
//        DataDependsMetric dataDependsReadDataDependsMetric4 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service4", "z", 1);
//        DataDependsMetric dataDependsWriteDataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service2", "x", 2);
//        DataDependsMetric dataDependsWriteDataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service3", "y", 2);
//        dataDependsMetrics.add(dataDependsReadDataDependsMetric4);
//        dataDependsMetrics.add(dataDependsWriteDataDependsMetric2);
//        dataDependsMetrics.add(dataDependsWriteDataDependsMetric3);
//        Map<String, Integer> dataDependsReadMap = new HashMap<>();
//        Map<String, Integer> dataDependsWriteMap = new HashMap<>();
//        dataDependsReadMap.put("service4", 1);
//        dataDependsReadMap.put("service1", 0);
//        dataDependsWriteMap.put("service2", 2);
//        dataDependsWriteMap.put("service3", 2);
//
//        Result expected = new Result(dataDependsReadMap, dataDependsWriteMap, dataDependsMetrics, null);
//
//        Result actual = dataDependencyService.getDataDependsScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_complex_trace())), ExampleTraces.Get_example_complex_trace_read_endpoint_map(), ExampleTraces.Get_example_complex_trace_write_endpoint_map()));
//
//        Assertions.assertThat(actual)
//                .usingRecursiveComparison()
//                .ignoringCollectionOrder()
//                .isEqualTo(expected);
//    }

//    @Test
//    public void Test_get_data_depends_parallel() {
//        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
//        DataDependsMetric dataDependsReadDataDependsMetric1 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service1", "i1", 0);
//        DataDependsMetric dataDependsReadDataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service2", "i2", 3);
//        DataDependsMetric dataDependsReadDataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service4", "i4", 2);
//        DataDependsMetric dataDependsWriteDataDependsMetric1 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service3", "i3", 1);
//        DataDependsMetric dataDependsWriteDataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service5", "i5", 1);
//        dataDependsMetrics.add(dataDependsReadDataDependsMetric1);
//        dataDependsMetrics.add(dataDependsReadDataDependsMetric2);
//        dataDependsMetrics.add(dataDependsReadDataDependsMetric3);
//        dataDependsMetrics.add(dataDependsWriteDataDependsMetric1);
//        dataDependsMetrics.add(dataDependsWriteDataDependsMetric2);
//        Map<String, Integer> dataDependsReadMap = new HashMap<>();
//        Map<String, Integer> dataDependsWriteMap = new HashMap<>();
//        dataDependsReadMap.put("service1", 0);
//        dataDependsReadMap.put("service2", 5);
//        dataDependsReadMap.put("service4", 2);
//        dataDependsWriteMap.put("service3", 1);
//        dataDependsWriteMap.put("service5", 1);
//
//        Result expected = new Result(dataDependsReadMap, dataDependsWriteMap, dataDependsMetrics, null);
//
//        Result actual = dataDependencyService.getDataDependsScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_parallel_trace())), ExampleTraces.Get_example_parallel_trace_read_endpoint_map(), ExampleTraces.Get_example_parallel_trace_write_endpoint_map()));
//
//        Assertions.assertThat(actual)
//                .usingRecursiveComparison()
//                .ignoringCollectionOrder()
//                .isEqualTo(expected);
//    }

}
