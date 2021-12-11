package edu.neu.madcourse.thingshub.FrontEnd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.thingshub.Model.User;
import edu.neu.madcourse.thingshub.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    private Button updateButton;
    private TextView username;
    private EditText userSignature;
    private CircleImageView userImage;

    private String userName;

    private DatabaseReference RootRef;

    private static final int PICTURE_NUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Bundle extras = getIntent().getExtras();
        userName = extras.getString("username");

        RootRef = FirebaseDatabase.getInstance().getReference();

        initializeFields();
        getUserProfile();

        // update signature
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        // update user image
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pictureIntent = new Intent();
                pictureIntent.setAction(Intent.ACTION_GET_CONTENT);
                pictureIntent.setType("image/*");
                startActivityForResult(pictureIntent, PICTURE_NUM);
            }
        });
    }

    private void initializeFields() {
        username = (TextView) findViewById(R.id.account_name);
        updateButton = (Button) findViewById(R.id.account_update_button);
        userSignature = (EditText) findViewById(R.id.account_signature);
        userImage = (CircleImageView) findViewById(R.id.account_image);
    }

    private void updateSettings() {
        String setUserSignature = userSignature.getText().toString();

        Map<String, String> profileMap = new HashMap<>();
        profileMap.put("Signature", setUserSignature);

        // TODO: think about how to structure signature entry
        //  either Users -> Tom -> Signature (currently using this)
        //  or     Users -> Tom -> Profile -> signature & image
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Users/"+ userName + "/Signature");
        dbRef.setValue(setUserSignature)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(
                            AccountActivity.this,
                            "Profile update successfully!",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    String error = task.getException().toString();
                    Toast.makeText(
                            AccountActivity.this,
                            "error: " + error,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // TODO: need to add update profile image
    }

    private void getUserProfile() {
        RootRef.child("Users").child(userName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.hasChild("Signature")) {
                                String retrieveSignature = snapshot.child("Signature").getValue().toString();
                                userSignature.setText(retrieveSignature);
                            }
                            if (snapshot.hasChild("Image")) {
                                String retrieveImage = snapshot.child("Image").getValue().toString();
                            }
                        }
                        username.setText(userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
