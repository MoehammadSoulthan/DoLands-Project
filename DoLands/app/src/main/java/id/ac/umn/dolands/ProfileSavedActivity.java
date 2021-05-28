package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSavedActivity extends AppCompatActivity implements ProfileSavedAdapter.ClickedItem {
    private Button btnEditProfile, btnClearAllSaved;
    private ImageButton imgBtnExit;
    private TextView tvMyReview, tvUsername, tvFullName, tvEmail, tvNoSavedYet;
    private CircleImageView circleImageView;
    private RecyclerView rvSaved;
    private ProfileSavedAdapter profileSavedAdapter;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser isLogin;
    private DatabaseReference reference;
    private String userID;
    private SessionManager sessionManager;
    private FirebaseFirestore firestore;
    private String Uid;
    private List<SavedLocationModel> savedLocationModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_saved);

        imgBtnExit = findViewById(R.id.imgbutton_exit);
        btnEditProfile = findViewById(R.id.button_editprofile);
        tvMyReview = findViewById(R.id.text_my_review);
        circleImageView = findViewById(R.id.circleImageView);
        tvNoSavedYet = findViewById(R.id.tvNoSavedYet);
        btnClearAllSaved = findViewById(R.id.button_clear_all_saved);

        tvUsername = findViewById(R.id.text_user_username);
        tvFullName = findViewById(R.id.text_user_fullname);
        tvEmail = findViewById(R.id.text_user_email);

        // Saved Detail RecyclerView
        rvSaved = findViewById(R.id.rvSaved);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvSaved.setLayoutManager(linearLayoutManager);

        // Set profileSavedAdapter
        profileSavedAdapter = new ProfileSavedAdapter(this);

        // savedLocationModelList
        savedLocationModelList = new ArrayList<>();

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

        // Go To My Review Activity
        tvMyReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSavedActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        // Button Edit Profile
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSavedActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Button Logout
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

        // Button Clear All Saved Attraction
        btnClearAllSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSavedAttr();
            }
        });

        // Navigation Bar
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

    @Override
    protected void onStart() {
        super.onStart();

        // Retrieving Saved Place From Firebase
        FirebaseDatabase.getInstance().getReference("SavedPlace").child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.e("KEY", String.valueOf(snapshot));
                savedLocationModelList.clear();
                for(DataSnapshot savedLocationSnapshot: snapshot.getChildren()) {
                    SavedLocationModel savedLocationModel = savedLocationSnapshot.getValue(SavedLocationModel.class);
                    savedLocationModelList.add(savedLocationModel);
                }

                profileSavedAdapter.setData(savedLocationModelList);
                rvSaved.setAdapter(profileSavedAdapter);

                if(!savedLocationModelList.isEmpty()) {
                    rvSaved.setVisibility(View.VISIBLE);
                    tvNoSavedYet.setVisibility(View.GONE);
                    btnClearAllSaved.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Show User Data From Session
    private void showAllUserData() {
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        String username = userDetails.get(SessionManager.KEY_USERNAME);
        String fullname = userDetails.get(SessionManager.KEY_FULLNAME);
        String email = userDetails.get(SessionManager.KEY_EMAIL);

        tvUsername.setText(username);
        tvFullName.setText(fullname);
        tvEmail.setText(email);
    }

    private void clearAllSavedAttr() {
        Dialog popup = new Dialog(ProfileSavedActivity.this);
        popup.setContentView(R.layout.popup_clear_saved_attr);

        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setCancelable(false);

        Button btn_ok = popup.findViewById(R.id.button_ok);
        TextView tv_cancel = popup.findViewById(R.id.text_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete All Saved Attraction From Firebase
                FirebaseDatabase.getInstance().getReference("SavedPlace").child(Uid).removeValue();
                Toast.makeText(getApplicationContext(), "Successfully Clear All Saved Attraction!", Toast.LENGTH_SHORT).show();

                popup.dismiss();
                startActivity(new Intent(getApplicationContext(), ProfileSavedActivity.class));
                overridePendingTransition(0, 0);
                finish();
//                finishAffinity();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        popup.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void ClickedImage(SavedLocationModel savedLocationModel) {
        Intent intent = new Intent(this, ExploreDetailActivity.class);
        intent.putExtra("xid", savedLocationModel.getXid())
                .putExtra("name", savedLocationModel.getName())
                .putExtra("roadName", savedLocationModel.getRoadName())
                .putExtra("county", savedLocationModel.getCounty())
                .putExtra("country", savedLocationModel.getCountry())
                .putExtra("postcode", savedLocationModel.getPostcode())
                .putExtra("stateDistrict", savedLocationModel.getStateDistrict())
                .putExtra("lon", savedLocationModel.getLon())
                .putExtra("lat", savedLocationModel.getLat())
                .putExtra("image", savedLocationModel.getImage());
        startActivity(intent);
    }
}