package com.example.a2048;

/**
 * Created by 杨 陈强 on 2017/7/5.
 */
public class Score {
    private int score;
    private String name;
    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Score(String name,int score,String time){
        this.setName(name);
        this.setScore(score);
        this.setTime(time);
    }

    public Score(){
    }

}
