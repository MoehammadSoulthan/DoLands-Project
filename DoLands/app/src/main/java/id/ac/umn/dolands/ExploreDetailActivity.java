package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ExploreDetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    Button btnSaveLoc, btnSendReview;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap gMap;
    TextView tvAttrName, tvLocationInformation, tvNoReviewsYet;
    String locationInformationPlaceholder;
    RecyclerView rvReview;
    EditText edit_review;
    SessionManager sessionManager;
    List<ReviewsModel> reviewsModelList = new ArrayList<>();
    ReviewsAdapter reviewsAdapter;

    // Detail Info From ExploreActivity
    String xid, name, roadName, county, country, postcode, stateDistrict, image;
    Double lon, lat;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    String imageUrl, Uid, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_detail);

        // Firebase Instance
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Uid = user.getUid();
        firestore = FirebaseFirestore.getInstance();

        tvAttrName = findViewById(R.id.tvAttrName);
        tvLocationInformation = findViewById(R.id.tvLocationInformation);
        btnSaveLoc = findViewById(R.id.btnSaveLoc);
        rvReview = findViewById(R.id.rvReview);
        tvNoReviewsYet = findViewById(R.id.tvNoReviewsYet);

        // RecyclerView
        rvReview = findViewById(R.id.rvReview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvReview.setLayoutManager(linearLayoutManager);

        // Review Section
        edit_review = findViewById(R.id.edit_review);
        btnSendReview = findViewById(R.id.btnSendReview);

        // Get Session
        sessionManager = new SessionManager(ExploreDetailActivity.this);

        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            xid = intent.getStringExtra("xid");
            name = intent.getStringExtra("name");
            roadName = intent.getStringExtra("roadName");
            county = intent.getStringExtra("county");
            country = intent.getStringExtra("country");
            postcode = intent.getStringExtra("postcode");
            stateDistrict = intent.getStringExtra("stateDistrict");
            lon = intent.getDoubleExtra("lon", 0);
            lat = intent.getDoubleExtra("lat", 0);
            image = intent.getStringExtra("image");
        }

        // Initialize Data
        setData();
        showReviews();

        btnSaveLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation(xid, name, roadName, county, country, postcode, stateDistrict, lon, lat, image);
            }
        });

        btnSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReview();
            }
        });

        // Settingan Action Bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.attract_map);
        supportMapFragment.getMapAsync(this);
    }

    private void setData() {
        tvAttrName.setText(name);
        locationInformationPlaceholder  = "Country: " + country + "\n";

        // Location Information
        if(roadName != null) {
            locationInformationPlaceholder += "Road Name: " + roadName + "\n";
        }

        if(county != null) {
            locationInformationPlaceholder += "County: " + county + "\n";
        }

        if(stateDistrict != null) {
            locationInformationPlaceholder += "State District: " + stateDistrict + "\n";
        }

        locationInformationPlaceholder += "Latitude: " + lat + "\n" + "Longitude: " + lon;
        tvLocationInformation.setText(locationInformationPlaceholder);
    }

    private void saveLocation(String xid, String name, String roadName, String county, String country, String postcode, String stateDistrict, Double lon, Double lat, String image) {
        SavedLocationModel savedLocationModel = new SavedLocationModel(xid, name, roadName, county, country, postcode, stateDistrict, lon, lat, image);
        FirebaseDatabase.getInstance().getReference("SavedPlace")
                .child(Uid).child(xid).setValue(savedLocationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Location Successfully Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to Save, Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Getting User Image
        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    imageUrl = task.getResult().getString("image");
                    if(imageUrl == null) {
                        imageUrl = "";
                    }
                } else {
                    imageUrl = "";
                }
            }
        });
    }

    private void sendReview() {
        if(!edit_review.getText().toString().isEmpty()) {

            // Reviews (For Detail)
            HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();
            username = userDetails.get(SessionManager.KEY_USERNAME);

            ReviewsModel reviewsModel = new ReviewsModel(imageUrl, username, edit_review.getText().toString());

            String reviewId = FirebaseDatabase.getInstance().getReference("Reviews").push().getKey();
            FirebaseDatabase.getInstance().getReference("Reviews")
                    .child(xid).child(reviewId).setValue(reviewsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        // Clear Edit Text
                        edit_review.getText().clear();
                        // Add to RecyclerView
                        Objects.requireNonNull(rvReview.getAdapter()).notifyItemInserted(reviewsModelList.size());
                        rvReview.smoothScrollToPosition(reviewsModelList.size());
//                        showReviews();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to Execute, Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // MyReviews (For User)
            SavedLocationModel savedLocationModel = new SavedLocationModel(xid, name, roadName, county, country, postcode, stateDistrict, lon, lat, image);
            FirebaseDatabase.getInstance().getReference("MyReview").child(Uid).child(xid).setValue(savedLocationModel);
        } else {
            edit_review.setError("Cannot Be Empty!");
        }
    }

    private void showReviews() {
        FirebaseDatabase.getInstance().getReference("Reviews").child(xid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewsModelList.clear();
                for(DataSnapshot reviewsSnapshot: snapshot.getChildren()) {
                    ReviewsModel reviewsModel = reviewsSnapshot.getValue(ReviewsModel.class);
                    reviewsModelList.add(reviewsModel);
                }

                reviewsAdapter = new ReviewsAdapter(reviewsModelList);
                rvReview.setAdapter(reviewsAdapter);

                if(!reviewsModelList.isEmpty()) {
                    rvReview.setVisibility(View.VISIBLE);
                    tvNoReviewsYet.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Show Maps
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapStyle(MapStyleOptions
                .loadRawResourceStyle(getApplicationContext(), R.raw.style_json));

        LatLng LatLng = new LatLng(lat, lon);
        gMap.addMarker(new MarkerOptions().position(LatLng).title(name));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng,11));
        gMap.getUiSettings().setScrollGesturesEnabled(false);
        gMap.getUiSettings().setZoomGesturesEnabled(false);
    }

}