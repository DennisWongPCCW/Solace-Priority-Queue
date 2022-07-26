package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class SentRequest {
    @JsonProperty
    private String id;
    @JsonProperty
    private String payload;
    @JsonProperty
    private String status;
    @JsonProperty
    private String createdBy;
    @JsonProperty
    private String createdDate;
    @JsonProperty
    private String remark;

    public SentRequest() {

    }
}
