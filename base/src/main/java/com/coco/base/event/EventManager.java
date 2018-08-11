package com.coco.base.event;

/**
 * Created by chenqi on 2018/8/11.
 */

public class EventManager implements IEventDispatcher{

    private static EventManager _Instance = null;
    private static EventDispatcher defaultDispatcher = null;

    public synchronized static EventManager defaultAgent(){
        if(_Instance == null){
            _Instance = new EventManager();
        }
        return _Instance;
    }

    private EventManager(){
        defaultDispatcher = new EventDispatcher();
    }

    @Override
    public void init() {
        if(defaultDispatcher != null){
            defaultDispatcher.init();
        }
    }

    @Override
    public void shutdownNow() {
        if(defaultDispatcher!=null){
            defaultDispatcher.shutdownNow();
        }
    }

    @Override
    public <P> void distribute(String eventType, P params) {
        defaultDispatcher.distribute(eventType, params);
    }

    @Override
    public void addEventListener(String type, IEventListener listener) {
        defaultDispatcher.addEventListener(type, listener);
    }

    @Override
    public void addEventListener(String type, int priority, IEventListener listener) {
        defaultDispatcher.addEventListener(type, 0, listener);
    }

    @Override
    public void removeEventListenerByType(String type) {
        defaultDispatcher.removeEventListenerByType(type);
    }

    @Override
    public void removeEventListener(String type, IEventListener listener) {
        defaultDispatcher.removeEventListener(type, listener);
    }

    @Override
    public void removeAllEventListeners() {
        defaultDispatcher.removeAllEventListeners();
    }

    @Override
    public boolean hasEventListener(String type) {
        return defaultDispatcher.hasEventListener(type);
    }

}
