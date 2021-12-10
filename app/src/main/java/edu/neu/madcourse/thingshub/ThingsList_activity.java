package edu.neu.madcourse.thingshub;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import edu.neu.madcourse.thingshub.RecyclerView.MyAdapter;
import edu.neu.madcourse.thingshub.RecyclerView.Things;

public class ThingsList_activity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    RecyclerView.LayoutManager layoutManager;
    public ArrayList<Things> itemList ;
    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String title = data.getStringExtra(AddThing_activity.EXTRA_TITLE);
                        System.out.println(title);
                        String color = data.getStringExtra(AddThing_activity.EXTRA_DESCRIPTION);
                        Things item = new Things(title,Integer.parseInt(color));
                        itemList.add(item);
                        Snackbar.make(findViewById(R.id.addThingTab),
                                "The link was successfully added", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things_list);
        mRecyclerView = findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();

        init(savedInstanceState);

        fab = findViewById(R.id.addThingTab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(ThingsList_activity.this,AddThing_activity.class);
            launchSomeActivity.launch(intent);
        });
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
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
    }
}