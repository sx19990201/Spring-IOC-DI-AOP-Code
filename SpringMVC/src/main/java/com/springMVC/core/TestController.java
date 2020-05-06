package com.springMVC.core;

import com.springMVC.annotation.MyController;
import com.springMVC.annotation.MyRequestMapping;
import com.springMVC.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MyController
@MyRequestMapping("/test")
public class TestController {

    @MyRequestMapping("/doTest")
    public void test1(HttpServletRequest request , HttpServletResponse response,
                      @MyRequestParam("param")String param){
        System.out.println(param);
        try {
            response.getWriter().write("test1 成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @MyRequestMapping("/doTest2")
    public void test2(HttpServletRequest request, HttpServletResponse response){
        try {
            response.getWriter().write(" test2 method success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
