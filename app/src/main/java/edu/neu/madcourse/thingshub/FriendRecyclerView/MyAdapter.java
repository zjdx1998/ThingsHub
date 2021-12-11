package edu.neu.madcourse.thingshub.FriendRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.thingshub.R;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    List<Friends> friendsList = new ArrayList<>();

    public MyAdapter(List<Friends>friendsList){
        this.friendsList=friendsList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendslist,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = friendsList.get(position).getName();
        holder.friends.setText(name);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

}
