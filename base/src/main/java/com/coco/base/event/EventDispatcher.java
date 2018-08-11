package com.coco.base.event;

import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;

import com.coco.base.util.DefaultThreadFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by chenqi on 2018/8/11.
 */

public class EventDispatcher implements IeventDispatcher {

    private static final String TAG = EventDispatcher.class.getSimpleName();

    //最多同时存在一线程，线程空闲超过60秒则回收
    private ExecutorService executorService = null;

    private Handler dispatcherHandler = null;

    //可重入锁
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    ArrayMap<String, List<EventListenerHolder>> listenerHolder = new ArrayMap<>();

    private ConcurrentHashMap<String, Integer> dispatchingKeyMap = new ConcurrentHashMap<>();


    public EventDispatcher(){
        this(null);
    }


    public EventDispatcher(Handler handler){
        if(handler != null){
            this.dispatcherHandler = handler;
        }else {
            this.dispatcherHandler = new Handler(Looper.getMainLooper());
        }
    }


    @Override
    public void init() {
        if(executorService == null){
            executorService = new ThreadPoolExecutor(
                    0,1,
                    60, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory("event_"));
        }
    }


    @Override
    public void shutdownNow() {
        if(executorService != null){
            executorService.shutdownNow();
            executorService = null;
        }
        dispatchingKeyMap.clear();
    }


    /**
     * 定向分发事件。对指定dispatcher D委派类型为Type T的事件。
     * D中所有关注事件类型T的listener 都会收到事件调用。
     * 这种分发中，D的子Dispatcher不参与分发。
     *
     * @param eventType 事件类型
     * @param params    事件参数
     * @param <P>
     */
    @Override
    public <P> void distribute(String eventType, P params) {

    }

    @Override
    public void addEventListener(String type, int priority, IeventListener listener) {

    }

    @Override
    public void addEventLIstener(String type, IeventListener listener) {

    }

    @Override
    public void removeEventLIstener(String type, IeventListener listener) {

    }

    @Override
    public void removeEventListenerByType(String type) {

    }

    @Override
    public void removeAllEventListeners() {

    }

    @Override
    public boolean hasEventListener() {
        return false;
    }

}
