package id.ac.umn.dolands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    ImageView ivAttract1, ivAttract2, ivAttract3, ivAttract4;
    private long backPressedTime;
    MaterialSearchBar searchBar;
    GoogleMap mGoogleMap;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    Helper helper;
    ArrayList<LocationInfoModel> locationInfoModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        ivAttract1 = findViewById(R.id.attract1);
        ivAttract2 = findViewById(R.id.attract2);
        ivAttract3 = findViewById(R.id.attract3);
        ivAttract4 = findViewById(R.id.attract4);

        // Making ApiClient
        helper = ApiClient.getClient().create(Helper.class);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
//                startSearch(text.toString(), true, null, true);
                String locationName = text.toString();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationName, 1);

                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        gotoLocation(address.getLatitude(), address.getLongitude(), address.getFeatureName());

                        Toast.makeText(getApplicationContext(), address.getFeatureName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Not Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

        // Intent to Detail
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

        // User Login
        user = FirebaseAuth.getInstance().getCurrentUser();
//        Log.e("USER", String.valueOf(user));

        // Maps
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.current_map);
        supportMapFragment.getMapAsync(this);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(ExploreActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            statusCheck();
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(ExploreActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        // Menu Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBottom);
        bottomNavigationView.setSelectedItemId(R.id.nav_explore);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        if (user == null) {
                            // Popup Alert Login
                            Dialog popup = new Dialog(ExploreActivity.this);
                            popup.setContentView(R.layout.popup_login);

                            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            popup.setCancelable(false);

                            Button btn_ok = popup.findViewById(R.id.button_ok);
                            TextView tv_skip = popup.findViewById(R.id.text_skip);

                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popup.dismiss();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finishAffinity();
                                }
                            });

                            tv_skip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bottomNavigationView.setSelectedItemId(R.id.nav_explore);
                                    popup.dismiss();
                                }
                            });
                            popup.show();
                            return true;
                        } else {
                            reference = FirebaseDatabase.getInstance().getReference("Users");
                            userID = user.getUid();
                            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User userProfile = snapshot.getValue(User.class);

                                    if (userProfile != null) {
                                        String username = userProfile.username;
                                        String fullname = userProfile.name;
                                        String email = userProfile.email;

//                                        Intent intentReview = new Intent(getApplicationContext(), ProfileActivity.class);
//                                        intentReview.putExtra("username", username);
//                                        intentReview.putExtra("fullname", fullname);
//                                        intentReview.putExtra("email", email);
//                                        startActivity(intentReview);

                                        SessionManager sessionManager = new SessionManager(ExploreActivity.this);
                                        sessionManager.createLoginSession(username, fullname, email);

                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                        overridePendingTransition(0, 0);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ExploreActivity.this, "Something Wrong Happened!", Toast.LENGTH_LONG).show();
                                }
                            });

//                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                            overridePendingTransition(0,0);
                        }
                    case R.id.nav_explore:
                        getCurrentLocation();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        getCurrentLocation();
    }

    // Check GPS Status
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    // GPS Permission Alert
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This App needs the Location Permission, Please accept to use Location Functionality!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        dialog.cancel();
//                    }
//                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    // Maps
    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.setMyLocationEnabled(true);
                            gotoLocation(location.getLatitude(), location.getLongitude(), "Current Location");
                        }
                    });
                }
            }
        });
    }

    // Goto Location Based On User Input
    private void gotoLocation(double latitude, double longitude, String featureName) {
        mGoogleMap.clear();
        LatLng LatLng = new LatLng(latitude, longitude);
        MarkerOptions options = new MarkerOptions().position(LatLng).title(featureName);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 11));
        mGoogleMap.addMarker(options);

        // Radius (meter)
        CircleOptions circleOptions = new CircleOptions()
                .center(LatLng)
                .radius(10000.0)
                .strokeColor(Color.YELLOW)
                .fillColor(Color.argb(30, 255, 255, 0));
        mGoogleMap.addCircle(circleOptions);

        getNearbyPlace(longitude, latitude);

//        Log.e("LatLng", latitude + " + " + longitude);

//        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + // Url
//                "?location=" + latitude + "," + longitude + // Location Latitude and Longitude
//                "&radius=10000" + // Nearby Radius
//                "&types=tourist_attraction" + // Place Type
//                "&sensor=true" + // Sensor
//                "&key=" + getResources().getString(R.string.API_1); // Google Map API Key

//        String url = "https://api.opentripmap.com/0.1/en/places/" + // Url
//                "radius?radius=10000" +
//                "&lon=" + longitude +
//                "&lat=" + latitude +
//                "&kinds=amusements%2Cinteresting_places" +
//                "&format=json" +
//                "&apikey=5ae2e3f221c38a28845f05b60967e742718eec2d1af005dd5c8deea0";
//        Log.e("URL", url);
    }

    private void getNearbyPlace(double longitude, double latitude) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("radius", "10000");
        hashMap.put("lon", String.valueOf(longitude));
        hashMap.put("lat", String.valueOf(latitude));
        hashMap.put("kinds", "amusements,interesting_places");
        hashMap.put("format", "json");
        hashMap.put("apikey", "5ae2e3f221c38a28845f05b60967e742718eec2d1af005dd5c8deea0");

        Call<ArrayList<LocationInfoModel>> locationInfoCallback = helper.getPost(hashMap);
        locationInfoCallback.enqueue(new Callback<ArrayList<LocationInfoModel>>() {
            @Override
            public void onResponse(Call<ArrayList<LocationInfoModel>> call, Response<ArrayList<LocationInfoModel>> response) {
                locationInfoModels = response.body();

                if(locationInfoModels.size() > 0) {
                    LatLng LatLngNearby = new LatLng(locationInfoModels.get(10).getPoint().getLat(), locationInfoModels.get(10).getPoint().getLon());
                    MarkerOptions options = new MarkerOptions().position(LatLngNearby).title(locationInfoModels.get(10).getName());
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mGoogleMap.addMarker(options);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LocationInfoModel>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Request Permission Maps
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                statusCheck();
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapStyle(MapStyleOptions
                .loadRawResourceStyle(getApplicationContext(), R.raw.style_json));
        mGoogleMap.setTrafficEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ExploreActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    // Back To Exit
    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity();
            super.onBackPressed();
            System.exit(0);
            return;
        } else {
            Toast.makeText(getBaseContext(), "Back Again To Exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }
}