package com.example.router;


import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.coco.base.event.EventManager;
import com.coco.base.event.IEventListener;
import com.example.router.event.AppEvent;

/**
 * create by chenqi on 2018/8/12
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        //注册事件
        addEvent();

        //模拟发送事件

        //1多线程写法
        //sendEvent1();
        //2handler写法
        sendEvent2();

    }

    private void sendEvent1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    EventManager.defaultAgent().distribute(AppEvent.TYPE_ON_RECEIVE_MESSAGE,
                            new AppEvent.AppEventParam(200,true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }


    private Handler handler = new Handler(getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            EventManager.defaultAgent().distribute(AppEvent.TYPE_ON_RECEIVE_MESSAGE,
                    new AppEvent.AppEventParam(200,true));
        }
    };

    private void sendEvent2() {
        handler.sendEmptyMessageDelayed(0,3000L);
    }


    private void addEvent() {
        EventManager.defaultAgent().addEventListener(AppEvent.TYPE_ON_RECEIVE_MESSAGE, listener);
    }

    private IEventListener listener = new IEventListener<AppEvent.AppEventParam>() {

        @Override
        public void onEvent(String eventType, AppEvent.AppEventParam params) {
            Toast.makeText(MainActivity.this,"received ? >> " + params.data, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        //销毁事件
        removeEvent();
        super.onDestroy();
    }

    private void removeEvent() {
        EventManager.defaultAgent().removeEventListener(AppEvent.TYPE_ON_RECEIVE_MESSAGE, listener);
    }
}
