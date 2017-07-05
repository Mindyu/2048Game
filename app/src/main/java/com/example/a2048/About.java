package com.example.a2048;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class About extends Activity {

    private TextView mytime;
    private SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private Date begin= null;

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            refreshTime();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //沉浸式状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //去掉标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);   //锁定竖屏
        setContentView(R.layout.activity_about);

        mytime= (TextView) findViewById(R.id.mytime);


        try {
            begin = dfs.parse("2017-03-05 00:00:00:000");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        timer.schedule(task, 0, 1000);
    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.sendMessage(Message.obtain());
        }
    };

    public void refreshTime(){
        long between=(System.currentTimeMillis()-begin.getTime())/1000;//除以1000是为了转换成秒
        long day1=between/(24*3600);
        long hour1=between%(24*3600)/3600;
        long minute1=between%(24*3600)%3600/60;
        long second1=between%(24*3600)%3600%60;
        mytime.setText(""+day1+"天"+hour1+"小时"+minute1+"分"+second1+"秒");
    }
}
