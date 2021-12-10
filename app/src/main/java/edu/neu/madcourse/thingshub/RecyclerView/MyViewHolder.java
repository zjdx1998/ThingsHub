package edu.neu.madcourse.thingshub.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.thingshub.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public ImageView colorImage;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.showTitle);
        colorImage = itemView.findViewById(R.id.colorImage);
    }

}
