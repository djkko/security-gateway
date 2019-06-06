package cn.denvie.api.gateway.core;

import java.lang.annotation.*;

/**
 * API映射注解，用来声明对外暴露的接口。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
/*
SpringBean被cglib动态代理后导致自定义注解丢失问题解决方案：
1.将spring.aop.proxy-target-class=true 去掉， 自动使用JDK代理。
2.使用注解解析器工具org.springframework.core.annotation.AnnotationUtils
 */
@Inherited
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMapping {

    /**
     * Api Name
     */
    String value();

    /**
     * 是否需要验证空参数
     */
    boolean needParams() default false;

    /**
     * 是否需要登录鉴权
     */
    boolean needLogin() default true;

}
