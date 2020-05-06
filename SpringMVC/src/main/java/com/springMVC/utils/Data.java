package com.springMVC.utils;

/**
 * 封装响应结果
 * 输出响应数据类
 * 控制器处理方法直接返回响应的数据，适用于ajax或直接写入输入流的情况
 */
public class Data {
    //输出数据
    private Object data;

    public Data() {
    }

    /**
     * 构造注入输出数据
     * @param data
     */
    public Data(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
