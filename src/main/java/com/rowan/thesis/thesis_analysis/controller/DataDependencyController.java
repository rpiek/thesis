package com.rowan.thesis.thesis_analysis.controller;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.metric.Result;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.service.DataDependencyService;
import com.rowan.thesis.thesis_analysis.service.TraceService;
import com.rowan.thesis.thesis_analysis.utility.JsonReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/data-dependency")
public class DataDependencyController {

    private final TraceService traceService = new TraceService();

    private final DataDependencyService dataDependencyService = new DataDependencyService();

    @Autowired
    JsonReader jsonReader;

    @PostMapping("/analyze")
    public @ResponseBody ResponseEntity<Result> calculateDataDependency(@RequestBody List<List<Span>> traces) {
        log.info("Traces amount: " + traces.size());
        Model model = traceService.tracesToModel(traces);
        Result result = dataDependencyService.getDataDependsScore(model);
        result.setDataDependsNeedMetrics(dataDependencyService.calculateDataDependsNeedMetrics(model));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/read")
    public @ResponseBody ResponseEntity<Result> calculateDataDependsRead(@RequestBody List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        Result result = dataDependencyService.getDataDependsScore(model);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/write")
    public @ResponseBody ResponseEntity<Result> calculateDataDependsWrite(@RequestBody List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        Result result = dataDependencyService.getDataDependsScore(model);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/need")
    public @ResponseBody ResponseEntity<Result> calculateDataDependsNeed(@RequestBody List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        Result result = new Result(0.0, 0.0, new HashMap<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>());
        result.setDataDependsNeedMetrics(dataDependencyService.calculateDataDependsNeedMetrics(model));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/analyze/file")
    public @ResponseBody ResponseEntity<Result> calculateDataDependsOfFile() throws IOException {
        List<List<Span>> traces = jsonReader.readJsonFile();

        log.info("Traces amount:" + traces.size());

        Model model = traceService.tracesToModel(traces);
        Result result = dataDependencyService.getDataDependsScore(model);
        result.setDataDependsNeedMetrics(dataDependencyService.calculateDataDependsNeedMetrics(model));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
