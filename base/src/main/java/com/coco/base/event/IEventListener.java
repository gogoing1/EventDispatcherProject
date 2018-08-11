package com.coco.base.event;

/**
 * Created by chenqi on 2018/8/11.
 */

public interface IEventListener<P> {

    /**
     * @param eventType 事件类型
     * @param params
     */
    void onEvent(String eventType, P params);
}
