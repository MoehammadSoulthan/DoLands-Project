package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    private Button btnSignup;
    private TextView tvToLogin;
    private EditText etName, etUsername, etEmail, etPassword, etRepassword;
    private ProgressBar progressBar;
    private CheckBox passShow1, passShow2;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        btnSignup = findViewById(R.id.button_signup);
        tvToLogin = findViewById(R.id.text_click_to_login);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

//                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        etName = findViewById(R.id.input_name);
        etUsername = findViewById(R.id.input_username);
        etEmail = findViewById(R.id.input_email);
        etPassword = findViewById(R.id.input_password);
        etRepassword = findViewById(R.id.input_repassword);

        progressBar = findViewById(R.id.progressBar);

        passShow1 = findViewById(R.id.passShow1);
        passShow1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        passShow2 = findViewById(R.id.passShow2);
        passShow2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    etRepassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    buttonView.setButtonTintList(getColorStateList(R.color.yellow));
                } else {
                    etRepassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    buttonView.setButtonTintList(getColorStateList(R.color.gray_dark));
                }
            }
        });

    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repassword = etRepassword.getText().toString().trim();

        if(name.isEmpty()) {
            etName.setError("Name is Required!");
            etName.requestFocus();
            return;
        }

        if(username.isEmpty()) {
            etUsername.setError("Username is Required!");
            etUsername.requestFocus();
            return;
        }

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
            etPassword.setError("Re-type Password is Required!");
            etPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
            etPassword.setError("Min Password Length Should Be 6 Characters!");
            etPassword.requestFocus();
            return;
        }

        if(repassword.isEmpty()) {
            etRepassword.setError("Re-type Password is Required!");
            etRepassword.requestFocus();
            return;
        }

        if(repassword.length() < 6) {
            etRepassword.setError("Min Re-type Password Length Should Be 6 Characters!");
            etRepassword.requestFocus();
            return;
        }

        if(!repassword.matches(password)) {
            etRepassword.setError("Password Didn't Match!");
            etRepassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(name, username, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "User Has Been Registered Sucessfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        FirebaseAuth.getInstance().signOut();
//                                            Log.e("VALUEEE", String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));

                                        // Redirect to Login Layout!
//                                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Failed to Register! Try Again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SignupActivity.this, "Failed to Register! Try Again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

        /*
            Belum Ada Error Checker username dan email already exist !
        */
    }
}