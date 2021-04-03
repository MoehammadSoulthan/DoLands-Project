package id.ac.umn.dolands;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ExploreDetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    Button btnReview;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_detail);
        btnReview = findViewById(R.id.button_review);

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExploreDetailActivity.this, ExploreReviewActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //settingan action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.attract_map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        LatLng Safari = new LatLng(-7.759321,112.667188);
        gMap.addMarker(new MarkerOptions().position(Safari).title("Taman Safari Prigen"));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Safari,15));
    }

}