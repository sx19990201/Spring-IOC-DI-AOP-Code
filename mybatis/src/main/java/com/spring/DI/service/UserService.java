package com.spring.DI.service;

import com.spring.DI.annotation.MyAutowired;
import com.spring.DI.annotation.MyComponent;
import com.spring.DI.pojo.User;

@MyComponent
public class UserService {

    @MyAutowired
    User user1;

    @MyAutowired
    User user2;

    public void userLogin(){
        System.out.println("用户1："+user1);
        user1.login();

        System.out.println("用户2："+user2);
        user2.login();
    }
}
