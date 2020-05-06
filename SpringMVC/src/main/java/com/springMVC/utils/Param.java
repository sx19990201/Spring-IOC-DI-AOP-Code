package com.springMVC.utils;

import java.util.Map;

/**
 * 封装请求参数
 */
public class Param {
    private Map<String, Object> paramMap;

    public Param() {
    }

    /**
     * 构造注入请求参数
     * @param paramMap
     */
    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
}
