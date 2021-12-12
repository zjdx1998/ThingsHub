package edu.neu.madcourse.thingshub.FrontEnd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.thingshub.Model.User;
import edu.neu.madcourse.thingshub.R;
import edu.neu.madcourse.thingshub.Server.Server;

public class AddFriendActivity extends AppCompatActivity {
    Button addButton;
    EditText editFriendname;
    String FriendName="";
    Boolean newFriendOrNotFlag=true;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewfriends);
        addButton=findViewById(R.id.addButton);
        editFriendname=findViewById(R.id.editTextTextFriendName);

        addButton.setOnClickListener(v-> {
            process();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(AddFriendActivity.this, FriendActivity.class);
                    startActivity(intent);
                }
            };
            Timer timer = new Timer();
            timer.schedule(task,500);
        });

    }
    private void process(){
        FriendName=editFriendname.getText().toString();
        if (FriendName.isEmpty()) {
            Toast.makeText(AddFriendActivity.this, "Please enter the correct Friend name!", Toast.LENGTH_SHORT).show();
            return;
        }
        Server.getInstance().getFriends(User.getInstance().getUserName(), friends -> {
            for (String friend:friends){
                if (friend.equals(FriendName)){
                    Toast.makeText(AddFriendActivity.this,"This is already your friend!",Toast.LENGTH_SHORT).show();
                    newFriendOrNotFlag=false;
                    break;
                }
                if(FriendName.equals(User.getInstance().getUserName())){
                    Toast.makeText(AddFriendActivity.this,"Don't add yourself",Toast.LENGTH_SHORT).show();
                    newFriendOrNotFlag=false;
                    break;
                }
            }
            if(newFriendOrNotFlag){
                Server.getInstance().checkUser(FriendName,exist -> {
                    if(exist){
                        Server.getInstance().addFriend(FriendName);
                        Toast.makeText(AddFriendActivity.this,"Successfully added",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(AddFriendActivity.this,"No user named it,please check again",Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });
    }
}
