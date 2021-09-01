package se.nackademin.java20.lab1.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import se.nackademin.java20.lab1.domain.UserService;

import java.util.List;

public class RestUserService implements UserService {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RestUserService(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public void enroll(String holder, String password) {
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/user/signup", new SignUpRequest(holder, password, List.of("CUSTOMER")), String.class);
        if (responseEntity.getStatusCode().isError()) {
            throw new RuntimeException("Could not create user");
        }
    }
}
