package com.example.ubiquity.nearbyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ubiquity.nearbyapp.Model.PlaceDetail;
import com.example.ubiquity.nearbyapp.Remote.IGoogleAPIService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlace extends AppCompatActivity {

    ImageView photo;
    TextView openingHours, placeAddress, placeName;

    IGoogleAPIService mService;

    PlaceDetail mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_place);

        mService=Common.getGoogleAPIService();

        photo= findViewById(R.id.photo);
        openingHours=findViewById(R.id.placenopenhourtv);
        placeAddress=findViewById(R.id.placeaddresstv);
        placeName=findViewById(R.id.placenametv);

        //All views should be empty
        placeName.setText("");
        placeAddress.setText("");
        openingHours.setText("");

        //For photo
        if(Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length >0) {
            Picasso.with(this)
                    .load(getPhotoPlaces(Common.currentResult.getPhotos()[0].getPhoto_reference(),1000)) //getPhotos is array
                    .placeholder(R.mipmap.noimage)
                    .into(photo);
        }

        //For opening hours
        if(Common.currentResult.getOpening_hours() !=null ) {

            openingHours.setText("Open now: "+ Common.currentResult.getOpening_hours().getOpen_now());
        }

        else {

            openingHours.setVisibility(View.GONE);
        }

        //For fetching address
        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        mPlace=response.body();
                        placeAddress.setText(mPlace.getResult().getFormatted_address());
                        placeName.setText(mPlace.getResult().getName());
                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url= new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid="+place_id);
        url.append("&key="+getResources().getString(R.string.browser_key));
        return url.toString();
    }

    private String getPhotoPlaces(String photo_referance,int maxWidth) {

        StringBuilder url= new StringBuilder("https://maps.googleapis.com/maps/api/place/photos");
        url.append("?maxwidth="+maxWidth);
        url.append("&photoreferance="+photo_referance);
        url.append("&key="+getResources().getString(R.string.browser_key));
        return url.toString();
    }
}
