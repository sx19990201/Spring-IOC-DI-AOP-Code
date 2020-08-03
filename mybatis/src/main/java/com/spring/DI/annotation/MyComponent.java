package com.spring.DI.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 进行di注入前需要创建工厂，在运行时从工厂取出对象为属性赋值，因此先做一些准备工作
 * 创建几个要用到的注解
 */
//注明注解用在什么位置
@Target(ElementType.TYPE)
//所定义的注解在何时生效
@Retention(RetentionPolicy.RUNTIME)
public @interface MyComponent {
    /**
     * 为此注解定义scope属性
     * @return
     */
    public String scope() default "";
}
