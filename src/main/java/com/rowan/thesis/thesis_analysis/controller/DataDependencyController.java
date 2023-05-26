package com.rowan.thesis.thesis_analysis.controller;

import com.rowan.thesis.thesis_analysis.model.input.Span;
import com.rowan.thesis.thesis_analysis.service.DataDependencyService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Slf4j
@RequestMapping("/data-autonomy")
public class DataDependencyController {

    private final DataDependencyService dataDependencyService;

    public DataDependencyController(DataDependencyService dataDependencyService) {
        this.dataDependencyService = dataDependencyService;
    }

    @PostMapping("/analyze")
    public @ResponseBody ResponseEntity<String> post(@RequestBody List<List<Span>> traces) {
        dataDependencyService.getDataAutonomyScore(traces);
        return new ResponseEntity<String>("POST Response", HttpStatus.OK);
    }

}
