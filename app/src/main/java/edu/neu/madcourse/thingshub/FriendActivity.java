package edu.neu.madcourse.thingshub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import edu.neu.madcourse.thingshub.FriendRecyclerView.Friends;
import edu.neu.madcourse.thingshub.FriendRecyclerView.MyAdapter;
import edu.neu.madcourse.thingshub.FriendRecyclerView.MyViewHolder;
import edu.neu.madcourse.thingshub.Model.User;
import edu.neu.madcourse.thingshub.Server.Server;

public class FriendActivity extends AppCompatActivity {
    FloatingActionButton addfriendButton;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    MyViewHolder myViewHolder;
    RecyclerView.LayoutManager layoutManager;
    public ArrayList<Friends> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        recyclerView=findViewById(R.id.friendsView);
        friends=new ArrayList<>();
        init(savedInstanceState);

        addfriendButton.setOnClickListener(view->{
            Intent intent = new Intent();
            intent.setClass(FriendActivity.this,AddFriendActivity.class);
            startActivity(intent);
                });
    }
    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
    }

    private void initialItemData(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            Server.getInstance().getFriends(User.getInstance().getUserName(),friendslist->{
                if(friendslist.size()==0){
                    Toast.makeText(FriendActivity.this,"You have no friends,Please add some",Toast.LENGTH_SHORT).show();
                }
                for (int i=0;i<friendslist.size();i++){
                    Friends friend = new Friends(friendslist.get(i));
                    friends.add(friend);
                    myAdapter.notifyItemInserted(friends.size()-1);
                    recyclerView.smoothScrollToPosition(friends.size()-1);
                }
                    }
                    );
        }


    }

    private void createRecyclerView() {
        myAdapter = new MyAdapter(friends);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

}
