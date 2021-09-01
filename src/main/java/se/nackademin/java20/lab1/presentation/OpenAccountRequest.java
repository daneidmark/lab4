package se.nackademin.java20.lab1.presentation;

public class OpenAccountRequest {
    private final String username;
    private final String password;

    public OpenAccountRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return username;
    }
}
