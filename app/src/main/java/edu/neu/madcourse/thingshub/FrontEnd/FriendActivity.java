package edu.neu.madcourse.thingshub.FrontEnd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import edu.neu.madcourse.thingshub.FriendRecyclerView.Friends;
import edu.neu.madcourse.thingshub.FriendRecyclerView.MyAdapter;
import edu.neu.madcourse.thingshub.Model.User;
import edu.neu.madcourse.thingshub.R;
import edu.neu.madcourse.thingshub.Server.Server;

public class FriendActivity extends AppCompatActivity {
    FloatingActionButton addfriendButton;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    public ArrayList<Friends> friends;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        recyclerView=findViewById(R.id.friendsView);
        addfriendButton=findViewById(R.id.addFriendsButton);
        friends=new ArrayList<>();
        createRecyclerView();
        addfriendButton.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(FriendActivity.this, AddFriendActivity.class);
            startActivity(intent);
        });
        myAdapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("UserName",friends.get(position).getName());
            intent.setClass(FriendActivity.this,ThingsList_activity.class);
            startActivity(intent);
        });
    }



    protected void onResume() {
        super.onResume();
        friends=new ArrayList<>();
        createRecyclerView();
        Server.getInstance().getFriends(User.getInstance().getUserName(),friendslist->{
            if (friendslist.size()>0){
                for (int i=0;i<friendslist.size();i++){
                    Friends friend = new Friends(friendslist.get(i));
                    friends.add(friend);
                    myAdapter.notifyItemInserted(friends.size()-1);
                    recyclerView.smoothScrollToPosition(friends.size()-1);
                }
            }
        });
    }

    private void createRecyclerView() {
        myAdapter = new MyAdapter(friends);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }


}
