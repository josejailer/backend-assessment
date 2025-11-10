package io.paymeter.assessment.infrastructure.entrypoints.parking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class Meta {

    @JsonProperty("_version")
    private String version;

    @JsonProperty("_requestDate")
    private LocalDateTime requestDate;

    @JsonProperty("_responseSize")
    private int responseSize;

    @JsonProperty("_messageId")
    private String messageId;

    @JsonProperty("_requestClient")
    private String requestClient;
}
