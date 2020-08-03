package com.spring.DI.springTest;

import com.spring.DI.annotation.MyComponent;
import com.spring.DI.applicationContext.AnnotationConfigApplicationContext;
import com.spring.DI.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@MyComponent
public class TestSpringDi {
    //创建AnnotationConfigApplicationContext工厂
    AnnotationConfigApplicationContext ctx;
    //创建UserService对象
    UserService userService;
    /**
     * 初始化方法
     */
    @Before
    public void init() {
        //实例工厂类，传入pojo/service/test三个包路径进行扫描
        ctx = new AnnotationConfigApplicationContext
                ("com.spring.DI.pojo",
                        "com.spring.DI.service", "com.spring.DI.springTest");

        userService = ctx.getBean("userService", UserService.class);
    }

    @Test
    public void userLogin() {
        userService.userLogin();
    }

    /**
     * 销毁方法
     */
    @After
    public void close() {
        ctx.close();
    }

}
