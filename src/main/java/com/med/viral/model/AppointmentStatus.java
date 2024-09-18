package com.med.viral.model;

public enum AppointmentStatus {
    OPEN ("OPEN"),
    CLOSED ("CLOSED"),
    CANCELLED ("CANCELLED");

    private String value;

    AppointmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
