package com.rowan.thesis.thesis_analysis.controller;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.model.metric.Result;
import com.rowan.thesis.thesis_analysis.model.trace.Model;
import com.rowan.thesis.thesis_analysis.service.DataDependencyService;
import com.rowan.thesis.thesis_analysis.service.TraceService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data-dependency")
public class DataDependencyController {

    private final TraceService traceService = new TraceService();

    private final DataDependencyService dataDependencyService = new DataDependencyService();

    @PostMapping("/analyze")
    public @ResponseBody ResponseEntity<Result> calculateDataDependency(@RequestBody List<List<Span>> traces) {
        Model model = traceService.tracesToModel(traces);
        Result result = dataDependencyService.getDataDependsScore(model);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @PostMapping("/analyze/DataDependsRead")
//    public @ResponseBody ResponseEntity<Result> calculateDataDependsRead(@RequestBody List<List<Span>> traces) {
//        Model model = traceService.tracesToModel(traces);
////        Result result = dataDependencyService.getDataDependsScore(model);
//
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
//
//    @PostMapping("/analyze/dataDependsWrite")
//    public @ResponseBody ResponseEntity<Result> calculateDataDependsWrite(@RequestBody List<List<Span>> traces) {
//        Model model = traceService.tracesToModel(traces);
////        Result result = dataDependencyService.getDataDependsScore(model);
//
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }


}
