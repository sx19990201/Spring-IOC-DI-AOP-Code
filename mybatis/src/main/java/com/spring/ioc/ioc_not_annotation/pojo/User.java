package com.spring.ioc.ioc_not_annotation.pojo;

public class User {
    private int id;
    private String name;
    private String password;

    public User() {
        System.out.println("无参构造函数执行");
    }

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        System.out.println("有参构造函数执行");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
