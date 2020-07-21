package com.example.demomap;

import com.example.demomap.response.RouteResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface APIInterface {

    @POST("routeService/{type}")
    Call<RouteResponse> getDirectionsWithType(@Path(value = "type", encoded = true) String type, @Body DirectionsRequest directionRequest);
}
