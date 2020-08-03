package com.spring.aop.example;

/**
 * 被代理的接口，基于jdk动态代理
 */
public interface HelloAOP {

    //被动态的目标方法
    public void show(String name);

}
