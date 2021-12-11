package edu.neu.madcourse.thingshub.FriendRecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.thingshub.R;

public class MyViewHolder extends RecyclerView.ViewHolder{
    public TextView friends;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        friends=itemView.findViewById(R.id.showFriend);
    }
}
