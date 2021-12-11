package edu.neu.madcourse.thingshub.FrontEnd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.thingshub.Model.User;
import edu.neu.madcourse.thingshub.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button3);
        User.setInstance("zjd"); // test
        button.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,ThingsList_activity.class);
            startActivity(intent);
        });

    }
}