package com.spring.aop.example.dynamic;

import com.spring.aop.example.HelloAOPImpl;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 代理任意目标类的CGLIB动态代理DEMO
 * CGLIB是一个强大、高性能的字节码生成库，它用于在运行是拓展，java类的实现接口；
 * 本质上他是通过动态的生成了一个子类去覆盖所要代理的类（非final修饰的类和方法）。
 */
public class CGLibDynamicProxy implements MethodInterceptor {

    //代理目标实例
    private HelloAOPImpl helloAOPImpl;

    //创建代理实例并返回
    public Object getInstance(HelloAOPImpl helloAOPImpl){
        this.helloAOPImpl=helloAOPImpl;
        /**
         * Enhancer是一个非常重要的类，它允许为非接口类型创建一个java代理，
         * Enhancer动态的创建给定类的子类，并且拦截代理类的所有方法。
         * 和jdk动态代理不一样的是不管是接口还是类他都能正常工作。
         * 而jdk动态代理，只能代理接口
         */
        //Enhancer动态代理对象
        Enhancer enhancer = new Enhancer();

        //给动态代理对象设置指定的代理类
        enhancer.setSuperclass(HelloAOPImpl.class);
        //设置回调
        enhancer.setCallback(this);
        //创建并返回的代理类的实例
        return enhancer.create();
    }

    @Override   //obj 代理对象， method 委托类方法，arg 方法参数，methodproxy代理方法的methodProxy对象
    public Object intercept(Object obj, Method method, Object[] arg, MethodProxy methodProxy) throws Throwable {
        //前置增强
        before();
        //通过代理对象调用父类的方法
        Object result = methodProxy.invokeSuper(obj,arg);
        //后置增强
        after();
        return result;
    }

    public void before(){
        System.out.println("CGLIB动态代理------前置增强");
    }

    public void after(){
        System.out.println("CGLIB动态代理------后置增强");
    }
}
