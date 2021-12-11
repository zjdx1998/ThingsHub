package com.hancher.contribution;

import java.util.Date;

public class ContributionItem {

    private Date time;
    private int number;
    private int row;
    private int col;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private int color;

    public ContributionItem(Date time, int number) {
        this.time = time;
        this.number = number;
        this.color = 0xFFEBEDF0;
    }

    public Date getTime() {
        return time;
    }

    public ContributionItem setTime(Date time) {
        this.time = time;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public ContributionItem setNumber(int number) {
        this.number = number;
        return this;
    }

    public int getRow() {
        return row;
    }

    public ContributionItem setRow(int row) {
        this.row = row;
        return this;
    }

    public int getCol() {
        return col;
    }

    public ContributionItem setCol(int col) {
        this.col = col;
        return this;
    }
}
