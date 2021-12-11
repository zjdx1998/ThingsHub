package edu.neu.madcourse.thingshub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.thingshub.Model.User;
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
        addButton=findViewById(R.id.addFriendsButton);
        editFriendname=findViewById(R.id.editTextTextFriendName);

        addButton.setOnClickListener(v-> {
            process();
            Intent intent = new Intent();
            intent.setClass(AddFriendActivity.this,FriendActivity.class);
            startActivity(intent);

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
            }
            if(newFriendOrNotFlag){
                Server.getInstance().addFriend(FriendName);
            }

        });
    }
}
