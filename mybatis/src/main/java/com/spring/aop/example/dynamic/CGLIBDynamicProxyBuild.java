package com.spring.aop.example.dynamic;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 封装通用的CGLIB动态代理类
 */
public class CGLIBDynamicProxyBuild implements MethodInterceptor {


    //代理类实例
    private static CGLIBDynamicProxyBuild  dynamicProxyBuild = new CGLIBDynamicProxyBuild();

    /**
     * 私有的构造，外界无法创建动态代理类实例
     */
    private CGLIBDynamicProxyBuild(){ }

    /**
     * 单例模式，饿汉模式：类加载速度慢，获取对象速度快，线程安全，不能延迟加载
     * @return
     */
    public static CGLIBDynamicProxyBuild getInstance(){
        return dynamicProxyBuild;
    }
    //创建动态代理类实例
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> cls){
        return (T) Enhancer.create(cls,this);
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        before();
        Object result = methodProxy.invokeSuper(target,args);
        after();
        return result;
    }

    public void before(){
        System.out.println("CGLIB动态代理封装----前置增强");
    }
    public void after(){
        System.out.println("CGLIB动态代理封装----后置增强");
    }


}
