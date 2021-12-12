package edu.neu.madcourse.thingshub.RecyclerView;

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

    List<Things> thingsList = new ArrayList<>();
    private Context context;

    public interface OnItemLongClickListener {
        void onItemLongClick(View view,int position);
    }
    private OnItemLongClickListener  onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener  onItemClickListener){
        this.onItemLongClickListener=onItemClickListener;
    }

    public MyAdapter(List<Things> thingsList, Context context) {
        this.thingsList = thingsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thingslist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String title = thingsList.get(position).getName();
        int color = thingsList.get(position).getColor();
        holder.title.setText(title);
        holder.colorImage.setBackgroundColor(color);

    }

    @Override
    public int getItemCount() {
        return thingsList.size();
    }
}
