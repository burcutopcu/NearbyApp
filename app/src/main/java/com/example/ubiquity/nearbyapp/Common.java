package com.example.ubiquity.nearbyapp;

import com.example.ubiquity.nearbyapp.Remote.IGoogleAPIService;
import com.example.ubiquity.nearbyapp.Remote.RetrofitClient;

/**
 * Created by Ubiquity on 2/19/2018.
 */

public class Common {
    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService()
    {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);

    }

}
