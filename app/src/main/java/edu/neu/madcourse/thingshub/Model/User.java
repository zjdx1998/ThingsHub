package edu.neu.madcourse.thingshub.Model;

import java.util.List;
import java.util.Map;

import edu.neu.madcourse.thingshub.Server.Server;

public class User{
    private String userName;
    private Map<Date, List<Thing>> history;
    private List<Thing> things;
    private List<String> friends;

    public User(String userName){
        this.userName = userName;
    }

    public User(String userName, Map<Date, List<Thing>> history, List<Thing> things, List<String> friends) {
        this.userName = userName;
        this.history = history;
        this.things = things;
        this.friends = friends;
    }
    private static User instance;
    public static User getInstance(){
        return instance;
    }
    public static void setInstance(String userName){
        instance = new User(userName);
        Server.getInstance().getThings(instance.getUserName(), _things->{
            instance.setThings(_things);
            Server.getInstance().getHistory(instance.getUserName(), _history->{
                instance.setHistory(_history);
                Server.getInstance().getFriends(instance.getUserName(), _friends->{
                    instance.setFriends(_friends);
                });
            });
        });
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<Date, List<Thing>> getHistory() {
        return history;
    }

    public void setHistory(Map<Date, List<Thing>> history) {
        this.history = history;
    }

    public List<Thing> getThings() {
        return things;
    }

    public void setThings(List<Thing> things) {
        this.things = things;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}