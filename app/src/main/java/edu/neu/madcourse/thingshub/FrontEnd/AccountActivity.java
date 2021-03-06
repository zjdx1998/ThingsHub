package edu.neu.madcourse.thingshub.FrontEnd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.thingshub.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    private Button updateButton;
    private Button friendsButton;
    private Button profileButton;
    private TextView username;
    private EditText userSignature;
    private CircleImageView userImage;

    private String userName;

    private DatabaseReference RootRef;
    private StorageReference userImageRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Uri imageUri;
    ValueEventListener valueEventListener;

    private static final int PICTURE_NUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Bundle extras = getIntent().getExtras();
        userName = extras.getString("username");

        RootRef = FirebaseDatabase.getInstance().getReference();
        userImageRef = FirebaseStorage.getInstance().getReference().child("User Images");

        // Enable sign in anonymously in case sign in error
        mAuth = FirebaseAuth.getInstance();
        signInAnonymously();

        initializeFields();

        // update signature
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        //go friends page
        friendsButton.setOnClickListener(view -> {
            Intent friendIntent = new Intent();
            friendIntent.setClass(AccountActivity.this, FriendActivity.class);
            startActivity(friendIntent);
        });

        //go profile page
        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent();
            profileIntent.putExtra("UserName", userName);
            profileIntent.setClass(AccountActivity.this, ThingsList_activity.class);
            startActivity(profileIntent);
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

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // Continue
            }
        }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }

    private void initializeFields() {
        username = (TextView) findViewById(R.id.account_name);
        updateButton = (Button) findViewById(R.id.account_update_button);
        userSignature = (EditText) findViewById(R.id.account_signature);
        userImage = (CircleImageView) findViewById(R.id.account_image);
        loadingBar = new ProgressDialog(this);
        friendsButton = findViewById(R.id.account_friends_button);
        profileButton = findViewById(R.id.account_profile_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_NUM && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            userImage.setImageURI(imageUri);

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Set User Image");
                loadingBar.setMessage("Image is uploading...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();
                final StorageReference filePath = userImageRef.child(userName + ".jpg");

                filePath.putFile(resultUri).continueWith(new Continuation<UploadTask.TaskSnapshot, Object>() {
                    @Override
                    public Object then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        final String downloadedUrl = task.getResult().toString();

                                        RootRef.child("Users").child(userName).child("Image")
                                                .setValue(downloadedUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(
                                                                    AccountActivity.this,
                                                                    "Image save successfully!",
                                                                    Toast.LENGTH_SHORT)
                                                                    .show();
                                                        } else {
                                                            String error = task.getException().toString();
                                                            Toast.makeText(
                                                                    AccountActivity.this,
                                                                    "Save image error: " + error,
                                                                    Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                        loadingBar.dismiss();
                                                    }
                                                });
                                    } else {
                                        String error = task.getException().toString();
                                        Toast.makeText(
                                                AccountActivity.this,
                                                "Download image error: " + error,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(
                                    AccountActivity.this,
                                    "Upload image error: " + error,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            loadingBar.dismiss();
                        }
                        return null;
                    }
                });
            }
        }
    }

    private void updateSettings() {
        String setUserSignature = userSignature.getText().toString();

        Map<String, String> profileMap = new HashMap<>();
        profileMap.put("Signature", setUserSignature);

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
    }

    private void getUserProfile() {
        RootRef.child("Users").child(userName)
                .addValueEventListener(valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.hasChild("Signature")) {
                                String retrieveSignature = snapshot.child("Signature").getValue().toString();
                                userSignature.setText(retrieveSignature);
                            }
                            if (snapshot.hasChild("Image") && !snapshot.child("Image").getValue().toString().isEmpty()) {
                                String retrieveImage = snapshot.child("Image").getValue().toString();
                                Picasso.get().load(retrieveImage).into(userImage);
                            }
                        }
                        username.setText(userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        RootRef.child("Users").child(userName).removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserProfile();
    }
}
