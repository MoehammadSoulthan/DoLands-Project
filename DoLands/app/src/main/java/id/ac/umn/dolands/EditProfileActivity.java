package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private Button btnSaveEdit, btnChangeProfilePic;
    private TextView tvEmail;
    private EditText etUsername, etFullname;
    private CircleImageView circleImageView;
    private Uri mImageUri, downloadUri = null;

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String Uid;
    private SessionManager sessionManager;
    private DatabaseReference reference;
    private String username, fullname, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Setting Action Bar
        // getSupportActionBar().setTitle(Html.fromHtml("<font color=\"yellow\">"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        etUsername = findViewById(R.id.edit_username);
        etFullname = findViewById(R.id.edit_fullname);
        tvEmail = findViewById(R.id.text_user_email);
        circleImageView = findViewById(R.id.circleImageView);
        btnChangeProfilePic = findViewById(R.id.button_change_profilephoto);
        btnSaveEdit = findViewById(R.id.button_saveEdit);

        // Get User Data
        sessionManager = new SessionManager(EditProfileActivity.this);
        getUserData();

        btnChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(EditProfileActivity.this);
                    }
                }
            }
        });

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void getUserData() {
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        username = userDetails.get(SessionManager.KEY_USERNAME);
        fullname = userDetails.get(SessionManager.KEY_FULLNAME);
        email = userDetails.get(SessionManager.KEY_EMAIL);

        etUsername.setText(username);
        etFullname.setText(fullname);
        tvEmail.setText(email);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                circleImageView.setImageURI(mImageUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateUserInfo() {
//        String username = etUsername.getText().toString().trim();
//        if(mImageUri != null) {
//            btnChangeProfilePic.setError("Please Upload Picture!");
//            StorageReference imageRef = storageReference.child("Profile_Pics").child(Uid + ".jpg");
//
//            imageRef.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    if(task.isSuccessful()) {
//                        saveToFireStore(task, username, imageRef);
//                    }
//                    else {
//                        Toast.makeText(EditProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }

        if(checkValidation()) {
            sessionManager.logout();
            sessionManager = new SessionManager(EditProfileActivity.this);
            sessionManager.createLoginSession(username, fullname, email);
            Toast.makeText(this, "Data Has Been Updated!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Data Is Same Cannot Be Updated!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkValidation() {
        if(!username.equals(etUsername.getText().toString()) || !fullname.equals(etFullname.getText().toString())) {
            if(username.isEmpty()) {
                etUsername.setError("Username is Required!");
                etUsername.requestFocus();
                return false;
            }
            else {
                username = etUsername.getText().toString();
                reference.child(mAuth.getCurrentUser().getUid()).child("name").setValue(username);
            }

            if(fullname.isEmpty()) {
                etFullname.setError("Fullname is Required!");
                etFullname.requestFocus();
                return false;
            }
            else {
                fullname = etFullname.getText().toString();
                reference.child(mAuth.getCurrentUser().getUid()).child("name").setValue(fullname);
            }

            return true;
        }
        else {
            return false;
        }
    }
    private void saveToFireStore(Task<UploadTask.TaskSnapshot> task, String username, StorageReference imageRef) {
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadUri = uri;
                HashMap<String, Object> map = new HashMap<>();
                map.put("username", username);
                map.put("image", downloadUri.toString());

//                firestore.collection("Users").document(Uid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            Toast.makeText(SignupActivity.this, "Profile Setting Saved", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            }
        });
    }

    // Back Button On Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Back To Exit
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

}