package com.mybatis.sqlsession;

import java.lang.reflect.Proxy;

/**
 * 定义SqlSession核心工厂，建立执行器，用于调用与数据库交互的方法
 */
public class MySqlSession {
    //创建一个执行器  这样这个执行器就可以调用方法了
    private Excutor excutor = new MyExcutor();

    //加载配置类
    private MyConfiguration myConfiguration = new MyConfiguration();

    //用执行器开始调用方法
    public <T> T selectOne(String statement,Object parameter){
        return excutor.query(statement,parameter);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<?> clas){
        /**
         * public static Object newProxyInstance
         * (ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) throws IllegalArgumentException
         *
         * lodar ： 一个ClassLoader对象，定义了由哪个ClassLoader对象来生成的代理对象进行加载
         * interface： 一个Interface对象的数组，表示的是我要将给我需要代理的对象提供一组什么接口，如果我提供了一组
         *              接口给它，那么这个代理对象就直接就宣称实现了该接口（动态），这样我就能调用这组接口的方法了
         *  h   : 一个InvocationHandler对象，表示的是当我这个动态代理对象在调用方法的时候，
         *          会关联到哪一个InvocationHandler对象上
         *           //动态代理调用    Proxy这个类的作用就是从来动态创建一个代理对象的类，用的最多的就是这个方法
         *         //这个方法的作用就是得到一个动态的代理对象，其接受三个参数，分别代表的含义：
         *          //在这里这个方法的作用是，将传进来的类生成代理对象进行加载，
         *         // 给这个类提供一组接口让他能够调用这些接口对应实现方法，
         *         //将这个代理对象在调用方法的时候关联到MyMapperProxy对象上
         *         //即 传进来的类，生成他的代理对象，给他相应的接口，让他可以调用接口的实现方法
         */
        return (T) Proxy.newProxyInstance(clas.getClassLoader(),new Class[]{clas},
                new MyMapperProxy(myConfiguration,this));
    }

}
