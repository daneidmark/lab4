package se.nackademin.java20.lab1.persistance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.nackademin.java20.lab1.domain.Account;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ContextConfiguration(initializers = AccountRepositoryHibernateIT.Lab1ApplicationTestsContextInitializer.class)
class AccountRepositoryHibernateIT {

    @Container
    private static final MySQLContainer db = new MySQLContainer("mysql:8.0.26").withPassword("password");

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void shouldSaveAndFindFromTheDatabase() {
        final AccountRepositoryHibernate repositoryHibernate = new AccountRepositoryHibernate(testEntityManager.getEntityManager());
        final Account account = repositoryHibernate.save(new Account("Dan", 10));
        final Optional<Account> actual = repositoryHibernate.findByUserIdAndAccountId(account.getHolder(), account.getId());
        assertTrue(actual.isPresent());
        assertEquals(actual.get().getId(), account.getId());
    }

    public static class Lab1ApplicationTestsContextInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String host = db.getJdbcUrl();
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    configurableApplicationContext,
                    "spring.datasource.url=" + host, "flyway.url=" + host);

        }
    }
}