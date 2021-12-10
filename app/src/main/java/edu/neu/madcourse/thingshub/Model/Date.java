package edu.neu.madcourse.thingshub.Model;

public class Date implements Comparable<Date>{
    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Date(String key){
        String[] ymd = key.split("-");
        this.year = Integer.valueOf(ymd[0]);
        this.month = Integer.valueOf(ymd[1]);
        this.day = Integer.valueOf(ymd[2]);
    }

    public Date(){}

    public String toKey(){
        return String.format("%04d",this.year)+"-"+String.format("%02d", this.month)+"-"+String.format("%02d", this.day);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return toKey();
    }

    @Override
    public int compareTo(Date date) {
        if(this.year > date.year) return 1;
        else if(this.year < date.year) return -1;
        if(this.month > date.month) return 1;
        else if(this.month < date.month) return -1;
        if(this.day > date.day) return 1;
        else if(this.day < date.day) return -1;
        return 0;
    }
}
