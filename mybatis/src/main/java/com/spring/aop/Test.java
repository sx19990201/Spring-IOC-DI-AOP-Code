package com.spring.aop;

import com.spring.aop.example.HelloAOP;
import com.spring.aop.example.HelloAOPImpl;
import com.spring.aop.example.dynamic.CGLIBDynamicProxyBuild;
import com.spring.aop.example.dynamic.CGLibDynamicProxy;
import com.spring.aop.example.dynamic.JDKDynamicProxy;
import com.spring.aop.example.dynamic.JDKDynamicProxyBuild;

public class Test {

    @SuppressWarnings(value = "unchecked")
    public static void main(String[] args) {

        //创建helloAop对象，由于JDK只能代理接口，所有需要用实现类实现接口的方式创建对象
        HelloAOP helloAOP = new HelloAOPImpl();
        //生成代理类，代理的目标对象为HelloAop，生成它的代理类
        JDKDynamicProxy proxy = new JDKDynamicProxy(helloAOP);
        //代理类调用目标代理对象的方法，并传入参数
        proxy.getProxy().show("张三");
        System.out.println();

        //创建委托类实例
        HelloAOPImpl helloAOPImpl = new HelloAOPImpl();
        //创建CGLib动态代理实例
        CGLibDynamicProxy proxy1 = new CGLibDynamicProxy();
        //动态创建helloaopimpl动态代理的代理类，
        // CGLIB创建代理类的原理是创建一个以委托类作为目标对象的子类，
        // 并重写父类的所有非fanil方法，子类会拦截父类的非final所有方法，顺便织入横切逻辑
        //这里创建cglibProxy代理对象作为 helloAOPImpl类的子类，并拦截父类的所有方法
        Object cglibProxy = proxy1.getInstance(helloAOPImpl);
        //代理类向上转型为父类
        HelloAOPImpl realProxy = (HelloAOPImpl) cglibProxy;
        //子类重写父类的方法再向上转型为父类，所以父类调用的就是子类的方法，而这个方法已经被增强了
        realProxy.show("李四");

        System.out.println();

        //创建代理对象
        HelloAOP helloAOP1 = new HelloAOPImpl();
        //创建代理实例
        JDKDynamicProxyBuild rebuild = new JDKDynamicProxyBuild(helloAOP1);
        //JDK动态生成代理目标对象的委托类，
        HelloAOP proxy2 = rebuild.getProxy(HelloAOP.class);
        proxy2.show("王五");

        System.out.println();

        //创建委托类实例
        CGLIBDynamicProxyBuild rebuild2 = CGLIBDynamicProxyBuild.getInstance();
        //生成代理类的委托类
        HelloAOPImpl helloAOPImpl2 =  rebuild2.getProxy(HelloAOPImpl.class);
        //调用方法
        helloAOPImpl2.show("赵六");

    }


}
