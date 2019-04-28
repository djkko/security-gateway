package cn.denvie.api.gateway.core;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.*;

/**
 * API注册中心。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
public class ApiRegisterCenter {

    private ApplicationContext applicationContext;
    private HashMap<String, ApiRunnable> apiMap = new HashMap();

    // spring ioc
    public ApiRegisterCenter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void loadApiFromSpringBeans() {
        // spring ioc 扫描所有Bean
        if (!checkValid()) {
            return;
        }
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Class<?> type;
        for (String name : beanNames) {
            type = applicationContext.getType(name);
            for (Method m : type.getDeclaredMethods()) {
                // 通过反谢拿到APIMapping注解
                ApiMapping apiMapping = m.getAnnotation(ApiMapping.class);
                if (apiMapping != null) {
                    addApiItem(apiMapping, name, m, type);
                }
            }
        }
    }

    public ApiRunnable findApiRunnable(String apiName) {
        return apiMap.get(apiName);
    }

    private void addApiItem(ApiMapping apiMapping, String beanName, Method method, Class<?> type) {
        if (!checkApiMapping()) return;
        ApiRunnable apiRunnable = new ApiRunnable();
        apiRunnable.apiName = apiMapping.value();
        apiRunnable.targetName = beanName;
        apiRunnable.targetMethod = method;
        apiRunnable.targetMethodName = type.getName() + "." + method.getName();
        apiRunnable.apiMapping = apiMapping;
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

    private static final String PCK = "cn.denvie.api";

    private boolean checkValid() {
        if (!ApiRegisterCenter.class.getName().startsWith(PCK)
                || !ApiGatewayHandler.class.getName().startsWith(PCK)) {
            return false;
        }
        return true;
    }

    private boolean checkApiMapping() {
        if (!ApiMapping.class.getName().startsWith(PCK)) {
            return false;
        }
        return true;
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

        public Object run(Object... args) throws Exception {
            if (target == null) {
                // applicationContext.getBean() 是线程安全的
                target = applicationContext.getBean(targetName);
            }
            return targetMethod.invoke(target, args);
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
    }

}
