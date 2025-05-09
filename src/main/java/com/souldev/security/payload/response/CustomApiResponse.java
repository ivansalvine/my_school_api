package com.souldev.security.payload.response;

public class CustomApiResponse {

    private Boolean success;
    private String message;
    private int statutCode;
    private Object data;

    public CustomApiResponse() {
    }

    public CustomApiResponse(Boolean success, String message, Object data, int statutCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statutCode = statutCode;
    }

    public CustomApiResponse(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statutCode = success ? 200 : 400;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatutCode() {
        return statutCode;
    }

    public void setStatutCode(int statutCode) {
        this.statutCode = statutCode;
    }
}