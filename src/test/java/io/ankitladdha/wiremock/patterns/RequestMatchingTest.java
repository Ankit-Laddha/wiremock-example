package io.ankitladdha.wiremock.patterns;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RequestMatchingTest {

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
    @DisplayName("Should ignore the request method and URL")
    void shouldIgnoreRequestMethod() {
        givenThat(any(anyUrl()).willReturn(aResponse().withStatus(200)));

        String serverUrl = buildApiMethodUrl(1L);
        var response = this.restTemplate.postForEntity(serverUrl, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should compare the actual request method with the expected method")
    void shouldCompareActualRequestMethodWithExpectedRequestMethod() {
        givenThat(get(anyUrl()).willReturn(aResponse().withStatus(200)));

        String serverUrl = buildApiMethodUrl(1L);
        var response = this.restTemplate.getForEntity(serverUrl, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should compare the actual URL with the exact expected URL")
    void shouldCompareActualUrlWithExactExpectedUrl() {
        givenThat(get(urlEqualTo("/api/message?id=1")).willReturn(aResponse()
                .withStatus(200))
        );

        String serverUrl = buildApiMethodUrl(1L);
        var response = restTemplate.getForEntity(serverUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should compare the actual URL with the expected URL regex")
    void shouldCompareActualUrlWithExpectedUrlRegex() {
        givenThat(get(urlMatching("/api/([a-z]*)\\?id=1"))
                .willReturn(aResponse().withStatus(200))
        );

        String serverUrl = buildApiMethodUrl(1L);
        var response = restTemplate.getForEntity(serverUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should compare the actual URL path with the expected URL path")
    void shouldCompareActualUrlWithExactExpectedUrlPath() {
        givenThat(get(urlPathEqualTo("/api/message"))
                .willReturn(aResponse().withStatus(200))
        );

        String serverUrl = buildApiMethodUrl(1L);
        var response = restTemplate.getForEntity(serverUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should compare the actual URL path with the expected path regex")
    void shouldCompareActualUrlWithExpectedUrlPathRegex() {
        givenThat(get(urlPathMatching("/api/([a-z]*)"))
                .willReturn(aResponse().withStatus(200))
        );

        String serverUrl = buildApiMethodUrl(1L);
        var response = restTemplate.getForEntity(serverUrl, String.class);
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
