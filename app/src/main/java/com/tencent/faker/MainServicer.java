package com.tencent.faker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.app.IntentService;

import com.tencent.utility.CpuUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by colinou on 2017/11/5.
 */

public class MainServicer extends Service {
    private int currentIdleCPU = 0, setIdleCPU = 0, sleepTime = 300;
    private boolean quit = true;  // CPU获取开关
    private MyBinder binder = new MyBinder();

    // 自定义 Binder 类 用于数据返回
    public class MyBinder extends Binder {
        public void setQuit(boolean flag) { quit = flag; }
    }

    // Service被创建时回调该方法
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Service is created");

//        new Thread() {
//            @Override
//            public void run() {
//                while (quit) {
//                    currentIdleCPU = 100 - CpuUtils.getProcessCpuRate();
//                    System.out.println("当前idelCPU为："+currentIdleCPU);
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
//
//
//
//        System.out.println("setIdleCPU："+setIdleCPU);
//        int theadCount = 30 * (100 - setIdleCPU) / 100;
//        System.out.println("当前线程数为："+theadCount);
//
//        for (int i = 0; i < theadCount; i++) {
//
//            new Thread() {
//                @Override
//                public void run() {
//                    int eachSleepTime = sleepTime;
//                    while (quit) {
//                        if (currentIdleCPU > setIdleCPU) {
//                            eachSleepTime -= 1;
//                        }
//                        if (currentIdleCPU < setIdleCPU) {
//                            eachSleepTime += 1;
//                        }
//                        if (eachSleepTime < 0) {
//                            eachSleepTime = 0;
//                        }
//                        System.out.println("当前sleepTime为："+eachSleepTime);
//                        try {
//                            Thread.sleep(eachSleepTime);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }.start();
//        }
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
        System.out.println("Service is Destroyed");
    }

    // 必须实现的方法，绑定该Service时回调该方法
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Service is Binded");
        setIdleCPU = intent.getIntExtra("set_cpu_value", 0);
        System.out.println("传递的set_cpu_value是："+setIdleCPU);
        currentIdleCPU = 100 - CpuUtils.getProcessCpuRate();
        new Thread() {
            @Override
            public void run() {
                while (quit) {
                    currentIdleCPU = 100 - CpuUtils.getProcessCpuRate();
                    System.out.println("当前idelCPU为："+currentIdleCPU);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();



        System.out.println("setIdleCPU："+setIdleCPU);
        int theadCount = 50 * (100 - setIdleCPU) / 100;
        System.out.println("当前线程数为："+theadCount);

        for (int i = 0; i < theadCount; i++) {
//            long temp = System.currentTimeMillis();
//            while (temp + 177 > System.currentTimeMillis()) {
//
//            }
            new Thread() {
                @Override
                public void run() {
//                    int eachSleepTime = sleepTime;
                    int eachSleepTime;
                    int max=200;
                    int min=10;
                    Random random = new Random();

                    eachSleepTime = random.nextInt(max)%(max-min+1) + min;
                    while (quit) {

                        if (currentIdleCPU > setIdleCPU) {
                            System.out.println("减法当前currentIdleCPU："+currentIdleCPU);
                            System.out.println("减法当前setIdleCPU："+setIdleCPU);
                            eachSleepTime -= 1;
                        }
                        if (currentIdleCPU < setIdleCPU) {
                            System.out.println("加法当前currentIdleCPU："+currentIdleCPU);
                            System.out.println("加法当前setIdleCPU："+setIdleCPU);
                            eachSleepTime += 1;
                        }
                        if (eachSleepTime < 0) {
                            eachSleepTime = 0;
                        }
                        System.out.println("当前sleepTime为："+eachSleepTime);
                        try {
                            Thread.sleep(eachSleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        return binder;
    }
}
