package com.coco.base.util;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenqi on 2018/8/11.
 */

public class DefaultThreadFactory implements ThreadFactory{

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;
    private final int threadPriority;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public DefaultThreadFactory(String threadNamePerfix){
        this(Thread.NORM_PRIORITY, threadNamePerfix);
    }

    public DefaultThreadFactory(int threadPriority, String threadNamePrefix){
        this.threadPriority = threadPriority;
        group = Thread.currentThread().getThreadGroup();
        namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
    }


    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if(t.isDaemon()) t.setDaemon(false);
        t.setPriority(threadPriority);
        return t;
    }

}
