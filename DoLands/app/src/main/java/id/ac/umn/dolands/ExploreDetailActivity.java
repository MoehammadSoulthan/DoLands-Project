package id.ac.umn.dolands;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ExploreDetailActivity extends AppCompatActivity implements OnMapReadyCallback{
//    Button btnReview;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap gMap;
    TextView tvAttrName, tvLocationInformation;
    String locationInformationPlaceholder;

    // Detail Info From ExploreActivity
    String name, roadName, county, country, postcode, stateDistrict;
    Double lon, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_detail);

//        btnReview = findViewById(R.id.button_review);
        tvAttrName = findViewById(R.id.tvAttrName);
        tvLocationInformation = findViewById(R.id.tvLocationInformation);

        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            name = intent.getStringExtra("name");
            roadName = intent.getStringExtra("roadName");
            county = intent.getStringExtra("county");
            country = intent.getStringExtra("country");
            postcode = intent.getStringExtra("postcode");
            stateDistrict = intent.getStringExtra("stateDistrict");
            lon = intent.getDoubleExtra("lon", 0);
            lat = intent.getDoubleExtra("lat", 0);
        }

        setData();

//        btnReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ExploreDetailActivity.this, ExploreReviewActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0,0);
//                finish();
//            }
//        });

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