package com.rowan.thesis.thesis_analysis.controller;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.service.DataDependencyService;
import com.rowan.thesis.thesis_analysis.service.TraceService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
@RequestMapping("/data-dependency")
public class DataDependencyController {

    private final TraceService traceService = new TraceService();

    private final DataDependencyService dataDependencyService = new DataDependencyService(traceService);

    @PostMapping("/analyze")
    public @ResponseBody ResponseEntity<String> calculateDataDependency(@RequestBody List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        dataDependencyService.getDataAutonomyScore(model);
        return new ResponseEntity<String>("POST Response", HttpStatus.OK);
    }

    @PostMapping("/analyze/DataDependsRead")
    public @ResponseBody ResponseEntity<String> calculateDataDependensRead(@RequestBody List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        dataDependencyService.getDataAutonomyScore(model);
        return new ResponseEntity<String>("POST Response", HttpStatus.OK);
    }

    @PostMapping("/analyze/dataDependsWrite")
    public @ResponseBody ResponseEntity<String> calculateDataDependendsWrite(@RequestBody List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        dataDependencyService.getDataAutonomyScore(model);
        return new ResponseEntity<String>("POST Response", HttpStatus.OK);
    }

    @PostMapping("/analyze/DataDependsNeed")
    public @ResponseBody ResponseEntity<String> calculateDataDependensNeed(@RequestBody List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        dataDependencyService.getDataAutonomyScore(model);
        return new ResponseEntity<String>("POST Response", HttpStatus.OK);
    }

}
