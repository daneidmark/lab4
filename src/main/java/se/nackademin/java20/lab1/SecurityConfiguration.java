package se.nackademin.java20.lab1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import se.nackademin.java20.lab1.security.JWTAuthorizationFilter;
import se.nackademin.java20.lab1.security.JWTParser;

@Configuration
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JWTParser jwtParser;

    @Autowired
    SecurityConfiguration(JWTParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final JWTAuthorizationFilter filter = new JWTAuthorizationFilter(authenticationManager(), jwtParser);

        http
                .csrf()
                .disable()
                .cors()
                .disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/partial/*").permitAll()
                .antMatchers("/admin/*").hasRole("ADMIN")
                .antMatchers("/account/*").hasRole("CUSTOMER")
                .anyRequest().authenticated().and()
                .addFilter(filter)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }


}