package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ColorStateListDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnSkipLogin;
    private TextView tvToSignup;
    private EditText etEmail, etPassword;
    private CheckBox passShow;

    private FirebaseAuth mAuth;
    private FirebaseUser isLogin;
    private DatabaseReference reference;
    private String userID;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();

        // For Testing Mode
//        FirebaseAuth.getInstance().signOut();
//        finishAffinity();

        btnLogin = findViewById(R.id.button_login);
        tvToSignup = findViewById(R.id.text_click_to_signup);
        btnSkipLogin = findViewById(R.id.button_skip_login);

        etEmail = findViewById(R.id.login_email);
        etPassword = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.progressBar);
        passShow = findViewById(R.id.passShow);

        passShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    buttonView.setButtonTintList(getColorStateList(R.color.yellow));
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    buttonView.setButtonTintList(getColorStateList(R.color.gray_dark));
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected(MainActivity.this)) {
                    showCustomDialog();
                } else {
                    userLogin();
                }

//                Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

        tvToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected(MainActivity.this)) {
                    showCustomDialog();
                } else {
                    etEmail.setText("");
                    etPassword.setText("");
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
//                finish();
                }
            }
        });

        btnSkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected(MainActivity.this)) {
                    showCustomDialog();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        isLogin = FirebaseAuth.getInstance().getCurrentUser();
//        Log.e("LOGIN", String.valueOf(isLogin));

        // Check if User Logged In
        if(isLogin != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users");
            userID = isLogin.getUid();
            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if(userProfile != null) {
                        String username = userProfile.username;
                        progressBar.setVisibility(View.GONE);
                        finish();

                        Toast.makeText(MainActivity.this, "Welcome " + username, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, ExploreActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Something Wrong Happened!", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            progressBar.setVisibility(View.GONE);
        }

    }

    // Check Internet Connection
    private boolean isConnected(MainActivity mainActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if( (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected()) ) {
            return true;
        }
        else {
            return false;
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Please Connect to the Internet to Proceed Further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }).show();
    }

    private void userLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(email.isEmpty()) {
            etEmail.setError("Email is Required!");
            etEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please Provide Valid Email!");
            etEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            etPassword.setError("Password is Required!");
            etPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            etPassword.setError("Min Password Length Should Be 6 Characters!");
            etPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()) {
                        // Redirect to User Profile
                        finish();
                        Toast.makeText(MainActivity.this, "Successfully Logged In ", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, ExploreActivity.class));
                    } else {
                        user.sendEmailVerification();
                        FirebaseAuth.getInstance().signOut();
                        etEmail.setText("");
                        etPassword.setText("");
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Check Your Email to Verify Your Account!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to Login! Please Check Your Credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}