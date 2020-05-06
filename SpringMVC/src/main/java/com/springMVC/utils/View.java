package com.springMVC.utils;

import java.util.Map;

/**
 * 视图解析器类
 * 定义视图名称和绑定的模型参数
 */
public class View {
    //视图名称
    private String viewName;
    //绑定的模型参数
    private Map<String,Object> modelParams;

    public View() {
    }

    /**
     * 构造注入视图名称和绑定的模型参数
     * @param viewName
     * @param modelParams
     */
    public View(String viewName, Map<String, Object> modelParams) {
        this.viewName = viewName;
        this.modelParams = modelParams;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModelParams() {
        return modelParams;
    }

    public void setModelParams(Map<String, Object> modelParams) {
        this.modelParams = modelParams;
    }
}
