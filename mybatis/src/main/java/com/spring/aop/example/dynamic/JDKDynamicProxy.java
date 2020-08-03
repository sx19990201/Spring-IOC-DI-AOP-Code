package com.spring.aop.example.dynamic;

import com.spring.aop.example.HelloAOP;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理目标为接口的JDK动态代理demo
 * aop：aspectj-autoProxy proxy-target-class = false
 */
public class JDKDynamicProxy implements InvocationHandler {

    //被代理的目标对象;
    private HelloAOP helloAOP;

    public JDKDynamicProxy(HelloAOP helloAOP){
        this.helloAOP=helloAOP;
    }

    @Override   //proxy 代理对象  method 目标所有的方法， 方法参数
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //动态代理，前置增强处理
        before();
        //代理方法执行，代理的目标实例，方法执行参数
        Object result = method.invoke(helloAOP,args);
        //动态代理，后置增强
        after();
        return result;
    }

    //生成代理对象并返回，
    public HelloAOP getProxy(){
        //生成代理对象，并返回
        return (HelloAOP) Proxy.newProxyInstance(
                //加载委托类
                this.helloAOP.getClass().getClassLoader(),
                //获取委托类里所有方法
                this.helloAOP.getClass().getInterfaces(),
                //代理对象执行的方法体
                this
        );
    }

    public void before(){
        System.out.println("JDK前置增强");
    }
    public void after(){
        System.out.println("JDK后置增加");
    }
}
