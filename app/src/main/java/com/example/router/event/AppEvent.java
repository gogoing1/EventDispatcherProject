package com.example.router.event;

import com.coco.base.event.BaseEventParam;

/**
 * Created by chenqi on 2018/8/12.
 */

public class AppEvent {

    public static final String TYPE_ON_RECEIVE_MESSAGE = "com.example.router.event.TYPE_ON_RECEIVE_MESSAGE";


    public static class AppEventParam extends BaseEventParam<Boolean>{
        public AppEventParam(int code, Boolean data) {
            super(code, data);
        }
    }
}
