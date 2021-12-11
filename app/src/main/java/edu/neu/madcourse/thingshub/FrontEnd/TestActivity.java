package edu.neu.madcourse.thingshub.FrontEnd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.List;
import java.util.Map;

import edu.neu.madcourse.thingshub.FrontEnd.AccountActivity;
import edu.neu.madcourse.thingshub.Model.Date;
import edu.neu.madcourse.thingshub.Model.Thing;
import edu.neu.madcourse.thingshub.Model.User;
import edu.neu.madcourse.thingshub.R;
import edu.neu.madcourse.thingshub.Server.Server;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Server.getInstance().createUser("zjd", created->{
            User.setInstance("zjd");
        });
        Button testBtn = findViewById(R.id.TestButton);
        testBtn.setOnClickListener(v->{
            testCheckUser();
            testCreateUser();
            testAddThing();
            testGetThings();
            testFilterThings();
            testGetFriends();//should output dyn
            testGetHistory();//should output 12-10 singing

            // AccountActivity Test
//            Intent intent = new Intent();
//            intent.putExtra("username", "zjd");
//            intent.setClass(TestActivity.this, AccountActivity.class);
//            startActivity(intent);
        });
    }

    public void testCheckUser() {
        Server.getInstance().checkUser("zjd", exist -> {
            assert exist==true;
        });
    }

    public void testCreateUser() {
        Server.getInstance().createUser("zjd", created -> {
            assert created==false;
        });
        Server.getInstance().createUser("dyn", created -> {
            assert created==false;
        });
    }

    public void testAddThing() {
        Server.getInstance().addThing(new Thing(
                "Singing",
                new Date(2021,12,5),
                new Date(2021,12,8),
                false,
                123111
        ));
    }

    public void testGetThings() {
        Server.getInstance().getThings(User.getInstance().getUserName(), things->{
            for (Thing thing : things){
                System.out.println(thing);
            }
        });
    }

    public void testFilterThings() {//print all things that end after 2021-12-9
        Server.getInstance().getThings(User.getInstance().getUserName(), things->{
            things = Server.getInstance().filterThings(things, new Date(2021,12,9));
            for (Thing thing : things){
                System.out.println(thing);
            }
        });
    }
    public void testAddFriend() {
        Server.getInstance().addFriend("dyn");
    }

    public void testGetFriends() {
        testAddFriend();
        Server.getInstance().getFriends("zjd", friends -> {
            for (String friend:friends){
                System.out.println(friend);
            }
        });
    }

    public void testMarkCompleted() {
        Server.getInstance().markCompleted(new Thing(
                "Singing",
                new Date(2021,12,5),
                new Date(2021,12,8),
                false,
                123111
        ));
    }

    public void testGetHistory() {
        testMarkCompleted();
        Server.getInstance().getHistory("zjd",history -> {
            for(Map.Entry<Date, List<Thing>> p : history.entrySet()){
                System.out.println(p.getKey() + "\n ------------------- \n" + p.getValue());
            }
        });
    }

    public void testMixColor() {
    }
}