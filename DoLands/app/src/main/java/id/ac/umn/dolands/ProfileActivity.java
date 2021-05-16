package id.ac.umn.dolands;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.ActionBar;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.material.bottomnavigation.BottomNavigationView;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    Button btnEditProfile;
    ImageButton imgBtnExit;
    TextView tvSaved, tvUsername, tvFullName, tvEmail;

    private FirebaseAuth mAuth;
    private FirebaseUser isLogin;
    private DatabaseReference reference;
    private String userID;
    private SessionManager sessionManager;

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


        // Show User Information
        sessionManager = new SessionManager(ProfileActivity.this);
        showAllUserData();
//        isLogin = FirebaseAuth.getInstance().getCurrentUser();
//        if(isLogin != null) {
//            reference = FirebaseDatabase.getInstance().getReference("Users");
//            userID = isLogin.getUid();
//            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    User userProfile = snapshot.getValue(User.class);
//
//                    if(userProfile != null) {
//                        String username = userProfile.username;
//                        String fullname = userProfile.name;
//                        String email = userProfile.email;
//
//                        tvUsername.setText(username);
//                        tvFullName.setText(fullname);
//                        tvEmail.setText(email);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(ProfileActivity.this, "Something Wrong Happened!", Toast.LENGTH_LONG).show();
//                }
//            });
//        }

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

    private void showAllUserData() {
//        Intent intent = getIntent();
//        String username = intent.getStringExtra("username");
//        String fullname = intent.getStringExtra("fullname");
//        String email = intent.getStringExtra("email");

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
//        int id = menuItem.getItemId();
//        if(id == R.id.nav_exit){
//            AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
//            alert.setMessage("Are You Sure to Exit?");
//            alert.setCancelable(true);
//
//            alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finishAffinity();
//                    System.exit(0);
//                }
//            });
//
//            alert.setPositiveButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//
//            AlertDialog alertDialog = alert.create();
//            alertDialog.show();
//        }
//        return true;
//    }
}