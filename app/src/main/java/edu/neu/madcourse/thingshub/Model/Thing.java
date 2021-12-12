package edu.neu.madcourse.thingshub.Model;

public class Thing {
    private String thingsName;
    private Date startDate;
    private Date endDate;
    private Boolean isCompleted;
    private int color;
    private double longitude;
    private double latitude;

    public Thing(String thingsName, Date startDate, Date endDate, Boolean isCompleted, int color, double longitude, double latitude) {
        this.thingsName = thingsName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = isCompleted;
        this.color = color;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Thing(String thingsName, Date startDate, Date endDate, Boolean isCompleted, int color) {
        this.thingsName = thingsName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = isCompleted;
        this.color = color;
    }
    public Thing(){}


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public String toKey(){return getThingsName();}

    public String getThingsName() {
        return thingsName;
    }

    public void setThingsName(String thingsName) {
        this.thingsName = thingsName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Thing{" +
                "thingsName='" + thingsName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isCompleted=" + isCompleted +
                ", color=" + color +
                '}';
    }
}
