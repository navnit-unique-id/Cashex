package com.inspirado.kuber.cash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.inspirado.kuber.Lender;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Created by Belal on 18/09/16.
 */


public class NewCashRequestFragment1 extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener {
    public static GoogleMap mMap;
    private LinearLayoutManager linearLayoutManager;
    User user;
    Hashtable lenders = new Hashtable();
    private FusedLocationProviderClient mFusedLocationClient;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Geocoder geocoder;
    boolean locationFound = false;
    boolean searching = false;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 4 * 1000;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Hashtable getLenders() {
        return lenders;
    }

    public void setLenders(Hashtable lenders) {
        this.lenders = lenders;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_cash_req_1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        displayLocationSettingsRequest(getContext());
        getActivity().setTitle(R.string.new_cash_request_lenders_title);
        Button nextBtn = (Button) getActivity().findViewById(R.id.regBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Fragment fragment = new NewCashRequestFragment3();
                    ((NewCashRequestFragment3) fragment).setUser(user);
                    ((NewCashRequestFragment3) fragment).setLenders(lenders);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button newCashReq1ViewListBtn = (Button) getActivity().findViewById(R.id.newCashReq1ViewListBtn);
        newCashReq1ViewListBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Fragment fragment = new NewCashRequestFragment2();
                    ((NewCashRequestFragment2) fragment).setUser(user);
                    ((NewCashRequestFragment2) fragment).setLenders(lenders);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                    ft.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.setOnMarkerDragListener(this);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerDragListener(this);
        }
        mMap.setOnMarkerClickListener(this);
        initiliazeMarker();
    }

    private void initiliazeMarker() {
      /*  if ((user.getLat() != 0) && (user.getLng() != 0)) {
            LatLng latLng = new LatLng(user.getLat(), user.getLng());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            locationFound = true;
        } else {*/
            searching = true;
            LatLng latLng = new LatLng(user.getLat(), user.getLng());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

            handler.postDelayed(runnable = new Runnable() {
                public void run() {
                    if(mGoogleApiClient!=null)mGoogleApiClient.connect();
                    getActivity().setTitle(R.string.registration3_progress_title);
                    int zoom = 7 + (new Random()).nextInt(4);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 5000, null);
                    handler.postDelayed(runnable, delay);
                }
            }, delay);
      //  }
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng position = marker.getPosition();

        Log.d(getClass().getSimpleName(), String.format("Drag from %f:%f",
                position.latitude,
                position.longitude));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng position = marker.getPosition();

        Log.d(getClass().getSimpleName(),
                String.format("Dragging to %f:%f", position.latitude,
                        position.longitude));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(getActivity()!=null)getActivity().setTitle(R.string.new_cash_request_lenders_title);
                    handler.removeCallbacks(runnable); //stop handler when activity not visible
                    searching = false;
                    if (locationFound){
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                        return;
                    }
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
/*                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
                    markerOptions.draggable(true);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);*/
                    //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(9000)
                            .strokeWidth(1)
                            .strokeColor(Color.GRAY)
                            .fillColor(Color.argb(128, 220, 220, 220)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11), 8000, null);
                    user.setLat(location.getLatitude());
                    user.setLng(location.getLongitude());
                    locationFound = true;
                    if(lenders.isEmpty()){
                        getNearbyLenders(location.getLatitude(), location.getLongitude());
                    }else{
                        displayLenderOnMap();
                    }
                }
            });
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Map disconnected !!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Connection to map failed !!", Snackbar.LENGTH_LONG).show();
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMarkerDragListener(this);
                } else {
                    Snackbar.make(getView(), "permission denied", Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), 1);
                            buildGoogleApiClient();
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void getNearbyLenders(Double lat, Double lng){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Finding lenders nearby...");
        progressDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url)+"/100/"+clientCode+"/cashrequest" + "/users/" + user.getId() + "/nearbylenders?lat="+lat+"&lng="+lng , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Lender lender = (new Gson()).fromJson(jsonObject.toString(), Lender.class);
                        lenders.put(lender.getId(),lender);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                displayLenderOnMap();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String UIMessage = "Error. Please try after some time";
                if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                    UIMessage = "Unable to connect to internet.";
                }
                Snackbar snackbar = Snackbar
                        .make(getView(), UIMessage, Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }


    private void displayLenderOnMap(){
        Set<Long> keys = lenders.keySet();
        for(Long key:keys){
            Lender lender = (Lender) lenders.get(key);
            if ((lender.getLat() != 0) && (lender.getLng() != 0)) {
                LatLng latLng = new LatLng(lender.getLat(), lender.getLng());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(lender.getBusinessName());
                if(lender.isSelected()){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else{
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions).setTag(lender.getId());
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTag()==null) return true;
        Long id = Long.parseLong(""+marker.getTag());
        Lender lender = (Lender)lenders.get(id);
        lender.setSelected(!lender.isSelected());
        marker.showInfoWindow();
        Snackbar snackbar;
        //     lenders.put(id,lender);
        if(lender.isSelected()){
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            snackbar = Snackbar
                    .make(getView(), "Request will be sent to "+lender.getBusinessName() , Snackbar.LENGTH_LONG);
        }else{
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            snackbar = Snackbar
                    .make(getView(), "Request will NOT be sent to "+lender.getBusinessName() , Snackbar.LENGTH_LONG);
        }
        snackbar.show();
        return true;
    }


}

