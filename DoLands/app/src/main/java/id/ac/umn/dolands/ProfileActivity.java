package id.ac.umn.dolands;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.ActionBar;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageButton;
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

public class ProfileActivity extends AppCompatActivity implements ProfileSavedAdapter.ClickedItem {
    private Button btnEditProfile;
    private ImageButton imgBtnExit;
    private TextView tvSaved, tvUsername, tvFullName, tvEmail, tvNoReviewYet;
    private CircleImageView circleImageView;
    private RecyclerView rvMyReview;
    private ProfileSavedAdapter myReviewAdapter;
    private List<SavedLocationModel> myReviewModelList;

    // Firebase
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
        setContentView(R.layout.activity_profile);

        imgBtnExit = findViewById(R.id.imgbutton_exit);
        btnEditProfile = findViewById(R.id.button_editprofile);
        tvSaved = findViewById(R.id.text_saved);
        tvUsername = findViewById(R.id.text_user_username);
        tvFullName = findViewById(R.id.text_user_fullname);
        tvEmail = findViewById(R.id.text_user_email);
        circleImageView = findViewById(R.id.circleImageView);
        tvNoReviewYet = findViewById(R.id.tvNoReviewYet);

        // Show User Information
        sessionManager = new SessionManager(ProfileActivity.this);
        showAllUserData();

        // Firebase Instance\
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        Uid = mAuth.getCurrentUser().getUid();

        // My Review RecyclerView=
        rvMyReview = findViewById(R.id.rvMyReview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvMyReview.setLayoutManager(linearLayoutManager);

        // Set myReviewAdapter
        myReviewAdapter = new ProfileSavedAdapter(this);

        // myReviewModelList
        myReviewModelList = new ArrayList<>();

        tvSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileSavedActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
//                finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imgBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
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
                switch (item.getItemId()){
                    case R.id.nav_explore:
                        finish();
                        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
                        overridePendingTransition(0,0);
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

        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    String username_img = task.getResult().getString("username");
                    String imageUrl = task.getResult().getString("image");
//                    Log.e("PROFILE PIC", String.valueOf(imageUrl));
                    if(imageUrl != null) {
                        Glide.with(ProfileActivity.this).load(imageUrl).into(circleImageView);
                    }
                }
            }
        });

        // Retrieving Saved Place From Firebase
        FirebaseDatabase.getInstance().getReference("MyReview").child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.e("KEY", String.valueOf(snapshot));
                myReviewModelList.clear();
                for(DataSnapshot savedLocationSnapshot: snapshot.getChildren()) {
                    SavedLocationModel savedLocationModel = savedLocationSnapshot.getValue(SavedLocationModel.class);
                    myReviewModelList.add(savedLocationModel);
                }

                myReviewAdapter.setData(myReviewModelList);
                rvMyReview.setAdapter(myReviewAdapter);

                if(!myReviewModelList.isEmpty()) {
                    rvMyReview.setVisibility(View.VISIBLE);
                    tvNoReviewYet.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    // Back To Exit
    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBottom);
        bottomNavigationView.setSelectedItemId(R.id.nav_explore);
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