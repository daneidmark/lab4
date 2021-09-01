package se.nackademin.java20.lab1.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SignUpRequest {
    private final String username;
    private final String password;
    private final List<String> roles;

    @JsonCreator
    public SignUpRequest(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("roles") List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = List.copyOf(roles);
    }


    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
       return List.copyOf(roles);
    }
}
