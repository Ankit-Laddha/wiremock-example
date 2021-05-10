package io.ankitladdha.wiremock.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomHostAndPortConfigurationTest {

    private RestTemplate restTemplate;
    private WireMockServer wireMockServer;

    @BeforeEach
    void configureSystemUnderTest() {
        this.restTemplate = new RestTemplate();
        this.wireMockServer = new WireMockServer(options().port(9999));
        this.wireMockServer.start();

        configureFor("localhost", 9999);
    }

    @Test
    @DisplayName("Should ensure that WireMock server was started")
    void shouldEnsureThatServerWasStarted() {
        stubFor(get(urlEqualTo("/custom")).willReturn(aResponse()
                .withStatus(200)
        ));

        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9999/custom", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }
}
