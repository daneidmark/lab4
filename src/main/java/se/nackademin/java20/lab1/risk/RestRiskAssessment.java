package se.nackademin.java20.lab1.risk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import se.nackademin.java20.lab1.domain.RiskAssesment;

public class RestRiskAssessment implements RiskAssesment {
    private static final Logger LOG = LoggerFactory.getLogger(RiskAssesment.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RestRiskAssessment(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean isPassingCreditCheck(String userId) {
        final ResponseEntity<RiskAssessmentDto> entity = restTemplate.getForEntity(baseUrl + "/risk/" + userId, RiskAssessmentDto.class);
        LOG.info("Got status {}", entity.getStatusCodeValue());
        if (entity.getStatusCode().is2xxSuccessful()) {
            LOG.info("Got body {}", entity.getBody());
            return entity.getBody().isPass();
        }

        throw new RuntimeException("Could not fetch risk assessment! got response " + entity.getStatusCodeValue());
    }
}
