package io.paymeter.assessment.infrastructure.entrypoints.status;

import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.TEXT_PLAIN;

class HealthControllerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        HealthController healthController = new HealthController();
        this.webTestClient = WebTestClient.bindToController(healthController).build();
    }

    @Test
    void statusReturnOkAnd200() {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(new MediaType(TEXT_PLAIN, StandardCharsets.UTF_8))
                .expectBody(String.class)
                .isEqualTo("OK");
    }
}
