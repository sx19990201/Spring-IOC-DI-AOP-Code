package com.MySpringMVC.myController;

import com.MySpringMVC.annotation.MyRequstMapping;

@com.MySpringMVC.annotation.MyController
@MyRequstMapping("/extIndex")   //       1、/extIndex/test
public class MyController {
    @MyRequstMapping("/test")
    public String test() {
        System.out.println("手写springmvc");

        return "test";
    }
}
