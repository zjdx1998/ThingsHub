package edu.neu.madcourse.thingshub.Model;

import java.util.List;
import java.util.Map;

public class User{
    private String userName;
    private Map<Date, List<Things>> history;
    private List<Things> things;

    public User(){ }

    public User(String userName, Map<Date, List<Things>> history, List<Things> things) {
        this.userName = userName;
        this.history = history;
        this.things = things;
    }
    private User instance;
    public User getInstance(){
        if(instance==null) instance = new User();
        return instance;
    }
}