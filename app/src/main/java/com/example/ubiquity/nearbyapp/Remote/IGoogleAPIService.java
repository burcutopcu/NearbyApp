package com.example.ubiquity.nearbyapp.Remote;

import com.example.ubiquity.nearbyapp.Model.MyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Ubiquity on 2/19/2018.
 */

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);
}
