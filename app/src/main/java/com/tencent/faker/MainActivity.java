package com.tencent.faker;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;

import com.tencent.utility.CpuUtils;

import org.w3c.dom.Text;

public class MainActivity extends Activity {
    private Button start_faker, quit_faker;
    private SeekBar target_seekbar;
    private ProgressBar current_progressbar, now_progressbar, exp_progressbar;
    private TextView current_value, target_value, now_value, exp_value;
    MainServicer.MyBinder myBinder;

    private Handler handler;
    private int current_cpu_value = 0, now_cpu_value, exp_cpu_value, set_cpu_value = 0;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("--Service Connected--");
            // 获取Service的onBind方法所返回的MyBinder对象
            myBinder = (MainServicer.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("--Service Disconnected--");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 连接各个组件
        start_faker = (Button) findViewById(R.id.start_faker);
        quit_faker = (Button) findViewById(R.id.quit_faker);
        current_progressbar= (ProgressBar) findViewById(R.id.current_progressbar);
        now_progressbar = (ProgressBar) findViewById(R.id.now_progressbar);
        exp_progressbar = (ProgressBar) findViewById(R.id.exp_progressbar);
        target_seekbar = (SeekBar) findViewById(R.id.target_seekbar);
        current_value = (TextView) findViewById(R.id.current_value);
        now_value = (TextView) findViewById(R.id.now_value);
        exp_value = (TextView) findViewById(R.id.exp_value);
        target_value = (TextView) findViewById(R.id.target_value);

        // 设置更新
        handler = new Handler();
        new Thread() {
            public void run() {
                while(true) {
                    current_cpu_value = CpuUtils.getProcessCpuRate();
                    now_cpu_value = CpuUtils.getAppProcessCpuRate("now");
                    exp_cpu_value = CpuUtils.getIdleCpuRateExcept("now");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            current_value.setText("" + (100 - current_cpu_value) + "%");
                            current_progressbar.setProgress(100-current_cpu_value);
                            now_value.setText("" + now_cpu_value + "%");
                            now_progressbar.setProgress(now_cpu_value);
                            exp_value.setText("" + exp_cpu_value + "%");
                            exp_progressbar.setProgress(exp_cpu_value);
                        }
                    });
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

        // 设置理想CPU参数更新
        target_seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            target_value.setText(""+target_seekbar.getProgress()+"%");
                            set_cpu_value = target_seekbar.getProgress();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // 创建启动Service的Intent
        final Intent intent = new Intent(this, MainServicer.class);


        // 绑定 start_faker 事件
        start_faker.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.putExtra("set_cpu_value", set_cpu_value);
                        bindService(intent, conn, Service.BIND_AUTO_CREATE);
                    }
                }
        );


        // 绑定 quit_faker 事件
        quit_faker.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myBinder.setQuit(false);
                        unbindService(conn);
                    }
                }
        );

    }

}
