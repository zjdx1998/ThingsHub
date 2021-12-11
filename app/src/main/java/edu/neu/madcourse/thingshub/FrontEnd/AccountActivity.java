package edu.neu.madcourse.thingshub.FrontEnd;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.thingshub.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    private Button updateButton;
    private TextView userName;
    private EditText userSignature;
    private CircleImageView userImage;

    private String name;
    private String signature;

    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Bundle extras = getIntent().getExtras();
        name = extras.getString("username");
        signature = extras.getString("signature");

        RootRef = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });
    }

    private void initializeFields() {
        userName = (TextView) findViewById(R.id.account_name);
        userName.setText(name);

        userSignature = (EditText) findViewById(R.id.account_signature);
        userSignature.setText(signature);

        userImage = (CircleImageView) findViewById(R.id.account_image);
    }

    private void updateSettings() {
        String setUserName = userName.toString();
        String setUserSignature = userSignature.getText().toString();

        Map<String, String> profileMap = new HashMap<>();
        profileMap.put("signature", setUserSignature);

        RootRef.child("Users").child(setUserName).child("Profile").setValue(profileMap)
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
    }
}
