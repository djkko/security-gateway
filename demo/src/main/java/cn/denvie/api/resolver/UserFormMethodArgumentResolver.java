package cn.denvie.api.resolver;

import cn.denvie.api.gateway.utils.RandomUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserFormMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserFormAnnotation.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        UserForm user = new UserForm();
        String uuid = RandomUtils.generateShortUuid();
        user.setUserFormId(uuid);
        user.setUserFormName("User" + uuid);
        user.setUserFormPassword("Password" + uuid);
        return user;
    }

    public static void main(String[] args) throws Exception {
        // 判断类是否实现了某接口
        System.err.println(HandlerMethodArgumentResolver.class.isAssignableFrom(UserFormMethodArgumentResolver.class));
        System.err.println(UserFormMethodArgumentResolver.class.newInstance() instanceof HandlerMethodArgumentResolver);
    }

}
