package com.springMVC.utils;

import java.lang.reflect.Method;

/**
 * 封装处理请求的控制器和处理方法
 *
 * 请求处理类
 * 封装处理请求的控制器和处理方法
 */
public class Handle {

    //处理请求控制器
    private Class<?> handleController;

    //处理请求方法
    private Method handleMethod;

    public Handle() {
    }
    //构造注入处理请求的控制器和方法
    public Handle(Class<?> handleController, Method handleMethod) {
        this.handleController = handleController;
        this.handleMethod = handleMethod;
    }

    public Class<?> getHandleController() {
        return handleController;
    }

    public void setHandleController(Class<?> handleController) {
        this.handleController = handleController;
    }

    public Method getHandleMethod() {
        return handleMethod;
    }

    public void setHandleMethod(Method handleMethod) {
        this.handleMethod = handleMethod;
    }
}
