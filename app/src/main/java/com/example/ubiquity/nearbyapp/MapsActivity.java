package com.example.ubiquity.nearbyapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ubiquity.nearbyapp.Model.MyPlaces;
import com.example.ubiquity.nearbyapp.Model.Results;
import com.example.ubiquity.nearbyapp.Remote.IGoogleAPIService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
        {

    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private  double latitude,longitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;

    IGoogleAPIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Init service
        mService= Common.getGoogleAPIService();

        //Runtime Permission

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        BottomNavigationView bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch(item.getItemId()) {
                   case R.id.action_hospital:
                       nearByPlace("hospital");
                       break;
                   case R.id.action_market:
                       nearByPlace("market");
                       break;
                   case R.id.action_school:
                       nearByPlace("school");
                       break;
                   case R.id.action_restaurant:
                       nearByPlace("restaurant");
                       break;
                   default:
                       break;
               }
                return true;
            }
        });
    }

            private void nearByPlace(final String placeType) {
                mMap.clear();
                String url= getUrl(latitude,longitude,placeType);

                mService.getNearByPlaces(url)
                        .enqueue(new Callback<MyPlaces>() {
                            @Override
                            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                                if(response.isSuccessful()) {
                                    for(int i=0; i<response.body().getResults().length;i++) {
                                        MarkerOptions markerOptions= new MarkerOptions();
                                        Results googlePlace= response.body().getResults()[i];
                                        double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                        double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());

                                        String placeName=googlePlace.getName();
                                        String vicinity=googlePlace.getVicinity();

                                        LatLng latlng= new LatLng(lat,lng);
                                        markerOptions.position(latlng);
                                        markerOptions.title(placeName);
                                        if(placeType.equals("hospital")) {
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        }
                                        else if (placeType.equals("restaurant")) {
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        }
                                        else if (placeType.equals("school")) {
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        }
                                        else if (placeType.equals("market")) {
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        }
                                        else{
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                        }

                                        mMap.addMarker(markerOptions);

                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyPlaces> call, Throwable t) {

                            }
                        });

            }

            private String getUrl(double latitude, double longitude, String placeType) {
            StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlaceUrl.append("location="+latitude+","+longitude);
            googlePlaceUrl.append("&radius="+1000);
            googlePlaceUrl.append("&type="+placeType);
            googlePlaceUrl.append("&sensor=true");
            googlePlaceUrl.append("&key="+getResources().getString(R.string.browser_key));
            Log.d("getUrl", googlePlaceUrl.toString());
            return googlePlaceUrl.toString();
    }

            private boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

           if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
               ActivityCompat.requestPermissions(this, new String[]{

                       Manifest.permission.ACCESS_FINE_LOCATION
               }, MY_PERMISSION_CODE);
           }
           else{
               ActivityCompat.requestPermissions(this, new String[]{

                       Manifest.permission.ACCESS_FINE_LOCATION
               }, MY_PERMISSION_CODE);
           }
           return false;
        }
        else{
            return true;
        }
    }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                switch (requestCode) {
                    case MY_PERMISSION_CODE: {

                        if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {

                            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                                if(mGoogleApiClient== null) {
                                    buildGoogleAPIClient();
                                }
                                mMap.setMyLocationEnabled(true);
                            }
                        }
                        else {
                            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Init Google Play Services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                buildGoogleAPIClient();
                mMap.setMyLocationEnabled(true);
            }

        }
            else {
                buildGoogleAPIClient();
                mMap.setMyLocationEnabled(true);

            }
    }

    private synchronized void buildGoogleAPIClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }

        }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation= location;
                if(mMarker != null) {
                    mMarker.remove();
                }

                latitude=location.getLatitude();
                longitude=location.getLongitude();

                LatLng latlng=new LatLng(latitude,longitude);
                MarkerOptions markerOptions=new MarkerOptions()
                        .position(latlng)
                        .title("Your Position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMarker= mMap.addMarker(markerOptions);

            //Move Camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            if(mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            }
    }
        }
