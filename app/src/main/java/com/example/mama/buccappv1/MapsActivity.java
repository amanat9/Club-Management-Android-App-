package com.example.mama.buccappv1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient client;
    private String lat;
    private String lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        Bundle extras = getIntent().getExtras();

        lat=extras.getString("lat");
        lon=extras.getString("lon");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestPermission();
        // Add a marker in Sydney and move the camera
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Log.e("bhul","long"+longitude+"  lat"+latitude);

                    //LatLng sydney = new LatLng(-34, 151);

                    LatLng mylocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(mylocation).title("ME"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                    Log.e("bhul","mama"+lat +" "+lon );
                    double Blong=Double.parseDouble(lon);
                    double Blat=Double.parseDouble(lat);
                    double diflo= Math.abs(longitude-Blong);
                    double diflat=Math.abs(latitude-Blat);
                    Log.e("bhul",latitude+" "+longitude);
                    Log.e("bhul",diflat+" "+diflo);

                    if((diflo<0.01 && diflat<0.01) ) {

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i =new Intent(getApplicationContext(),Testattendance.class);
                                startActivity(i);
                            }
                        }, 5000);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"You are not in the event Location. \nso cannot have attendance",Toast.LENGTH_LONG).show();
                        Intent i =new Intent(getApplicationContext(),UEActivity.class);
                        startActivity(i);

                    }

                }
            }
        });
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }

}
