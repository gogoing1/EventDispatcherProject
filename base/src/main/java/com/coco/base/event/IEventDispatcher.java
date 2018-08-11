package com.coco.base.event;

/**
 * Created by chenqi on 2018/8/11.
 */

public interface IEventDispatcher extends IEvent{

    /**
     * 定向分发事件。对指定dispatcher D委派类型为Type T的事件。
     * D中所有关注事件类型T的listener 都会收到事件调用。
     * 这种分发中，D的子Dispatcher不参与分发。
     */
    <P> void distribute(String eventType, P params);

    void addEventListener(String type, IEventListener listener);

    void addEventListener(String type, int priority, IEventListener listener);

    void removeEventListenerByType(String type);

    void removeEventListener(String type, IEventListener listener);

    void removeAllEventListeners();

    boolean hasEventListener(String type);
}
