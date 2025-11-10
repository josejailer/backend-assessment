package io.paymeter.assessment.infrastructure.entrypoints.parking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MetaDTO {

    private static String deploymentVersion = "0.0.1";

    @Data
    @Builder(toBuilder = true)
    public static class Meta {

        @JsonProperty("_version")
        private String version;

        @JsonProperty("_requestDate")
        private LocalDateTime requestDate;

        @JsonProperty("_requestClient")
        private String requestClient;

    }

    public static <T> Meta build(T data, ServerHttpRequest request) {
        return Meta.builder()
                .version(deploymentVersion)
                .requestDate(LocalDateTime.now())
                .requestClient(validateAddress(request))
                .build();
    }

    private static String validateAddress(ServerHttpRequest request) {
        return Optional.ofNullable(request.getRemoteAddress())
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .orElse("");
    }

}
