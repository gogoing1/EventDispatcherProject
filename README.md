# EventDispatcherProject
一个轻量级的事件分发库

## 引入步骤
### 一 把model 'base'直接复制一份到项目中 ，在使用到的model的 build.gradle文件下添加：
compile project（'base'）

### 二 在application中初始化
    public class CocoApplication extends Application{

        @Override
        public void onCreate() {
            super.onCreate();

            //初始化事件库
            init();
        }

        private void init() {
            EventManager.defaultAgent().init();
        }
    }

### 三 在UI层注册监听，这里模拟的发送事件一般是在业务层处理

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
            //销毁事件（避免内存泄露）
            removeEvent();
            super.onDestroy();
        }

        private void removeEvent() {
            EventManager.defaultAgent().removeEventListener(AppEvent.TYPE_ON_RECEIVE_MESSAGE, listener);
        }
    }

### 四 事件可以携带数据，需要继承 BaseEventParam，并传入数据类型，这里是Boolean

    public static class XXXEventParam extends BaseEventParam<Boolean>{

            public AppEventParam(int code, Boolean data) {
                super(code, data);
            }
    }


    ok～ 这个轻量级的事件库，喜欢的朋友 可以点个Star哦，have a nice day...
