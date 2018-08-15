package com.example.router;

import android.app.Application;

import com.coco.base.event.EventManager;

/**
 * Created by chenqi on 2018/8/13.
 */

public class CocoApplication extends Application{


    @Override
    public void onCreate() {
        super.onCreate();

        //初始化事件
        init();
    }

    private void init() {
        EventManager.defaultAgent().init();
    }
}
