package xyz.funny233.pod_user_info.controller;

public class Response {
    public int code;
    public String message;
    public Object data;

    public Response(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }
}
