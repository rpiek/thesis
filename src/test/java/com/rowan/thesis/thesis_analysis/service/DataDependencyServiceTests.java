package com.rowan.thesis.thesis_analysis.service;

import com.rowan.thesis.thesis_analysis.helper.ExampleTraces;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsMetric;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsNeedMetric;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsNeedScore;
import com.rowan.thesis.thesis_analysis.model.metric.DataDependsType;
import com.rowan.thesis.thesis_analysis.model.metric.Result;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class DataDependencyServiceTests {

    @InjectMocks
    private DataDependencyService dataDependencyService;

    @Test
    public void Test_get_data_depends_read() {
        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
        Map<String, Double> service2Map = new HashMap<>();
        service2Map.put("service1", 1.0);
        Map<String, Double> service3Map = new HashMap<>();
        service3Map.put("service2", 2.0);
        DataDependsMetric dataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service2", "x", service2Map, 1);
        DataDependsMetric dataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service3", "y", service3Map,2);
        dataDependsMetrics.add(dataDependsMetric2);
        dataDependsMetrics.add(dataDependsMetric3);
        Map<String, Double> map = new HashMap<>();
        map.put("service1", 0.0);
        map.put("service2", 1.0);
        map.put("service3", 2.0);
        Result expected = new Result(map, new HashMap<>(), dataDependsMetrics, new ArrayList<>());

        Result actual = dataDependencyService.getDataDependsReadScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_simple_get_trace())), new ArrayList<>(), ExampleTraces.Get_example_simple_trace_endpoint_map(), new HashMap<>()));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void Test_get_data_depends_write() {
        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
        Map<String, Double> service2Map = new HashMap<>();
        service2Map.put("service1", 1.0);
        Map<String, Double> service3Map = new HashMap<>();
        service3Map.put("service2", 2.0);
        DataDependsMetric dataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service2", "x", service2Map, 1);
        DataDependsMetric dataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service3", "y", service3Map, 2);
        dataDependsMetrics.add(dataDependsMetric2);
        dataDependsMetrics.add(dataDependsMetric3);
        Map<String, Double> map = new HashMap<>();
        map.put("service1", 0.0);
        map.put("service2", 1.0);
        map.put("service3", 2.0);
        Result expected = new Result(new HashMap<>(), map, dataDependsMetrics, new ArrayList<>());

        Result actual = dataDependencyService.getDataDependsWriteScore(new Model(new ArrayList<>(), new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_simple_post_trace())), new HashMap<>(), ExampleTraces.Get_example_simple_trace_endpoint_map()));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void Test_get_data_depends() {
        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
        Map<String, Double> service2Map = new HashMap<>();
        service2Map.put("service1", 2.0);
        Map<String, Double> service3Map = new HashMap<>();
        service3Map.put("service2", 2.0);
        Map<String, Double> service4Map = new HashMap<>();
        service4Map.put("service3", 1.0);
        DataDependsMetric dataDependsReadDataDependsMetric4 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service4", "z", service4Map, 1);
        DataDependsMetric dataDependsWriteDataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service2", "x", service2Map, 2);
        DataDependsMetric dataDependsWriteDataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service3", "y", service3Map, 2);
        dataDependsMetrics.add(dataDependsReadDataDependsMetric4);
        dataDependsMetrics.add(dataDependsWriteDataDependsMetric2);
        dataDependsMetrics.add(dataDependsWriteDataDependsMetric3);
        Map<String, Double> dataDependsReadMap = new HashMap<>();
        Map<String, Double> dataDependsWriteMap = new HashMap<>();
        dataDependsReadMap.put("service1", 0.0);
        dataDependsReadMap.put("service2", 0.0);
        dataDependsReadMap.put("service3", 0.0);
        dataDependsReadMap.put("service4", 1.0);
        dataDependsWriteMap.put("service1", 0.0);
        dataDependsWriteMap.put("service2", 2.0);
        dataDependsWriteMap.put("service3", 2.0);
        dataDependsWriteMap.put("service4", 0.0);

        Result expected = new Result(dataDependsReadMap, dataDependsWriteMap, dataDependsMetrics, new ArrayList<>());

        Result actual = dataDependencyService.getDataDependsScore(new Model(new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_complex_read_trace())), new ArrayList<>(Collections.singleton(ExampleTraces.Get_example_complex_write_trace())), ExampleTraces.Get_example_complex_trace_read_endpoint_map(), ExampleTraces.Get_example_complex_trace_write_endpoint_map()));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void Test_get_data_depends_parallel() {
        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
        Map<String, Double> service2Map = new HashMap<>();
        service2Map.put("service1", 1.0);
        service2Map.put("service3", 0.5);
        Map<String, Double> service3Map = new HashMap<>();
        service3Map.put("service2", 0.5);
        Map<String, Double> service4Map = new HashMap<>();
        service4Map.put("service2", 1.0);
        Map<String, Double> service5Map = new HashMap<>();
        service5Map.put("service4", 0.5);
        DataDependsMetric dataDependsReadDataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service2", "y", service2Map, 1.118033988749895);
        DataDependsMetric dataDependsReadDataDependsMetric4 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service4", "y", service4Map, 1.0);
        DataDependsMetric dataDependsWriteDataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service3", "z", service3Map, 0.5);
        DataDependsMetric dataDependsWriteDataDependsMetric5 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service5", "y", service5Map,0.5);
        dataDependsMetrics.add(dataDependsReadDataDependsMetric2);
        dataDependsMetrics.add(dataDependsReadDataDependsMetric4);
        dataDependsMetrics.add(dataDependsWriteDataDependsMetric3);
        dataDependsMetrics.add(dataDependsWriteDataDependsMetric5);
        Map<String, Double> dataDependsReadMap = new HashMap<>();
        Map<String, Double> dataDependsWriteMap = new HashMap<>();
        dataDependsReadMap.put("service1", 0.0);
        dataDependsReadMap.put("service2", 1.118033988749895);
        dataDependsReadMap.put("service3", 0.0);
        dataDependsReadMap.put("service4", 1.0);
        dataDependsReadMap.put("service5", 0.0);
        dataDependsWriteMap.put("service1", 0.0);
        dataDependsWriteMap.put("service2", 0.0);
        dataDependsWriteMap.put("service3", 0.5);
        dataDependsWriteMap.put("service4", 0.0);
        dataDependsWriteMap.put("service5", 0.5);

        Result expected = new Result(dataDependsReadMap, dataDependsWriteMap, dataDependsMetrics, new ArrayList<>());

        Result actual = dataDependencyService.getDataDependsScore(new Model(ExampleTraces.Get_example_parallel_read_trace(), ExampleTraces.Get_example_parallel_write_trace(), ExampleTraces.Get_example_parallel_read_trace_endpoint_map(), ExampleTraces.Get_example_parallel_write_trace_endpoint_map()));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void Test_get_data_depends_parallel_with_dup() {
        ArrayList<DataDependsMetric> dataDependsMetrics = new ArrayList<>();
        Map<String, Double> service2Map = new HashMap<>();
        service2Map.put("service1", 2.0);
        service2Map.put("service3", 2.0);
        Map<String, Double> service3Map = new HashMap<>();
        service3Map.put("service2", 2.0);
        Map<String, Double> service4Map = new HashMap<>();
        service4Map.put("service2", 2.0);
        Map<String, Double> service5Map = new HashMap<>();
        service5Map.put("service4", 1.0);
        DataDependsMetric dataDependsReadDataDependsMetric2 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service2", "y", service2Map, 2.8284271247461903);
        DataDependsMetric dataDependsReadDataDependsMetric4 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service4", "y", service4Map, 2);
        DataDependsMetric dataDependsReadDataDependsMetric3 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_READ, "service3", "z", service3Map, 2);
        DataDependsMetric dataDependsWriteDataDependsMetric5 = new DataDependsMetric(DataDependsType.DATA_DEPENDS_WRITE, "service5", "y", service5Map, 1);
        dataDependsMetrics.add(dataDependsReadDataDependsMetric2);
        dataDependsMetrics.add(dataDependsReadDataDependsMetric4);
        dataDependsMetrics.add(dataDependsReadDataDependsMetric3);
        dataDependsMetrics.add(dataDependsWriteDataDependsMetric5);
        Map<String, Double> dataDependsReadMap = new HashMap<>();
        Map<String, Double> dataDependsWriteMap = new HashMap<>();
        dataDependsReadMap.put("service1", 0.0);
        dataDependsReadMap.put("service2", 2.8284271247461903);
        dataDependsReadMap.put("service3", 2.0);
        dataDependsReadMap.put("service4", 2.0);
        dataDependsReadMap.put("service5", 0.0);
        dataDependsWriteMap.put("service1", 0.0);
        dataDependsWriteMap.put("service2", 0.0);
        dataDependsWriteMap.put("service3", 0.0);
        dataDependsWriteMap.put("service4", 0.0);
        dataDependsWriteMap.put("service5", 1.0);

        Result expected = new Result(dataDependsReadMap, dataDependsWriteMap, dataDependsMetrics, new ArrayList<>());

        Result actual = dataDependencyService.getDataDependsScore(new Model(ExampleTraces.Get_example_parallel_read_trace_with_dup(), ExampleTraces.Get_example_parallel_write_trace_with_dup(), ExampleTraces.Get_example_parallel_read_trace_endpoint_map_with_dup(), ExampleTraces.Get_example_parallel_write_trace_endpoint_map_with_dup()));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void Test_get_data_depends_need() {
        DataDependsNeedScore dataDependsNeedScore = new DataDependsNeedScore("service2", "i1", 0.6666666666666666);
        DataDependsNeedScore dataDependsNeedScore2 = new DataDependsNeedScore("service2", "i2", 0.3333333333333333);
        Set<DataDependsNeedScore> dataDependsNeedScoreSet1 = Set.of(dataDependsNeedScore, dataDependsNeedScore2);
        DataDependsNeedMetric dataDependsNeedMetricRead1 = new DataDependsNeedMetric("service1", DataDependsType.DATA_DEPENDS_READ, 3, dataDependsNeedScoreSet1);

        DataDependsNeedScore dataDependsNeedScore3 = new DataDependsNeedScore("service3", "i3", 1.0);
        Set<DataDependsNeedScore> dataDependsNeedScoreSet2 = Set.of(dataDependsNeedScore3);
        DataDependsNeedMetric dataDependsNeedMetricRead2 = new DataDependsNeedMetric("service2", DataDependsType.DATA_DEPENDS_READ, 1, dataDependsNeedScoreSet2);

        DataDependsNeedMetric dataDependsNeedMetricRead3 = new DataDependsNeedMetric("service3", DataDependsType.DATA_DEPENDS_READ, 0, new HashSet<>());

        DataDependsNeedScore dataDependsNeedScore4 = new DataDependsNeedScore("service2", "i4", 1.0);
        Set<DataDependsNeedScore> dataDependsNeedScoreSet3 = Set.of(dataDependsNeedScore4);
        DataDependsNeedMetric dataDependsNeedMetricWrite1 = new DataDependsNeedMetric("service1", DataDependsType.DATA_DEPENDS_WRITE, 3, dataDependsNeedScoreSet3);

        DataDependsNeedMetric dataDependsNeedMetricWrite2 = new DataDependsNeedMetric("service2", DataDependsType.DATA_DEPENDS_WRITE, 0, new HashSet<>());

        DataDependsNeedMetric dataDependsNeedMetricWrite3 = new DataDependsNeedMetric("service3", DataDependsType.DATA_DEPENDS_WRITE, 0, new HashSet<>());

        List<DataDependsNeedMetric> expected = List.of(dataDependsNeedMetricRead1, dataDependsNeedMetricRead2, dataDependsNeedMetricWrite1, dataDependsNeedMetricWrite2, dataDependsNeedMetricRead3 ,dataDependsNeedMetricWrite3);

        List<DataDependsNeedMetric> actual = dataDependencyService.calculateDataDependsNeedMetrics(new Model(ExampleTraces.Get_example_read_traces_short(), ExampleTraces.Get_example_write_traces_short(), ExampleTraces.Get_example_parallel_read_trace_endpoint_map_short(), ExampleTraces.Get_example_parallel_write_trace_endpoint_map_short()));

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }


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
