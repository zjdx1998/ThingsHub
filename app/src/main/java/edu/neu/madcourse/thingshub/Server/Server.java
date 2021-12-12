package edu.neu.madcourse.thingshub.Server;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
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
    public static final int BKG_COLOR = 0xFFEBEDF0;
    public static final int DES_COLOR = 0xFF9E9E9E;
    public static final int BORDER_COLOR = 0xFF9E9E9E;
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
                dbRef.child(userName).setValue(userName);
                dbRef = database.getReference("/Users/"+userName+"/Friends");
                dbRef.child(User.getInstance().getUserName()).setValue(User.getInstance().getUserName());
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

    public void markCompleted(String thingName){
        DatabaseReference dbRef = database.getReference("/Users/" + User.getInstance().getUserName() + "/Things");
        DatabaseReference[] finalDbRef = {dbRef};
        dbRef.child(thingName).get().addOnCompleteListener(task ->{
            if(!task.isSuccessful()) return;
            Thing thing = task.getResult().getValue(Thing.class);
            thing.setCompleted(true);
            finalDbRef[0].child(thingName).setValue(thing);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String date = df.format(Calendar.getInstance().getTime());
            finalDbRef[0] = database.getReference("/Users/"+User.getInstance().getUserName()+"/History");
            finalDbRef[0].child(date).child(thing.toKey()).setValue(thing);
        });

    }

    public interface GetHistoryCallback{
        void onGetHistory(Map<String, List<Thing>> history);
    }
    public void getHistory(String userName, GetHistoryCallback historyCB){
        checkUser(userName, exist->{
            if(!exist){
                historyCB.onGetHistory(null);
            }else{
                DatabaseReference dbRef = database.getReference("/Users/"+userName+"/History");
                dbRef.get().addOnCompleteListener(task -> {
                    Map<String, List<Thing>> histories = new HashMap<>();
                    if(task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            for (DataSnapshot historyDateSnapshot : snapshot.getChildren()) {
                                String date = historyDateSnapshot.getKey();
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

    int blend( int i1, int i2, double ratio ) {
        if ( ratio > 1f ) ratio = 1f;
        else if ( ratio < 0f ) ratio = 0f;
        double iRatio = 1.0f - ratio;

        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = (int)((a1 * iRatio) + (a2 * ratio));
        int r = (int)((r1 * iRatio) + (r2 * ratio));
        int g = (int)((g1 * iRatio) + (g2 * ratio));
        int b = (int)((b1 * iRatio) + (b2 * ratio));

        return  a << 24 | r << 16 | g << 8 | b ;
    }

    public int mixColor(List<Thing> things){
        int ans = Server.BKG_COLOR;
        for(Thing thing : things){
            ans = blend(ans, thing.getColor(), 0.5);
        }
        return ans;
    }

    public String getAddress(Context ctx, @NonNull Location location){
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses.get(0).getAddressLine(0);
    }
}
