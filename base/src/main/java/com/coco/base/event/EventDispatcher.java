package com.coco.base.event;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.coco.base.util.DefaultThreadFactory;

import java.util.ArrayList;
import java.util.Iterator;
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

public class EventDispatcher implements IEventDispatcher {

    private static final String TAG = EventDispatcher.class.getSimpleName();

    //最多同时存在一线程，线程空闲超过60秒则回收
    private ExecutorService executorService = null;

    private Handler dispatcherHandler = null;

    //可重入锁
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ArrayMap<String, List<EventListenerHolder>> listenerHolder = new ArrayMap<>();

    private List<EventListenerHolder> willRemoveHolderList = new ArrayList<>();

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
                    60L, TimeUnit.SECONDS,
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
     * 定向分发事件。
     *
     * 对指定dispatcher D委派类型为Type T的事件。
     * D中所有关注事件类型T的listener 都会收到事件调用。
     * 这种分发中，D的子Dispatcher不参与分发。
     *
     * @param eventType 事件类型
     * @param params    事件参数
     * @param <P>
     */
    @Override
    public <P> void distribute(final String eventType, final P params) {
        if(executorService !=null){
            Log.i(TAG, String.format("[ distribute error executorService == null. type = %s params = %s]",
                    eventType, params.toString()));
            return;
        }
        Log.i(TAG, String.format("[ distribute. type = %s]",eventType));

        try {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    if(TextUtils.isEmpty(eventType)){
                        Log.e(TAG, "[ unknow event type. stop distributing.]");
                        return;
                    }
                    lock.readLock().lock();
                    try {
                        List<EventListenerHolder> holders = listenerHolder.get(eventType);
                        if(holders != null){
                            for(EventListenerHolder holder : holders){
                                final IEventListener listener = holder.listener;
                                if(listener != null){
                                    final String key = getDispatchingKey(eventType, listener);
                                    int count = dispatchingKeyMap.contains(key) ? dispatchingKeyMap.get(key)+1 : 1;
                                    dispatchingKeyMap.put(key, count);
                                    dispatcherHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            int count = dispatchingKeyMap.containsKey(key) ? dispatchingKeyMap.get(key) : 0;
                                            if(count > 0){
                                                listener.onEvent(eventType, params);
                                            }else {
                                                Log.w(TAG, getDispatchingKey(eventType, listener) + " is removed from dispatchngKeyMap.");
                                            }
                                        }
                                    });
                                }
                            }
                        }

                    }catch (Exception e){
                        Log.e(TAG,"distribute run Exception ",e);
                    }finally {
                        lock.readLock().unlock();
                    }
                }
            });

        }catch (Exception e){
            Log.e(TAG,"distribute exception", e);
        }
    }


    /**
     * 添加事件
     *
     * @param type     事件类型
     * @param listener 事件监听
     */
    @Override
    public void addEventListener(String type, IEventListener listener) {
        addEventListener(type,0, listener);
    }

    /**
     * 添加事件
     *
     * @param type     事件类型
     * @param priority 事件优先级
     * @param listener 事件监听
     */
    @Override
    public void addEventListener(String type, int priority, IEventListener listener) {
        lock.writeLock().lock();

        try {
            List<EventListenerHolder> holderList = listenerHolder.get(type);
            if(holderList == null){
                holderList = new ArrayList<>();
                listenerHolder.put(type,holderList);
            }
            EventListenerHolder holder = EventListenerHolder.obtain(priority, listener);
            int i = 0;
            for(EventListenerHolder oldHolder : holderList){
                if(oldHolder.priority < holder.priority){
                    break;
                }
                i++;
            }
            holderList.add(i,holder);

        }finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * 移除事件
     *
     * @param type 事件类型
     */
    @Override
    public void removeEventListenerByType(String type) {
        lock.writeLock().lock();

        try {
            List<EventListenerHolder> holderList = listenerHolder.get(type);
            if(holderList != null){
                Iterator<EventListenerHolder> iterator = holderList.iterator();
                while (iterator.hasNext()){
                    EventListenerHolder holder = iterator.next();
                    dispatchingKeyMap.remove(getDispatchingKey(type, holder.listener));
                    iterator.remove();
                    holder.recycle();
                }
            }

        }finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public void removeEventListener(String type, IEventListener listener) {
        lock.writeLock().lock();
        try {
            willRemoveHolderList.clear();
            List<EventListenerHolder> holderList = listenerHolder.get(type);
            if(holderList != null){
                for(EventListenerHolder holder : holderList){
                    if(holder.listener == listener){
                        willRemoveHolderList.add(holder);
                    }
                }

                for(EventListenerHolder holder : willRemoveHolderList){
                    dispatchingKeyMap.remove(getDispatchingKey(type, listener));
                    holderList.remove(holder);
                    holder.recycle();
                }
            }
            willRemoveHolderList.clear();

        }finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public void removeAllEventListeners() {
        lock.writeLock().lock();

        try {
            for(String type: listenerHolder.keySet()){
                List<EventListenerHolder> holderList = listenerHolder.get(type);
                if(holderList != null){
                    Iterator<EventListenerHolder> iterator = holderList.iterator();
                    while (iterator.hasNext()){
                        EventListenerHolder holder = iterator.next();
                        dispatchingKeyMap.remove(getDispatchingKey(type, holder.listener));
                        iterator.remove();
                        holder.recycle();
                    }
                }

            }

        }finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public boolean hasEventListener(String type) {
        lock.writeLock().lock();

        try {
            List<EventListenerHolder> holderList = listenerHolder.get(type);
            return holderList!=null && !holderList.isEmpty();
        }finally {
            lock.writeLock().unlock();
        }
    }

    private String getDispatchingKey(String eventType, IEventListener listener){
        return eventType + "_" +listener.hashCode();
    }

}
