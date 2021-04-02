package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ExploreActivity extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    ImageView ivAttract1,ivAttract2,ivAttract3,ivAttract4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        ivAttract1 = (ImageView) findViewById(R.id.attract1);
        ivAttract2 = (ImageView) findViewById(R.id.attract2);
        ivAttract3 = (ImageView) findViewById(R.id.attract3);
        ivAttract4 = (ImageView) findViewById(R.id.attract4);

        //intent ke detail
        ivAttract1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExploreActivity.this, ExploreDetailActivity.class);
                startActivity(intent);
            }
        });
        ivAttract2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExploreActivity.this, ExploreDetailActivity.class);
                startActivity(intent);
            }
        });
        ivAttract3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExploreActivity.this, ExploreDetailActivity.class);
                startActivity(intent);
            }
        });
        ivAttract4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExploreActivity.this, ExploreDetailActivity.class);
                startActivity(intent);
            }
        });

        //maps
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.current_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(ExploreActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }else {
            ActivityCompat.requestPermissions(ExploreActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        //menu bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBottom);

        bottomNavigationView.setSelectedItemId(R.id.nav_explore);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        //popup alert login
                        Dialog popup = new Dialog(ExploreActivity.this);
                        popup.setContentView(R.layout.popup_login);

                        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popup.setCancelable(false);

                        Button btn_ok = popup.findViewById(R.id.button_ok);
                        TextView tv_skip = popup.findViewById(R.id.text_skip);

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void  onClick(View v) {
                                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                                overridePendingTransition(0,0);
                            }
                        });

                        tv_skip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void  onClick(View v) {
                                bottomNavigationView.setSelectedItemId(R.id.nav_explore);
                                popup.dismiss();
                            }
                        });
                        popup.show();
                        return true;
                    case R.id.nav_explore:
                        return true;
                }
                return false;
            }
        });
    }

    //maps
    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("You are here");

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    //maps
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }
}