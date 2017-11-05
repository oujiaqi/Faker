package com.tencent.faker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.app.IntentService;

import com.tencent.utility.CpuUtils;

/**
 * Created by colinou on 2017/11/5.
 */

public class MainServicer extends Service {
    private double CPURate;
    private boolean quit;  // CPU获取开关
    private MyBinder binder = new MyBinder();

    // 自定义 Binder 类 用于数据返回
    public class MyBinder extends Binder {
        public double getCPURate() {
            return CPURate;
        }
    }

    // Service被创建时回调该方法
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Service is created");

        new Thread() {
            @Override
            public void run() {
                while (!quit) {
                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        System.out.println("Service running error");
                    }
                    CPURate = 100 - CpuUtils.getProcessCpuRate();
                }
            }
        }.start();

    }

    // Service被断开连接时回调该方法
    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("Service is Unbinded");
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service is Started");
        return START_STICKY;
    }

    // Service被关闭之前回调该方法
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.quit = true;
        System.out.println("Service is Destroyed");
    }

    // 必须实现的方法，绑定该Service时回调该方法
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Service is Binded");
        return binder;
    }
}
