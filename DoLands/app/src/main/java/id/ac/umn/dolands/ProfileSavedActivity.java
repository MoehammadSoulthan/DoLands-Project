package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSavedActivity extends AppCompatActivity {
    private Button btnEditProfile;
    private ImageButton imgBtnExit;
    private TextView tvMyReview, tvUsername, tvFullName, tvEmail;;
    private RelativeLayout rlSaved1;
    private CircleImageView circleImageView;

    private FirebaseAuth mAuth;
    private FirebaseUser isLogin;
    private DatabaseReference reference;
    private String userID;
    private SessionManager sessionManager;
    private FirebaseFirestore firestore;
    private String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_saved);

        imgBtnExit = findViewById(R.id.imgbutton_exit);
        btnEditProfile = findViewById(R.id.button_editprofile);
        tvMyReview = findViewById(R.id.text_my_review);
        rlSaved1 = findViewById(R.id.saved_attract1);
        circleImageView = findViewById(R.id.circleImageView);

        tvUsername = findViewById(R.id.text_user_username);
        tvFullName = findViewById(R.id.text_user_fullname);
        tvEmail = findViewById(R.id.text_user_email);

        // Show User Information
        sessionManager = new SessionManager(ProfileSavedActivity.this);
        showAllUserData();

        // Display Profile Image
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        Uid = mAuth.getCurrentUser().getUid();

        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    String username_img = task.getResult().getString("username");
                    String imageUrl = task.getResult().getString("image");
//                    Log.e("PROFILE PIC", String.valueOf(imageUrl));
                    if(imageUrl != null) {
                        Glide.with(ProfileSavedActivity.this).load(imageUrl).into(circleImageView);
                    }
                }
            }
        });

        rlSaved1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSavedActivity.this, ExploreDetailActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        tvMyReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSavedActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSavedActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imgBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProfileSavedActivity.this);
                alert.setMessage("Are you sure want to Logout?");
                alert.setCancelable(true);

                alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finishAffinity();
//                        System.exit(0);

                        FirebaseAuth.getInstance().signOut();
                        sessionManager.logout();
                        BottomNavigationView bottomNavigationView = findViewById(R.id.navBottom);
                        bottomNavigationView.setSelectedItemId(R.id.nav_explore);
                        finishAffinity();
                    }
                });

                alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navBottom);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_explore:
                        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_profile:
                        return true;
                }
                return false;
            }
        });

    }

    private void showAllUserData() {
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        String username = userDetails.get(SessionManager.KEY_USERNAME);
        String fullname = userDetails.get(SessionManager.KEY_FULLNAME);
        String email = userDetails.get(SessionManager.KEY_EMAIL);

        tvUsername.setText(username);
        tvFullName.setText(fullname);
        tvEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}