package com.example.a2048;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    //    private ImageView menu;
    private LinearLayout layout;
    private Button mainMenu;
    private Button btnReset;
    private Button btnUp;
    private TextView textScore;
    private TextView textBestScore;
    private int[][] oldFlag = new int[4][4];
    private static int score = 0;
    private static int best_score=0;

    //触摸事件手指按下和松开的两个坐标
    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;

    //16个方块的id
    static int[][] btnBlock = {
            {R.id.btn00, R.id.btn01, R.id.btn02, R.id.btn03},
            {R.id.btn10, R.id.btn11, R.id.btn12, R.id.btn13},
            {R.id.btn20, R.id.btn21, R.id.btn22, R.id.btn23},
            {R.id.btn30, R.id.btn31, R.id.btn32, R.id.btn33}
    };

    //16个方块的对应的值
    static int[][] flag = {
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //沉浸式状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //去掉标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);   //锁定竖屏
        setContentView(R.layout.activity_main);
        initView();
        startGame();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initView() {
        layout = (LinearLayout) findViewById(R.id.mianLayout);
        layout.setLongClickable(true);
        layout.setOnTouchListener(this);

        btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);
        btnUp = (Button) findViewById(R.id.btnUp);
        btnUp.setOnClickListener(this);
        mainMenu = (Button) findViewById(R.id.mainmenu);
        mainMenu.setOnClickListener(this);
        textScore = (TextView) findViewById(R.id.current_score);
        textBestScore = (TextView) findViewById(R.id.best_score);
        score = 0;         //初始化分数
        try {
            best_score = getBestScore().getScore();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        menu=(ImageView)findViewById(R.id.menu);
//        menu.setOnClickListener(this);
//        ImageView logo=(ImageView)findViewById(R.id.imglogo);
//        logo.setOnClickListener(this);
        //初始化每一个按键的值和颜色
        TextView view;
        for (int i = 0; i < 16; i++) {
            flag[i / 4][i % 4] = 0;
            view = (TextView) findViewById(btnBlock[i / 4][i % 4]);
            view.setText("");
            view.setBackground(getDrawable(R.drawable.border));
        }
        textScore.setText("\n"+score);
        textBestScore.setText("\n"+best_score);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goOn:
                Toast.makeText(this, "继续游戏", Toast.LENGTH_SHORT);
                break;
            case R.id.newGame:
                Toast.makeText(this, "重新开始", Toast.LENGTH_SHORT);
                reset();
                break;
            case R.id.bestScore:
                Toast.makeText(this, "最高分", Toast.LENGTH_SHORT);
                Score item1=null;
                try {
                    item1=getBestScore();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String string = String.valueOf(best_score);
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                if (item1!=null){
                    try {
                        sdf.parse(item1.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    string = "Name："+item1.getName()+"\n\n Score："+item1.getScore()+"\n\n  Time："+item1.getTime();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("最高分")
                        .setMessage(string);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create()
                        .show();
                break;
            case R.id.saveGame:
                Toast.makeText(this, "已保存游戏", Toast.LENGTH_SHORT);
                break;
            case R.id.about:
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, About.class);
                startActivity(intent);
                break;
            case R.id.quit:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        /*if(v.getId()==R.id.menu){
//            Toast.makeText(this,"按钮menu被按下",Toast.LENGTH_SHORT).show();
            openOptionsMenu();
        } else if(v.getId()==R.id.imglogo){
            Toast.makeText(this,"Logo被按下",Toast.LENGTH_SHORT).show();
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu, popup.getMenu());
            popup.show();*/
        if (v.getId() == R.id.mainmenu) {
            openOptionsMenu();
        } else if (v.getId() == R.id.btnReset) {
            reset();
        } else if (v.getId() == R.id.btnUp) {
            backToLast();
        }
    }

    //手势滑动
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (x2 - x1 > 50 && Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
//                    Toast.makeText(this,"向右滑动",Toast.LENGTH_SHORT).show();
                    moveToRight();
                } else if (x1 - x2 > 50 && Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
//                    Toast.makeText(this,"向左滑动",Toast.LENGTH_SHORT).show();
                    moveToLeft();
                } else if (y2 - y1 > 50 && Math.abs(x2 - x1) < Math.abs(y2 - y1)) {
//                    Toast.makeText(this,"向下滑动",Toast.LENGTH_SHORT).show();
                    moveToBottom();
                } else if (y1 - y2 > 50 && Math.abs(x2 - x1) < Math.abs(y2 - y1)) {
//                    Toast.makeText(this,"向上滑动",Toast.LENGTH_SHORT).show();
                    moveToUp();
                }
        }
        return true;
    }

    //随机数获得下一方块位置
    public void nextBlock() {
        if (!isGameOver()) {
            int next = getRandom();
            while (flag[next / 4][next % 4] != 0) {
                next = getRandom();
            }
            TextView btn = (TextView) findViewById(btnBlock[next / 4][next % 4]);
            btn.setTextSize(28);
            btn.setTextColor(Color.RED);
            btn.setGravity(Gravity.CENTER);
            if (getRandom() < 10) {    //随机数0-9出现方块2    二四出现几率为5:3
                btn.setText("2");
//            btn.setBackgroundColor(0xffeee4da);
                btn.setBackground(getDrawable(R.drawable.border2));
                flag[next / 4][next % 4] = 2;
            } else {                 //10-15则出现方块4
                btn.setText("4");
//            btn.setBackgroundColor(0xffeee4da);
                btn.setBackground(getDrawable(R.drawable.border4));
                flag[next / 4][next % 4] = 4;
            }
        } else {
            Toast.makeText(MainActivity.this, "GAME OVER", Toast.LENGTH_SHORT);
            reset();
        }

    }

    //获得随机数
    public int getRandom() {
        Random r = new Random();
        return r.nextInt(16);
    }

    //开始游戏
    public void startGame() {
        nextBlock();
    }

    //判断游戏是否结束
    public boolean isGameOver() {
        boolean flagOver = true;
        for (int i = 0; i < 16; i++) {
            if (flag[i / 4][i % 4] == 0) {
                flagOver = false;
                break;
            }
        }
        //游戏结束时才更新最高分xml文件
        if (flagOver){
            //更新分数
            if (score>=best_score){
                Score newBestScore=new Score();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time =df.format(new Date());
                newBestScore.setTime(time);
                newBestScore.setScore(score);
                newBestScore.setName("爸爸");
                saveBest_score(newBestScore);
            }
        }
        return flagOver;
    }

    //重新开始
    public void reset() {
        initView();
        startGame();
    }

    //向右滑动
    public void moveToRight() {
        for (int i = 0; i < 16; i++) {
            oldFlag[i / 4][i % 4] = flag[i / 4][i % 4];
        }
        for (int j = 0; j < 4; j++) {
            ArrayList<Integer> list1 = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (flag[j][3 - i] != 0) {
                    list1.add(flag[j][3 - i]);
                }
            }
            ArrayList<Integer> temp = new ArrayList<>();
            int i = 0;
            for (i = 0; i < list1.size() - 1; i++) {
                if (list1.get(i) == 2048){
                }else if (list1.get(i) == list1.get(i + 1)) {
                    temp.add(list1.get(i) * 2);
                    score += list1.get(i) * 2;
                    i++;
                } else {
                    temp.add(list1.get(i));
                }
            }
            if (i == list1.size() - 1) {
                temp.add(list1.get(i));
            }
            if (list1.size() > 1) {
                list1 = temp;
            }
            for (i = 0; i < list1.size(); i++) {
                flag[j][3 - i] = list1.get(i);
                fillBlock(list1.get(i), j, 3 - i);
            }
            for (i = list1.size(); i < 4; i++) {
                flag[j][3 - i] = 0;
                fillBlock(0, j, 3 - i);
            }
        }
        //判断是否变化
        boolean isNext = false;
        for (int i = 0; i < 16; i++) {
            if (oldFlag[i / 4][i % 4] != flag[i / 4][i % 4]) {
                isNext = true;
                break;
            }
        }
        if (isNext) {
            nextBlock();
        } else if (isGameOver()) {
            Toast.makeText(MainActivity.this, "GAME OVER", Toast.LENGTH_SHORT);
            reset();   //重新开始
        }
    }

    //向左滑动
    public void moveToLeft() {
        for (int i = 0; i < 16; i++) {      //保存原有模样
            oldFlag[i / 4][i % 4] = flag[i / 4][i % 4];
        }
        for (int j = 0; j < 4; j++) {
            ArrayList<Integer> list1 = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (flag[j][i] != 0) {
                    list1.add(flag[j][i]);
                }
            }
            ArrayList<Integer> temp = new ArrayList<>();
            int i = 0;
            for (i = 0; i < list1.size() - 1; i++) {
                if (list1.get(i) == 2048){
                }else if (list1.get(i) == list1.get(i + 1)) {
                    temp.add(list1.get(i) * 2);
                    score += list1.get(i) * 2;
                    i++;
                } else {
                    temp.add(list1.get(i));
                }
            }
            if (i == list1.size() - 1) {
                temp.add(list1.get(i));
            }
            list1 = temp;
            for (i = 0; i < list1.size(); i++) {
                flag[j][i] = list1.get(i);
                fillBlock(list1.get(i), j, i);
            }
            for (i = list1.size(); i < 4; i++) {
                flag[j][i] = 0;
                fillBlock(0, j, i);
            }
        }
        boolean isNext = false;
        for (int i = 0; i < 16; i++) {
            if (oldFlag[i / 4][i % 4] != flag[i / 4][i % 4]) {
                isNext = true;
                break;
            }
        }
        if (isNext) {
            nextBlock();
        } else if (isGameOver()) {
            Toast.makeText(MainActivity.this, "GAME OVER", Toast.LENGTH_SHORT);
            reset();
        }
    }

    //向上滑动
    public void moveToUp() {
        for (int i = 0; i < 16; i++) {
            oldFlag[i / 4][i % 4] = flag[i / 4][i % 4];
        }
        for (int j = 0; j < 4; j++) {
            ArrayList<Integer> list1 = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (flag[i][j] != 0) {
                    list1.add(flag[i][j]);
                }
            }
            ArrayList<Integer> temp = new ArrayList<>();
            int i = 0;
            for (i = 0; i < list1.size() - 1; i++) {
                if (list1.get(i) ==2048){
                }else if (list1.get(i) == list1.get(i + 1)) {
                    temp.add(list1.get(i) * 2);
                    score += list1.get(i) * 2;
                    i++;
                } else {
                    temp.add(list1.get(i));
                }
            }
            if (i == list1.size() - 1) {
                temp.add(list1.get(i));
            }
            if (list1.size() > 1) {
                list1 = temp;
            }
            for (i = 0; i < list1.size(); i++) {
                flag[i][j] = list1.get(i);
                fillBlock(list1.get(i), i, j);
            }
            for (i = list1.size(); i < 4; i++) {
                flag[i][j] = 0;
                fillBlock(0, i, j);
            }
        }
        boolean isNext = false;     //判断滑动后是否有变化
        for (int i = 0; i < 16; i++) {
            if (oldFlag[i / 4][i % 4] != flag[i / 4][i % 4]) {
                isNext = true;
                break;
            }
        }
        if (isNext) {
            nextBlock();
        } else if (isGameOver()) {
            Toast.makeText(MainActivity.this, "GAME OVER", Toast.LENGTH_SHORT);
            reset();
        }
    }

    //向下滑动
    public void moveToBottom() {
        for (int i = 0; i < 16; i++) {
            oldFlag[i / 4][i % 4] = flag[i / 4][i % 4];
        }
        for (int j = 0; j < 4; j++) {
            ArrayList<Integer> list1 = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (flag[3 - i][j] != 0) {
                    list1.add(flag[3 - i][j]);
                }
            }
            ArrayList<Integer> temp = new ArrayList<>();
            int i = 0;
            for (i = 0; i < list1.size() - 1; i++) {
                if (list1.get(i) == 2048){
                }else if (list1.get(i) == list1.get(i + 1)) {
                    temp.add(list1.get(i) * 2);
                    score += list1.get(i) * 2;
                    i++;
                } else {
                    temp.add(list1.get(i));
                }
            }
            if (i == list1.size() - 1) {
                temp.add(list1.get(i));
            }
            list1 = temp;
            for (i = 0; i < list1.size(); i++) {
                flag[3 - i][j] = list1.get(i);
                fillBlock(list1.get(i), 3 - i, j);
            }
            for (i = list1.size(); i < 4; i++) {
                flag[3 - i][j] = 0;
                fillBlock(0, 3 - i, j);
            }
        }
        boolean isNext = false;
        for (int i = 0; i < 16; i++) {
            if (oldFlag[i / 4][i % 4] != flag[i / 4][i % 4]) {
                isNext = true;
                break;
            }
        }
        if (isNext) {
            nextBlock();
        } else if (isGameOver()) {
            Toast.makeText(MainActivity.this, "GAME OVER", Toast.LENGTH_SHORT);
            reset();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void fillBlock(int flag, int x, int y) {
        refreshScore();
        TextView btn;
        switch (flag) {
            case 0:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
//                btn.setBackgroundColor(0x000000);
                btn.setBackground(getDrawable(R.drawable.border));
                btn.setText("");
                break;
            case 2:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
//                btn.setBackgroundColor(0xffeee4da);
                btn.setBackground(getDrawable(R.drawable.border2));
                btn.setText(String.valueOf(flag));
                break;
            case 4:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
//                btn.setBackgroundColor(0xffede0c8);
                btn.setBackground(getDrawable(R.drawable.border4));
                btn.setText(String.valueOf(flag));
                break;
            case 8:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
//                btn.setBackgroundColor(0xfff2b179);
                btn.setBackground(getDrawable(R.drawable.border8));
                btn.setText(String.valueOf(flag));
                break;
            case 16:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
//                btn.setBackgroundColor(0xfff59563);
                btn.setBackground(getDrawable(R.drawable.border16));
                btn.setText(String.valueOf(flag));
                break;
            case 32:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
//                btn.setBackgroundColor(0xfff67c5f);
                btn.setBackground(getDrawable(R.drawable.border32));
                btn.setText(String.valueOf(flag));
                break;
            case 64:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
//                btn.setBackgroundColor(0xfff65e3b);
                btn.setBackground(getDrawable(R.drawable.border64));
                btn.setText(String.valueOf(flag));
                break;
            case 128:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
                btn.setBackground(getDrawable(R.drawable.border128));
//                btn.setBackgroundColor(0xffedcf72);
                btn.setText(String.valueOf(flag));
                break;
            case 256:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
                btn.setBackground(getDrawable(R.drawable.border256));
//                btn.setBackgroundColor(0xffedcc61);
                btn.setText(String.valueOf(flag));
                break;
            case 512:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
                btn.setBackground(getDrawable(R.drawable.border512));
//                btn.setBackgroundColor(0xffedc850);
                btn.setText(String.valueOf(flag));
                break;
            case 1024:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
                btn.setBackground(getDrawable(R.drawable.border1024));
//                btn.setBackgroundColor(0xffedc53f);
                btn.setText(String.valueOf(flag));
                break;
            case 2048:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
                btn.setBackground(getDrawable(R.drawable.border2048));
//                btn.setBackgroundColor(0xffedc22e);
                btn.setText(String.valueOf(flag));
                break;
            default:
                btn = (TextView) findViewById(btnBlock[x][y]);
                btn.setTextSize(28);
                btn.setTextColor(Color.RED);
                btn.setGravity(Gravity.CENTER);
                btn.setBackgroundColor(0xff3c3a32);
                break;
        }
    }

    //刷新分数
    public void refreshScore(){
        textScore.setText("\n"+score);
        if (score>best_score){
            best_score=score;
            textBestScore.setText("\n"+best_score);
        }
    }

    //后退一步
    public void backToLast() {
        int temp=0;      //判断是否只有一个方块，仅有一个方块  就不能后退一步了
        for (int i = 0; i < 16; i++) {
            if(flag[i / 4][i % 4]!=0){
                ++temp;
            }
        }
        if (temp>1){
            for (int i = 0; i < 16; i++) {
                fillBlock(oldFlag[i / 4][i % 4], i / 4, i % 4);
                flag[i / 4][i % 4] = oldFlag[i / 4][i % 4];
            }
        }
    }

    //保存数据
    public void saveBest_score(Score score){
        SharedPreferences.Editor editor =getApplicationContext().getSharedPreferences("score_data", Context.MODE_PRIVATE).edit();
        editor.putString("name",score.getName());
        editor.putInt("score",score.getScore());
        editor.putString("time",score.getTime());
        editor.commit();
    }

    //取出最高分
    public Score getBestScore() throws ParseException {
        Score rs=new Score();
        SharedPreferences preferences=getSharedPreferences("score_data",MODE_PRIVATE);
        rs.setName(preferences.getString("name","无名氏"));
        rs.setScore(preferences.getInt("score",0));
        rs.setTime(preferences.getString("time",""));
        return rs;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (score>=best_score){
                Score newBestScore=new Score();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time =df.format(new Date());
                newBestScore.setTime(time);
                newBestScore.setScore(score);
                newBestScore.setName("无名氏");
                saveBest_score(newBestScore);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
