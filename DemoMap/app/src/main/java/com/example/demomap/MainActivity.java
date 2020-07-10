package com.example.demomap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.demomap.response.PolylineItem;
import com.example.demomap.response.RouteResponse;
import com.example.demomap.response.RoutesItem;
import com.example.demomap.response.StepsItem;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.HWLocation;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.Polyline;
import com.huawei.hms.maps.model.PolylineOptions;
import com.huawei.hms.maps.util.LogM;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapViewDemoActivity";
    private static final int REQUEST_CODE = 1000;
    //Huawei map
    private HuaweiMap hMap;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    LocationCallback mLocationCallback;

    private MapView mMapView;

    private APIInterface apiInterface;

    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    LatLng sourceLatLng;
    LatLng destinationLatLng;
    Polyline alreadyAddedLine;
    List<LatLng> lineLatLngList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogM.d(TAG, "onCreate:hzj");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(this, RUNTIME_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE);
        }

        //get mapview instance
        mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        //get map instance
        mMapView.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = new LocationRequest();
        // Set the location update interval (in milliseconds).
        mLocationRequest.setInterval(10000);
        // Set the weight.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "---onLocationResult--");
                if (locationResult != null) {
                    // Process the location callback result.
                    if(locationResult != null){
                        List<HWLocation> hwLocations =  locationResult.getHWLocationList();
                        List<Location> locations = locationResult.getLocations();

                        if(hwLocations != null && hwLocations.size() > 0){
                            Log.d(TAG, "---onLocationResult--hwLocations---"+hwLocations.size());
                            HWLocation hwLocation = hwLocations.get(0);
                            Log.d(TAG, "---onLocationResult--hwLocation---"+hwLocation.getLatitude() + "<----->"+hwLocation.getLongitude());
                            //Log.d(TAG, "---onLocationResult--hwLocation---");
                            LatLng latLng = new LatLng(hwLocation.getLatitude(), hwLocation.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                            hMap.animateCamera(cameraUpdate);
                            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);


                            sourceLatLng = latLng;
                            addSource(sourceLatLng);
                            //MarkerOptions markerOptions = new MarkerOptions();
                            //markerOptions.position(latLng);
                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_car_red));
                            //hMap.addMarker(markerOptions);
                           //hMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
                            //getDataForRoute();


                        }else{
                            Log.d(TAG, "---onLocationResult--hwLocations not found---");
                        }

                        if(locations != null && locations.size() > 0){
                            Log.d(TAG, "---onLocationResult--locations---"+locations.size());
                        }else{
                            Log.d(TAG, "---onLocationResult--locations not found---");
                        }
                    }
                }
            }
        };

        checkDeviceLocationSettings();

        getLoc();
        //getDataForRoute();
    }
    private boolean isSourceAdded = false;
    private void addSource(LatLng latLng){
        if(!isSourceAdded) {
            hMap.clear();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_car_red));
            hMap.addMarker(markerOptions);
            isSourceAdded = true;
        }
    }
    Marker destinationMarker;
    private void addDestination(LatLng latLng){
        //hMap.clear();
        if(destinationMarker != null) {
            destinationMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_car_red));
        destinationMarker = hMap.addMarker(markerOptions);
    }

    private void getDataForRoute(LatLngData origin, LatLngData destination){
        if(alreadyAddedLine != null){
            alreadyAddedLine.remove();
        }
        apiInterface = DirectionsRetrofit.getClient().create(APIInterface.class);

        //LatLngData origin = new LatLngData(40.9702245, 29.0706332);
        //LatLngData destination  = new LatLngData(41.1106263, 29.0322126);
        DirectionsRequest directionsRequest = new DirectionsRequest(origin, destination);

        Call<RouteResponse> call = apiInterface.getDirectionsWithType("driving", directionsRequest);
        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                Log.d(TAG, "==onResponse=="+response);
                RouteResponse routeResponse = response.body();
                List<RoutesItem> routesItemList = routeResponse.getRoutes();
                if(routesItemList.size() > 0) {
                    List<StepsItem> steps = routesItemList.get(0).getPaths().get(0).getSteps();
                    int stepSize = steps.size();
                    Log.d(TAG,"--steps-->"+steps);
                    lineLatLngList = new ArrayList<>();
                    for(StepsItem stepItem : steps){
                        List<PolylineItem> polylineItems = stepItem.getPolyline();
                        for(PolylineItem polylineItem : polylineItems){
                            LatLng latLng = new LatLng(polylineItem.getLat(), polylineItem.getLng());
                            lineLatLngList.add(latLng);
                        }
                    }

                    Iterable<LatLng> latLngIterable = lineLatLngList;
                    alreadyAddedLine = hMap.addPolyline(new PolylineOptions().addAll(latLngIterable).color(Color.BLUE).width(3));
                    maxStep = lineLatLngList.size();
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Log.d(TAG, "==onFailure==");
            }
        });
    }

    @Override
    public void onMapReady(HuaweiMap map) {
        //get map instance in a callback method
        Log.d(TAG, "onMapReady: ");
        hMap = map;
        hMap.setOnMapClickListener(latLng -> {
            //hMap.clear();

            //MarkerOptions markerOptions = new MarkerOptions();
            //markerOptions.position(latLng);
            //hMap.addMarker(markerOptions);
            destinationLatLng = latLng;
            addDestination(destinationLatLng);

            LatLngData origin = new LatLngData(sourceLatLng.latitude, sourceLatLng.longitude);
            LatLngData destination  = new LatLngData(destinationLatLng.latitude, destinationLatLng.longitude);
            getDataForRoute(origin, destination);

            createVehicle(sourceLatLng.latitude, sourceLatLng.longitude, true);
            mHandler.sendEmptyMessageDelayed(MOVE_VEHICLE, 2000);
        });
    }

    Marker vehicleMarker;
    private void createVehicle(double lat, double lng, boolean isFirstTime){
        if(!isFirstTime){
            vehicleMarker.remove();
        }
        LatLng latLng1 = new LatLng(lat, lng);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(latLng1);
        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_car_red));
        vehicleMarker = hMap.addMarker(markerOptions1);
    }

    private void moveVehicle(){
        //vehicleMarker.remove();
        mHandler.sendEmptyMessageDelayed(MOVE_VEHICLE, 1000);
    }

    private int MOVE_VEHICLE = 1000;
    int step = 0;
    int maxStep = 0; //lineLatLngList.size();

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == MOVE_VEHICLE){
                Log.d(TAG,"---------------MOVE_VEHICLE--------step---"+step+"<-maxStep-->"+maxStep);
                if (step < maxStep) {
                    LatLng temp = lineLatLngList.get(step);
                    createVehicle(temp.latitude, temp.longitude, false);
                    step = step + 10;
                    moveVehicle();
                } else {
                    Log.d(TAG, "reached destination");
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        checkPermission();
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkDeviceLocationSettings() {
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        //mLocationRequest = new LocationRequest();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        // Check the device location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // Initiate location requests when the location settings meet the requirements.
                        fusedLocationProviderClient
                                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Processing when the API call is successful.
                                        Log.d(TAG, "---onSuccess LocationSettingsResponse-");
                                        fusedLocationProviderClient.getLastLocation();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "---onFailure LocationSettingsResponse-");
                        // Device location settings do not meet the requirements.
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    // Call startResolutionForResult to display a pop-up asking the user to enable related permission.
                                    rae.startResolutionForResult(MainActivity.this, 0);
                                } catch (IntentSender.SendIntentException sie) {
                                    //...
                                }
                                break;
                        }
                    }
                });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    private void getLoc() {
        fusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Processing when the API call is successful.
                        Log.d(TAG, "onSuccess location found");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Processing when the API call fails.
                        Log.d(TAG, "onFailure location found");
                    }
                });
    }

}
