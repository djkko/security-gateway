package cn.denvie.api.gateway.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API映射注解，用来声明对外暴露的接口。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMapping {

    /**
     * Api Name
     */
    String value();

    /**
     * 是否需要登录鉴权
     */
    boolean needLogin() default true;

}
