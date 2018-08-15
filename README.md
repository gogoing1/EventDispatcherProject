# EventDispatcherProject
一个轻量级的事件分发库

## 使用

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        //注册事件
        addEvent();

        //模拟发送事件
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
