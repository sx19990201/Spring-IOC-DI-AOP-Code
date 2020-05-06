package com.springMVC.annotation;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {

    /**
     * 表示给controller其别名
     * @return
     */
    public String value() default "";
}
