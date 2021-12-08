package edu.neu.madcourse.thingshub.Model;

public class Date{
    private String year;
    private String month;
    private String day;

    public Date(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String toKey(){ //==> same as toString()
        return this.year+"-"+this.month+"-"+this.day;
    }

    @Override
    public String toString() {
        return toKey();
    }
}
