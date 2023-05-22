package com.oc.rapportmicroservice.model;

import java.util.Objects;

public class Report {

    private Long patId;
    private String patFullName;
    private String diabetesAssessment;

    public Report() {
    }

    public Report(Long patId, String patFullName, String diabetesAssessment) {
        this.patId = patId;
        this.patFullName = patFullName;
        this.diabetesAssessment = diabetesAssessment;
    }

    public Long getPatId() {
        return patId;
    }

    public void setPatId(Long patId) {
        this.patId = patId;
    }

    public String getPatFullName() {
        return patFullName;
    }

    public void setPatFullName(String patFullName) {
        this.patFullName = patFullName;
    }

    public String getDiabetesAssessment() {
        return diabetesAssessment;
    }

    public void setDiabetesAssessment(String diabetesAssessment) {
        this.diabetesAssessment = diabetesAssessment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report rapport = (Report) o;
        return Objects.equals(patId, rapport.patId) && Objects.equals(patFullName, rapport.patFullName) && Objects.equals(diabetesAssessment, rapport.diabetesAssessment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patId, patFullName, diabetesAssessment);
    }

    @Override
    public String toString() {
        return "Report{" +
                "patId=" + patId +
                ", patFullName='" + patFullName + '\'' +
                ", diabetesAssessment='" + diabetesAssessment + '\'' +
                '}';
    }
}
