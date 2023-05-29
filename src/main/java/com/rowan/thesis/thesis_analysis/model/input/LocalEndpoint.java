package com.rowan.thesis.thesis_analysis.model.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalEndpoint {

    @JsonProperty("serviceName")
    private String serviceName;

}
