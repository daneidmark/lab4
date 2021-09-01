package se.nackademin.java20.lab1;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import se.nackademin.java20.lab1.application.PersonalFinanceService;
import se.nackademin.java20.lab1.domain.RiskAssesment;
import se.nackademin.java20.lab1.domain.UserService;
import se.nackademin.java20.lab1.persistance.AccountRepository;
import se.nackademin.java20.lab1.persistance.AccountRepositoryHibernate;
import se.nackademin.java20.lab1.risk.RestRiskAssessment;
import se.nackademin.java20.lab1.security.JWTParser;
import se.nackademin.java20.lab1.user.RestUserService;

import javax.net.ssl.SSLContext;
import javax.persistence.EntityManager;

@Configuration
public class ApplicationConfiguration {
    @Value("${server.ssl.enabled}")
    private boolean enabled;
    @Value("${server.ssl.trust-store-password}")
    private String trustStorePassword;
    @Value("${server.ssl.trust-store}")
    private Resource trustStore;
    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${server.ssl.key-password}")
    private String keyPassword;
    @Value("${server.ssl.key-store}")
    private Resource keyStore;
    @Value("${app.risk-service-url}")
    private String riskServiceBaseUrl;
    @Value("${app.user-service-url}")
    private String userServiceBaseUrl;

    @Bean
    public AccountRepository accountRepository(EntityManager em) {
        return new AccountRepositoryHibernate(em);
    }

    @Bean
    public PersonalFinanceService personalFinanceService(AccountRepository accountRepository, RiskAssesment riskAssessment, UserService userService) {
        return new PersonalFinanceService(accountRepository, riskAssessment, userService);
    }

    @Bean
    public RiskAssesment riskAssesment(RestTemplate restTemplate) {
        return new RestRiskAssessment(restTemplate, riskServiceBaseUrl);
    }

    @Bean
    public UserService userService() {
        return new RestUserService(new RestTemplate(), userServiceBaseUrl);
    }

    @Bean
    public RestTemplate restTemplate() throws Exception {
        final RestTemplate restTemplate;
        if (enabled) {
            restTemplate = new RestTemplate(clientHttpRequestFactory());
        } else {
            restTemplate = new RestTemplate();
        }
        restTemplate.setErrorHandler(
                new DefaultResponseErrorHandler() {
                    @Override
                    protected boolean hasError(HttpStatus statusCode) {
                        return false;
                    }
                });

        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() throws Exception {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    private CloseableHttpClient httpClient() throws Exception {
        // Load our keystore and truststore containing certificates that we trust.
        SSLContext sslcontext =
                SSLContextBuilder.create().loadTrustMaterial(trustStore.getFile(), trustStorePassword.toCharArray())
                        .loadKeyMaterial(keyStore.getFile(), keyStorePassword.toCharArray(),
                                keyPassword.toCharArray()).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    @Bean
    public JWTParser jwtParser() {
        return new JWTParser();
    }
}
