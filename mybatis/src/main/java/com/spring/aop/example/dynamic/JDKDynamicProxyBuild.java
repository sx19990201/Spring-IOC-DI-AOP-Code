package com.spring.aop.example.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 封装通用的JDK动态代理类
 *
 * 基于单例模式封装通用的jdk动态代理类
 */
public class JDKDynamicProxyBuild implements InvocationHandler {

    //目标委托实例
    private Object target;

    /**
     * 构造注入委托实例
     * @param target
     */
    public JDKDynamicProxyBuild(Object target){
        this.target=target;
    }

    /**
     * 生成动态代理类
     * @param obj
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<T> obj){
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(), //获取加载委托类
                target.getClass().getInterfaces(),  //获得委托类需要实现的所有接口
                this    //执行动态代理方法
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        Object result = method.invoke(target,args);
        after();
        return result;
    }

    public void before(){
        System.out.println("JDK动态代理封装----前置增强");
    }
    public void after(){
        System.out.println("JDK动态代理封装----后置增强");
    }

}
