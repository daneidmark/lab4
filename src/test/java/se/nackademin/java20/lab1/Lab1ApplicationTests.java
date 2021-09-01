package se.nackademin.java20.lab1;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Testcontainers
@ContextConfiguration(initializers = Lab1ApplicationTests.Lab1ApplicationTestsContextInitializer.class)
@AutoConfigureMockMvc
class Lab1ApplicationTests {

    @Container
    private static final MySQLContainer db = new MySQLContainer("mysql:8.0.26").withPassword("password");

    @Autowired
    private MockMvc mockMvc;

    private final static WireMockServer wireMockServer = new WireMockServer(1234);

    @Test
    void contextLoads() {
    }

    @BeforeEach
    public void before() {
        wireMockServer.start();
    }

    @AfterEach
    public void after() {
        wireMockServer.stop();
    }

    @Test
    void shouldOpenAccountWhenRiskAssessmentPasses() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/risk/dan")).willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("content-type", APPLICATION_JSON.toString())
                .withBody("{\"pass\": true}")));

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/bank/dan/account"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/bank/dan/account/{[0-9]*}"))
                .andReturn();

        final String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        final String[] split = redirectedUrl.split("/");
        long accountId = Long.parseLong(split[split.length - 1]);

        mockMvc.perform(MockMvcRequestBuilders.get(redirectedUrl))
                .andExpect(status().isOk())
                .andExpect(model().attribute("balance", 0L))
                .andExpect(model().attribute("userId", "dan"))
                .andExpect(model().attribute("accountId", accountId));

    }


    public static class Lab1ApplicationTestsContextInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String host = db.getJdbcUrl();
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    configurableApplicationContext,
                    "spring.datasource.url=" + host,
                    "flyway.url=" + host,
                    "app.risk-service-url=http://localhost:1234"); // wiremock server

        }
    }
}
