package com.rowan.thesis.thesis_analysis.model.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Span {

    @JsonProperty("traceId")
    String traceId;

    @JsonProperty("id")
    String spanId;

    @JsonProperty("parentId")
    String parentId;

    @JsonProperty("name")
    String path;

    @JsonProperty("kind")
    String kind;

    @JsonProperty("localEndpoint")
    LocalEndpoint localEndpoint;

//    @JsonProperty("start_time")
//    ZonedDateTime startTime;
//
//    @JsonProperty("end_time")
//    ZonedDateTime endTime;

//    @JsonProperty("status_code")
//    StatusCode statusCode;

    @JsonProperty("tags")
    Tags tags;

}
