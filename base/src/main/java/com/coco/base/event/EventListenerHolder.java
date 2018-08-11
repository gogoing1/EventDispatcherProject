package com.coco.base.event;

import android.support.v4.util.Pools;

/**
 * Created by chenqi on 2018/8/11.
 */

public class EventListenerHolder {

    public int priority = 0;
    public IEventListener listener;

    private static final Pools.Pool<EventListenerHolder> holderPool =
            new Pools.SynchronizedPool<EventListenerHolder>(50);


    public static EventListenerHolder obtain(){
        EventListenerHolder holder = holderPool.acquire();
        if(holder!=null){
            return holder;
        }
        return new EventListenerHolder();
    }


    public static EventListenerHolder obtain(IEventListener listener){
        EventListenerHolder holder = EventListenerHolder.obtain();
        holder.listener = listener;
        return holder;
    }


    public static EventListenerHolder obtain(int priority, IEventListener listener){
        EventListenerHolder holder = EventListenerHolder.obtain();
        holder.priority = priority;
        holder.listener = listener;
        return holder;
    }


    /**
     * 释放资源
     */
    public void recycle(){
        this.priority = 0;
        this.listener = null;
        holderPool.release(this);
    }



}
