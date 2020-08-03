package com.spring.ioc.ioc_annotation.pojo;

import com.spring.ioc.ioc_annotation.annotation.MyComponent;
import com.spring.ioc.ioc_annotation.annotation.MyValue;

@MyComponent(scope = "prototype")
public class PrototypeUser {

    @MyValue(value = "1")
    private Integer id;

    @MyValue(value = "南瓜")
    private String name;

    @MyValue(value = "654321")
    private String password;

    public PrototypeUser() {
        System.out.println("无参构造方法执行");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "PrototypeUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
