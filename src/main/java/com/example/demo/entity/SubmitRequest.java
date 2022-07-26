package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class SubmitRequest {
    @JsonProperty
    public String type;
    @JsonProperty
    public String path;
    @JsonProperty
    public Object payload;
}
