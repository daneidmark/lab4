package se.nackademin.java20.lab1.risk;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

class RestRiskAssessmentIT {

    private WireMockServer mockRiskServer;

    @BeforeEach
    public void before() {
        mockRiskServer = new WireMockServer(9090);
        mockRiskServer.start();
    }

    @AfterEach
    public void after() {
        mockRiskServer.stop();
    }

    @Test
    void canMakeRiskAssessment() {
        mockRiskServer.stubFor(get(urlEqualTo("/risk/dan")).willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("content-type", APPLICATION_JSON.toString())
                .withBody("{\"pass\": true}")));
        final RestRiskAssessment restRiskAssessment = new RestRiskAssessment(new RestTemplate(), mockRiskServer.baseUrl());
        final boolean passingCreditCheck = restRiskAssessment.isPassingCreditCheck("dan");
        assertTrue(passingCreditCheck);
    }
}