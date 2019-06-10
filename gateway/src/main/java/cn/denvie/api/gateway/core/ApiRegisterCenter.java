package cn.denvie.api.gateway.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API注册中心。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
@Slf4j
public class ApiRegisterCenter {

    private ApplicationContext applicationContext;
    private ConcurrentHashMap<String, ApiRunnable> apiMap = new ConcurrentHashMap();
    private List<HandlerMethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();

    // spring ioc
    public ApiRegisterCenter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void loadApiFromSpringBeans() {
        // spring ioc 扫描所有Bean
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Class<?> type;
        Object obj;
        for (String beanName : beanNames) {
            type = applicationContext.getType(beanName);
            obj = applicationContext.getBean(beanName);
            // log.debug("【ApiGateway】Find Bean：{}, Type：{}，obj：{}", beanName, type, obj);

            // 获取自定义的 HandlerMethodArgumentResolver
            if (!type.getName().startsWith("org.springframework")
                    && obj != null && obj instanceof HandlerMethodArgumentResolver) {
                log.debug("【ApiGateway】Find HandlerMethodArgumentResolver：{}", type.getName());
                methodArgumentResolvers.add((HandlerMethodArgumentResolver) obj);
            }

            for (Method m : type.getDeclaredMethods()) {
                // 通过反谢拿到APIMapping注解
                // 使用AnnotationUtils解决SpringBean被cglib动态代理后导致自定义注解丢失问题
                // ApiMapping apiMapping = m.getAnnotation(ApiMapping.class);
                ApiMapping apiMapping = AnnotationUtils.findAnnotation(m, ApiMapping.class);
                if (apiMapping != null) {
                    addApiItem(apiMapping, beanName, m, type);
                }
            }
        }
    }

    /**
     * 获取支持的方法参数解析器
     */
    public HandlerMethodArgumentResolver supportsParameter(MethodParameter parameter) {
        for (HandlerMethodArgumentResolver resolver : methodArgumentResolvers) {
            if (resolver.supportsParameter(parameter)) {
                return resolver;
            }
        }
        return null;
    }

    public ApiRunnable findApiRunnable(String apiName) {
        return apiMap.get(apiName);
    }

    private void addApiItem(ApiMapping apiMapping, String beanName, Method method, Class<?> type) {
        ApiRunnable apiRunnable = new ApiRunnable();
        apiRunnable.apiName = apiMapping.value();
        apiRunnable.targetName = beanName;
        apiRunnable.targetMethod = method;
        apiRunnable.targetMethodName = type.getName() + "." + method.getName();
        apiRunnable.apiMapping = apiMapping;
        // MethodParameter
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            apiRunnable.getMethodParameters().add(new MethodParameter(method, i));
        }
        // Add to ApiRunnable Map
        log.debug("【ApiGateway】Find Api：{}", apiRunnable.toString());
        apiMap.put(apiRunnable.apiName, apiRunnable);
    }

    public ApiRunnable findApiRunnable(String apiName, String version) {
        return (ApiRunnable) apiMap.get(apiName + "_" + version);
    }

    public List<ApiRunnable> findApiRunnables(String apiName) {
        if (apiName == null) {
            throw new IllegalArgumentException("apiMapping name can not be null");
        }
        List<ApiRunnable> list = new ArrayList();
        for (ApiRunnable api : apiMap.values()) {
            if (api.apiName.equals(apiName)) {
                list.add(api);
            }
        }
        return list;
    }

    public List<ApiRunnable> getAll() {
        List<ApiRunnable> list = new ArrayList<ApiRunnable>();
        list.addAll(apiMap.values());
        Collections.sort(list, new Comparator<ApiRunnable>() {
            public int compare(ApiRunnable o1, ApiRunnable o2) {
                return o1.getApiName().compareTo(o2.getApiName());
            }
        });
        return list;
    }

    public boolean containsApi(String apiName, String version) {
        return apiMap.containsKey(apiName + "_" + version);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 用于执行对应的API方法。
     */
    public class ApiRunnable {
        private String apiName;             // ApiMapping Name
        private String targetName;          // Spring Ioc Bean name
        private Object target;              // Bean实例
        private Method targetMethod;        // 执行的方法
        private String targetMethodName;    // 执行的方法全路径
        private ApiMapping apiMapping;

        // MethodParameter
        private List<MethodParameter> methodParameters = new ArrayList<>();

        public Object run(Object... args) throws Exception {
            return targetMethod.invoke(getTarget(), args);
        }

        public Class<?>[] getParamTypes() {
            return targetMethod.getParameterTypes();
        }

        public String getApiName() {
            return apiName;
        }

        public String getTargetName() {
            return targetName;
        }

        public Object getTarget() {
            if (target == null) {
                // applicationContext.getBean() 是线程安全的
                target = applicationContext.getBean(targetName);
            }
            return target;
        }

        public Method getTargetMethod() {
            return targetMethod;
        }

        public String getTargetMethodName() {
            return targetMethodName;
        }

        public ApiMapping getApiMapping() {
            return apiMapping;
        }

        public List<MethodParameter> getMethodParameters() {
            return methodParameters;
        }

        public void setMethodParameters(List<MethodParameter> methodParameters) {
            this.methodParameters = methodParameters;
        }

        @Override
        public String toString() {
            return "ApiRunnable{" +
                    "apiName='" + apiName + '\'' +
                    ", targetName='" + targetName + '\'' +
                    ", targetMethodName='" + targetMethodName + '\'' +
                    '}';
        }
    }

}
