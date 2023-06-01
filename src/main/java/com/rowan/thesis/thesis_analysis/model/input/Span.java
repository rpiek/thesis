package com.rowan.thesis.thesis_analysis.model.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("timestamp")
    Long timeStamp;

    @JsonProperty("tags")
    Tags tags;

}
