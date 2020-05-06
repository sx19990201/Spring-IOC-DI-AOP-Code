package com.springMVC.annotation;

import java.lang.annotation.*;

/**
 * 可以在类和方法上定义该注解
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {

    /**
     * 表示访问该方法的url
     * @return
     */
    String value() default "";
}
