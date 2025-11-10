package io.paymeter.assessment.infrastructure.entrypoints.parking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
public class ResponseDTO<T> {

    @Autowired
    private MetaDTO.Meta meta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T errors;

    public static <T> ResponseDTO success(T data, ServerHttpRequest request) {
        return ResponseDTO.builder()
                .meta(MetaDTO.build(data,request))
                .data(data)
                .build();
    }

    public static <T> ResponseDTO buildError(T errors,ServerHttpRequest request) {
        return ResponseDTO.builder()
                .meta(MetaDTO.build(errors,request))
                .errors(errors)
                .build();
    }


}
