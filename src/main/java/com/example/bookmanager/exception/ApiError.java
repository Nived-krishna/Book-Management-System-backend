package com.example.bookmanager.exception;

public class ApiError {
    private String message;
    private Object details;

    public ApiError() {}

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(String message, Object details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getDetails() { return details; }
    public void setDetails(Object details) { this.details = details; }
}
