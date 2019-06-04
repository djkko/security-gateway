package cn.denvie.api;

import cn.denvie.api.resolver.UserFormMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class DemoWebMvcConfig extends WebMvcConfigurationSupport {

    @Autowired
    UserFormMethodArgumentResolver userFormMethodArgumentResolver;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userFormMethodArgumentResolver);
    }
}
