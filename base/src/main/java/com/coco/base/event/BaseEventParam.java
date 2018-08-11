package com.coco.base.event;

/**
 * Created by chenqi on 2018/8/11.
 */

public class BaseEventParam<T> {

    public int code = 0;
    public T data = null;

    public BaseEventParam(){}

    public BaseEventParam(int code, T data){
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseEventParam{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
