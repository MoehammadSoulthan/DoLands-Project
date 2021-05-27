package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExploreDetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    Button btnSaveLoc;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap gMap;
    TextView tvAttrName, tvLocationInformation;
    String locationInformationPlaceholder;

    // Detail Info From ExploreActivity
    String xid, name, roadName, county, country, postcode, stateDistrict, image;
    Double lon, lat;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_detail);

        // Firebase Instance
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Uid = user.getUid();

        tvAttrName = findViewById(R.id.tvAttrName);
        tvLocationInformation = findViewById(R.id.tvLocationInformation);
        btnSaveLoc = findViewById(R.id.btnSaveLoc);

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

        btnSaveLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation(xid, name, roadName, county, country, postcode, stateDistrict, lon, lat, image);
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