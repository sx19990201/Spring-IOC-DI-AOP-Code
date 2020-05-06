package com.springMVC.utils;

/**
 * 请求信息类
 * 封装请求方式和路径
 */
public class Request {

    //请求方式
    private String requestMethod;
    //请求路径
    private String requestPath;

    public Request() {
    }
    //构造注入

    /**
     * 构造注入处理方式和路径
     * @param requestMethod 请求方式
     * @param requestPath   请求路径
     */
    public Request(String requestMethod, String requestPath) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }
}
