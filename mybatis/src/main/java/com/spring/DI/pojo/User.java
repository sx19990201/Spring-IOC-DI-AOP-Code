package com.spring.DI.pojo;

import com.spring.DI.annotation.MyComponent;
import com.spring.DI.annotation.MyValue;


/**
 * 实体类属性值暂用注解方式写死作为测试（实际中不会这样用），
 * 此实体暂为单例类（不注明scope属性默认为单例类）
 */
@MyComponent(scope = "prototype")
public class User {

    @MyValue(value = "1")
    private Integer id;
    @MyValue(value = "冬瓜")
    private String name;
    @MyValue(value = "123456")
    private String password;

    public User() {
        System.out.println("无参构造方法执行");
    }

    public void login() {
        System.out.println("用户登录：id = " + id + ", name" + name + ", password= " + password);
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
}
