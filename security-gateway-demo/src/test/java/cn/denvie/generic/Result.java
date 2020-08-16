package cn.denvie.generic;

public class Result<T> {

    // 返回的结果
    protected T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
