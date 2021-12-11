package edu.neu.madcourse.thingshub.FrontEnd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;


import edu.neu.madcourse.thingshub.Model.User;
import edu.neu.madcourse.thingshub.R;
import edu.neu.madcourse.thingshub.Server.Server;

public class LoginActivity extends AppCompatActivity{
        Button loginButton;
        EditText editUsername;
        EditText editPassword;
        String userName="";
        String token;
        String msg;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_login);

                loginButton = findViewById(R.id.loginButton);
                editUsername = findViewById(R.id.editTextTextPersonName);
                editPassword = findViewById(R.id.editTextTextPassword);

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Something is wrong! Please restart your app!", Toast.LENGTH_SHORT).show();
                        } else {
                                token = task.getResult();
                                msg = token;
                                Log.e("CLIENT_REGISTRATION_TOKEN", msg);
                        }
                });

                loginButton.setOnClickListener(v -> {
                        login();
                        User.setInstance(editUsername.getText().toString());
                        Intent intent = new Intent();
                        intent.putExtra("username", editUsername.getText().toString());
                        intent.setClass(LoginActivity.this, AccountActivity.class);
                        startActivity(intent);
                });
        }

        private void login() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                userName = editUsername.getText().toString();
                if (userName.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Please enter the correct user name!", Toast.LENGTH_SHORT).show();
                        return;
                }
                Server.getInstance().checkUser(userName, check->{
                        if(check==Boolean.TRUE){
                                User.setInstance(userName);
                        }
                        else{
                                Server.getInstance().createUser(userName, created->{
                                        User.setInstance(userName);
                                });
                        }
                        System.out.println(userName);
                });
        }
}
