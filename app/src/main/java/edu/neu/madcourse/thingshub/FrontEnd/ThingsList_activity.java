package edu.neu.madcourse.thingshub.FrontEnd;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hancher.contribution.ContributionConfig;
import com.hancher.contribution.ContributionItem;
import com.hancher.contribution.ContributionView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.neu.madcourse.thingshub.Model.Date;
import edu.neu.madcourse.thingshub.Model.Thing;
import edu.neu.madcourse.thingshub.Model.User;
import edu.neu.madcourse.thingshub.R;
import edu.neu.madcourse.thingshub.RecyclerView.MyAdapter;
import edu.neu.madcourse.thingshub.RecyclerView.Things;
import edu.neu.madcourse.thingshub.Server.Server;

public class ThingsList_activity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    RecyclerView.LayoutManager layoutManager;
    public ArrayList<Things> itemList ;
    private ContributionView myView;
    private String curUserName;
    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String title = data.getStringExtra(AddThing_activity.THINGS_NAME);
                        int color = data.getIntExtra(AddThing_activity.COLOR, 0);
                        Things item = new Things(title, color);
                        itemList.add(item);
                        mMyAdapter.notifyItemInserted(itemList.size()-1);
                        mRecyclerView.smoothScrollToPosition(itemList.size()-1);
                        Server.getInstance().addThing(new Thing(
                                title,
                                new Date(data.getStringExtra(AddThing_activity.START_DATE)),
                                new Date(data.getStringExtra(AddThing_activity.END_DATE)),
                                false,
                                color,
                                data.getDoubleExtra(AddThing_activity.LONGITUDE, -122.33739),
                                data.getDoubleExtra(AddThing_activity.LATITUDE, 47.62288)
                        ));
                        Snackbar.make(findViewById(R.id.addThingTab),
                                "The link was successfully added", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curUserName = getIntent().getExtras().getString("UserName");
        setContentView(R.layout.activity_things_list);
        mRecyclerView = findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        myView = findViewById(R.id.contributionView);
        init(savedInstanceState);

        fab = findViewById(R.id.addThingTab);
        fab.setOnClickListener(view -> {
            if(!curUserName.equals(User.getInstance().getUserName())) return;
            Intent intent = new Intent();
            intent.setClass(ThingsList_activity.this,AddThing_activity.class);
            launchSomeActivity.launch(intent);
        });
        initCalendar();
    }

    private void initCalendar() {
        List<ContributionItem> data = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR)-1, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        java.util.Date startDate = calendar.getTime();
        Server.getInstance().getHistory(curUserName, history -> {
            System.out.println(history);
            for (int i = 0; i < 365; i++) {
                ContributionItem curItem = new ContributionItem(calendar.getTime(), 0);
                Date curDate = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
                List<Thing> things = history.get(curDate.toKey());
                if(things!=null && !things.isEmpty()){
                    curItem.setColor(Server.getInstance().mixColor(things));
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                data.add(curItem);
            }
            ContributionConfig newConfig = new ContributionConfig();
            newConfig.setBorderColor(Server.BORDER_COLOR);
            int[] rkColor = new int[]{Server.BKG_COLOR};
            newConfig.setRankColor(rkColor);
            newConfig.setTxtColor(Server.DES_COLOR);
            myView.setData(startDate, data, newConfig);
        });
    }

    private void init(Bundle savedInstanceState) {
        Server.getInstance().getThings(curUserName, things->{
            if(things==null) return;
            for(Thing thing : things){
                itemList.add(new Things(thing.getThingsName(), thing.getColor()));
            }
            initialItemData(savedInstanceState);
            createRecyclerView();
        });
    }

    private void initialItemData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_ITEMS)) {
            if (itemList == null || itemList.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_ITEMS);
                for (int i = 0; i < size; i++) {
                    String name = savedInstanceState.getString(KEY_OF_INSTANCE + i + "0");
                    String color = savedInstanceState.getString(KEY_OF_INSTANCE + i + "1");
                    Things item = new Things(name, Integer.parseInt(color));
                    System.out.println(item.getName());
                    itemList.add(item);
                }
            }
        }
    }


    private void createRecyclerView() {
        mMyAdapter = new MyAdapter(itemList,ThingsList_activity.this);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMyAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
//        mMyAdapter.setOnItemLongClickListener((view, position) -> {
//        });
        GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                    View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = mRecyclerView.getChildLayoutPosition(childView);
                        Server.getInstance().markCompleted(itemList.get(position).getName());
                        itemList.remove(position);
                        mMyAdapter.notifyItemRemoved(position);
                        Toast.makeText(ThingsList_activity.this,position + "Mark as completed!",Toast.LENGTH_SHORT).show();
                    }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (mGestureDetector.onTouchEvent(e)) {
                    return true;
                }
                return false;
            }
        });
    }
}