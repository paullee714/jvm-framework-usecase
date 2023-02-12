package com.example.springcontrolleradvice.controller.exception;

public class CustomException extends RuntimeException {
    private String message;
    private String errorCode;
    private String hint;

    protected CustomException() {
    }

    public CustomException(String message, String errorCode, String hint) {
        this.message = message;
        this.errorCode = errorCode;
        this.hint = hint;
    }


    // generate getters and setters


    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
