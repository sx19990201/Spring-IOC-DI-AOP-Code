package com.mybatis.config;

import java.util.List;

/**
 *  定义映射器接口与映射方法之间的关联关系
 */
public class MapperBean {
    private String interfaceName; //接口名 （完全限定名)
    private List<Function> list;    //该接口下所有的方法

    public MapperBean() {
    }

    public MapperBean(String interfaceName, List<Function> list) {
        this.interfaceName = interfaceName;
        this.list = list;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public List<Function> getList() {
        return list;
    }

    public void setList(List<Function> list) {
        this.list = list;
    }
}
