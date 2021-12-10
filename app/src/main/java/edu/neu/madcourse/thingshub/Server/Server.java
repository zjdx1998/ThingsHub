package edu.neu.madcourse.thingshub.Server;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.neu.madcourse.thingshub.Model.Date;
import edu.neu.madcourse.thingshub.Model.Thing;
import edu.neu.madcourse.thingshub.Model.User;
/*
 *  @author: jeromy zhang
 *  Class Server, run as singleton mode.
 */
public class Server {
    static FirebaseDatabase database;
    public static final String DB_URL = "https://thingshub-numda21fall-default-rtdb.firebaseio.com/";
    public Server(){
        database = FirebaseDatabase.getInstance();
    }
    private static Server instance;
    public static Server getInstance(){
        if(instance==null) instance = new Server();
        return instance;
    }
    public interface CheckUserExistCallback{
        public void onCheckUserExist(Boolean exist);
    }
    public void checkUser(String userName, CheckUserExistCallback cueCB){
        DatabaseReference dbRef = database.getReference("/Users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(userName)){
                    cueCB.onCheckUserExist(true);
                }else{
                    cueCB.onCheckUserExist(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    public interface CreateUserCallback{
        public void onUserCreated(Boolean created);
    }
    public void createUser(String userName, CreateUserCallback createCB){
        DatabaseReference dbRef = database.getReference("/Users");
        checkUser(userName, exist->{
            if(!exist){
                createCB.onUserCreated(true);
                dbRef.child(userName).setValue(new User(userName));
            }else{
                createCB.onUserCreated(false);
            }
        });
    }
    public void addThing(Thing thing){
        DatabaseReference dbRef = database.getReference("/Users/"+User.getInstance().getUserName());
        dbRef.child("Things").child(thing.toKey()).setValue(thing);
        User.getInstance().getThings().add(thing);
    }
    public interface GetThingsCallback{
        public void onGetThings(List<Thing> things);
    }
    public void getThings(String userName, GetThingsCallback gtCB){
        checkUser(userName, exist->{
            if(!exist){
                gtCB.onGetThings(null);
            }else{
                DatabaseReference dbRef = database.getReference("/Users/"+userName+"/Things");
                dbRef.get().addOnCompleteListener(task -> {
                    List<Thing> things = new ArrayList<Thing>();
                    if(task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Thing thing = dataSnapshot.getValue(Thing.class);
                                things.add(thing);
                            }
                        }
                    }
                    gtCB.onGetThings(things);
                });
            }
        });
    }

    public List<Thing> filterThings(List<Thing> things, boolean isCompleted){
        List<Thing> ret = new ArrayList<>();
        for(Thing thing : things){
            if (thing.getCompleted() == isCompleted) ret.add(thing);
        }
        return ret;
    }

    public List<Thing> filterThings(List<Thing> things, Date startDate){
        List<Thing> ret = new ArrayList<>();
        for(Thing thing : things){
            if(thing.getEndDate().compareTo(startDate) >= 0) ret.add(thing);
        }
        return ret;
    }
    public interface GetFriendsCallback{
        void onGetFriends(List<String> friends);
    }
    public void getFriends(String userName, GetFriendsCallback friendCB){
        checkUser(userName, exist->{
            if(!exist){
                friendCB.onGetFriends(null);
            }else{
                DatabaseReference dbRef = database.getReference("/Users/"+userName+"/Friends");
                dbRef.get().addOnCompleteListener(task -> {
                    List<String> friends = new ArrayList<>();
                    if(task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String friend = dataSnapshot.getValue(String.class);
                                if(!friends.contains(friend)) friends.add(friend);
                            }
                        }
                    }
                    friendCB.onGetFriends(friends);
                });
            }
        });
    }
    public void addFriend(String userName){
        checkUser(userName, exist->{
            if(exist){
                DatabaseReference dbRef = database.getReference("/Users/"+User.getInstance().getUserName()+"/Friends");
                dbRef.push().setValue(userName);
                dbRef = database.getReference("/Users/"+userName+"/Friends");
                dbRef.push().setValue(User.getInstance().getUserName());
                if(!User.getInstance().getFriends().contains(userName)) User.getInstance().getFriends().add(userName);
            }
        });
    }
    public void markCompleted(Thing thing){
        DatabaseReference dbRef = database.getReference("/Users/"+User.getInstance().getUserName()+"/Things");
        thing.setCompleted(true);
        dbRef.child(thing.toKey()).setValue(thing);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());
        dbRef = database.getReference("/Users/"+User.getInstance().getUserName()+"/History");
        dbRef.child(date).child(thing.toKey()).setValue(thing);
    }
    public interface GetHistoryCallback{
        void onGetHistory(Map<Date, List<Thing>> history);
    }
    public void getHistory(String userName, GetHistoryCallback historyCB){
        checkUser(userName, exist->{
            if(!exist){
                historyCB.onGetHistory(null);
            }else{
                DatabaseReference dbRef = database.getReference("/Users/"+userName+"/History");
                dbRef.get().addOnCompleteListener(task -> {
                    Map<Date, List<Thing>> histories = new HashMap<>();
                    if(task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            for (DataSnapshot historyDateSnapshot : snapshot.getChildren()) {
                                Date date = new Date(historyDateSnapshot.getKey());
                                for(DataSnapshot thingSnapshot : historyDateSnapshot.getChildren()){
                                    if(histories.get(date)==null){
                                        histories.put(date, new ArrayList<>());
                                    }
                                    histories.get(date).add(thingSnapshot.getValue(Thing.class));
                                }
                            }
                        }
                    }
                    historyCB.onGetHistory(histories);
                });
            }
        });
    }
    public int mixColor(List<Thing> things){
        int ans = 0;
        for(Thing thing : things){
            ans += thing.getColor();
        }
        return ans / things.size();
    }
}
