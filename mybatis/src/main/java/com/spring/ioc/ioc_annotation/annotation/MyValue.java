package com.spring.ioc.ioc_annotation.annotation;

        import java.lang.annotation.ElementType;
        import java.lang.annotation.Retention;
        import java.lang.annotation.RetentionPolicy;
        import java.lang.annotation.Target;

/**
 * @rarget 属性表示该注解用在什么位置
 * ElementType.FIELD 表示可作用于枚举 常量上
 * @Retention 表示所定义的注解在何时生效
 * RetentionPolicy.RUNTIME 表示在运行时间有效
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyValue {
    /**
     * 定义value属性
     * @return
     */
    public String value();
}
