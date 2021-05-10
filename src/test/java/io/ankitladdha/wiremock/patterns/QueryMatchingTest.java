package io.ankitladdha.wiremock.patterns;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class QueryMatchingTest {

    private RestTemplate restTemplate;
    private WireMockServer wireMockServer;

    @BeforeEach
    void configureSystemUnderTest() {
        this.restTemplate = new RestTemplate();
        this.wireMockServer = new WireMockServer(options()
                .dynamicPort()
        );
        this.wireMockServer.start();
        configureFor("localhost", this.wireMockServer.port());
    }

    @Test
    @DisplayName("Should compare the actual value with the exact expected value")
    void shouldCompareActualUrlWithExactExpectedUrlWithQueryParameter() {
        givenThat(get(urlPathEqualTo("/api/message"))
                .withQueryParams(Map.of("id", equalTo("1"), "name", equalTo("Ankit")))
                .willReturn(aResponse().withStatus(200))
        );

        String apiMethodUrl = String.format("http://localhost:%d/api/message?id=%d&name=%s",
                this.wireMockServer.port(),
                1L,
                "Ankit"
        );

        ResponseEntity<String> response = restTemplate.getForEntity(apiMethodUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    @DisplayName("Should compare the actual value with the exact expected value")
    void shouldCompareActualUrlWithExactExpectedUrlWithHeaderAccept() {
        givenThat(get(urlEqualTo("/api/message?id=1"))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .willReturn(aResponse().withStatus(200))
        );

        String apiMethodUrl = buildApiMethodUrl(1L);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(
                Collections.singletonList(MediaType.APPLICATION_JSON_UTF8)
        );
        HttpEntity<String> httpRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                HttpMethod.GET,
                httpRequest,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should ensure that request has correct username and password")
    void shouldEnsureThatRequestHasCorrectUsernameAndPassword() {
        givenThat(get(urlEqualTo("/api/message?id=1"))
                .withBasicAuth("username", "password")
                .willReturn(aResponse().withStatus(200))
        );

        String apiMethodUrl = buildApiMethodUrl(1L);

        HttpHeaders headers = new HttpHeaders();
        String auth = "username:password";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.defaultCharset()));

        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        HttpEntity<String> httpRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                HttpMethod.GET,
                httpRequest,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    @DisplayName("Should compare the actual value with the expected value")
    void shouldCompareActualUrlWithExactExpectedUrlWithHeader() {
        givenThat(get(urlEqualTo("/api/message?id=1"))
                .withCookie("name", equalTo("Ankit"))
                .willReturn(aResponse().withStatus(200))
        );

        String apiMethodUrl = buildApiMethodUrl(1L);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "name=Ankit");
        HttpEntity<String> httpRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                HttpMethod.GET,
                httpRequest,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String buildApiMethodUrl(Long messageId) {
        return String.format("http://localhost:%d/api/message?id=%d",
                this.wireMockServer.port(),
                messageId
        );
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }


}
