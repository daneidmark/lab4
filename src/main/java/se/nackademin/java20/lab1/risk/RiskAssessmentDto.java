package se.nackademin.java20.lab1.risk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RiskAssessmentDto {
    private final boolean pass;

    @JsonCreator
    public RiskAssessmentDto(@JsonProperty("pass") boolean pass) {
        this.pass = pass;
    }

    public boolean isPass() {
        return pass;
    }

    @Override
    public String toString() {
        return "RiskAssessmentDto{" +
                "pass=" + pass +
                '}';
    }
}
