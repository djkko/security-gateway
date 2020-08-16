package cn.denvie.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 参考：https://www.jianshu.com/p/d62c2be60617
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    private final Class raw;
    private final Type[] args;

    public ParameterizedTypeImpl(Class raw, Type[] args) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
    }

    // 以Map<String, User>为例, 这里返回[String.class,User.class]
    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    // Map<String, User>里的Map, 这里返回值是Map.class
    @Override
    public Type getRawType() {
        return raw;
    }

    // 用于这个泛型上中包含了内部类的情况,一般返回null
    @Override
    public Type getOwnerType() {
        return null;
    }

}
