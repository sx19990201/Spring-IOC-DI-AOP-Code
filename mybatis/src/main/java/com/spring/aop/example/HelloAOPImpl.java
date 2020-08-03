package com.spring.aop.example;

/**
 * aop的接口实现类
 */
public class HelloAOPImpl implements HelloAOP {
    @Override
    public void show(String name) {
        System.out.println("===========hello,"+name+"=============");
    }
}
