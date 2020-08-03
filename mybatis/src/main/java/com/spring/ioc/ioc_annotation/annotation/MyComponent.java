package com.spring.ioc.ioc_annotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Target 属性用于注明此注解用在什么位置
 * ElementType.TYPE 表示可用在类，接口，枚举上等
 * @Retention 属性表示所定义的注解何时生效
 * RetentionPolicy.runtime 表示在运行时有效
 *
 * @interface 表示注解类型
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyComponent {
    /**
     * 为此注解定义scope属性
     * @return
     */
    public String scope();
}
