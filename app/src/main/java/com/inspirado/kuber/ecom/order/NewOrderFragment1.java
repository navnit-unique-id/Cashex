package com.inspirado.kuber.ecom.order;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

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
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.store.Store;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Created by Belal on 16/09/16.
 */


public class NewOrderFragment1 extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener {
    public static GoogleMap mMap;
    private LinearLayoutManager linearLayoutManager;
    User user;
    Hashtable stores = new Hashtable();
    Hashtable storesMarker = new Hashtable(); // |2 | marker | //
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
    String filter = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Hashtable getStores() {
        return stores;
    }

    public void setStores(Hashtable stores) {
        this.stores = stores;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_order_new_1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        displayLocationSettingsRequest(getContext());
        getActivity().setTitle(R.string.nearby_stores_title);

        ToggleButton allBtn = (ToggleButton) getActivity().findViewById(R.id.allBtn);
        ToggleButton favBtn = (ToggleButton) getActivity().findViewById(R.id.favBtn);

        allBtn.setChecked(true);
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favBtn.setChecked(false);
                allBtn.setChecked(true);
                filter = null;
                displayLenderOnMap();
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favBtn.setChecked(true);
                allBtn.setChecked(false);
                filter = "fav";
                displayLenderOnMap();
            }
        });

        Button nextBtn = (Button) getActivity().findViewById(R.id.regBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Fragment fragment = new NewOrderFragment3();
                    ((NewOrderFragment3) fragment).setUser(user);
                    Store selectedStore = null;
                    Iterator storeItr = stores.values().iterator();
                    while (storeItr.hasNext()) {
                        Store store = (Store) storeItr.next();
                        if (store.isSelected()) {
                            selectedStore = store;
                        }
                    }
                    if (selectedStore != null) {
                        ((NewOrderFragment3) fragment).setStore(selectedStore);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                        ft.commit();
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please select a store", Snackbar.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button newCashReq1ViewListBtn = (Button) getActivity().findViewById(R.id.newCashReq1ViewListBtn);
        newCashReq1ViewListBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Fragment fragment = new NewOrderFragment2();
                    ((NewOrderFragment2) fragment).setUser(user);
                    ((NewOrderFragment2) fragment).setStores(stores);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        searching = true;
        LatLng latLng = new LatLng(user.getLat(), user.getLng());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                if (mGoogleApiClient != null) mGoogleApiClient.connect();
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
                    if (getActivity() != null) getActivity().setTitle(R.string.nearby_stores_title);
                    handler.removeCallbacks(runnable); //stop handler when activity not visible
                    searching = false;
                    if (locationFound) {
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13), 8000, null);
                    user.setLat(location.getLatitude());
                    user.setLng(location.getLongitude());
                    locationFound = true;
                    if (stores.isEmpty()) {
                        getNearbyStores(location.getLatitude(), location.getLongitude());
                    } else {
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
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

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

    private void getNearbyStores(Double lat, Double lng) {
        if(getContext()==null) return;
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        String clientCode = user.getClientCode();
        progressDialog.setMessage("Finding stores nearby...");
        progressDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/properties/nearby?user=" + user.getId() + "&lat=" + lat + "&lng=" + lng + "&orgChain=51", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Store store = (new Gson()).fromJson(jsonObject.toString(), Store.class);
                        if (!store.getOwnerId().equals(user.getId())) {
                            stores.put(store.getId(), store);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                displayLenderOnMap();
                if(progressDialog!=null) progressDialog.dismiss();
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


    private void displayLenderOnMap() {
        mMap.clear();
        Set<Long> keys = stores.keySet();
        for (Long key : keys) {
            Store store = (Store) stores.get(key);
            if (shouldDisplay(store, this.filter)) {
                if ((store.getLat() != 0) && (store.getLng() != 0)) {
                    LatLng latLng = new LatLng(store.getLat(), store.getLng());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(store.getName());
                    if (store.isSelected()) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(store.getId());
                    marker.showInfoWindow();
                    storesMarker.put(store.getId(),marker);
                }
            }
        }
    }

    private boolean shouldDisplay(Store store, String filter) {
        if (filter == null) return true;
        if ((user==null) || (user!=null && user.getUserPreferences()==null)) return false;
        List prefs = user.getUserPreferences();
        List eligible = new ArrayList();
        for (int i = 0; i < prefs.size(); i++) {
            UserPreference preference = (UserPreference) prefs.get(i);
            if ((preference.getType() == 1)
                    && (store.getId().equals(preference.getTypeId()))
                    && ("favouritestore".equalsIgnoreCase(preference.getAttributeName()))
                    && ("true".equalsIgnoreCase(preference.getAttributValue()))) {
                eligible.add(preference);
            }
            return false;
        };
        int filteredRecordNum = eligible.size();

/*        int filteredRecordNum = user.getUserPreferences().stream().filter(preference -> {
            if ((preference.getType() == 1)
                    && (store.getId().equals(preference.getTypeId()))
                    && ("favouritestore".equalsIgnoreCase(preference.getAttributeName()))
                    && ("true".equalsIgnoreCase(preference.getAttributValue()))) {
                return true;
            }
            return false;
        }).collect(Collectors.toList()).size();*/
        if (filteredRecordNum > 0) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() == null) return true;
        Long clickedId = Long.parseLong("" + marker.getTag());
        Enumeration keys = storesMarker.keys();
        while(keys.hasMoreElements()){
            Long thisStoreId = Long.parseLong(""+keys.nextElement());
            Marker thisMarker = (Marker)storesMarker.get(thisStoreId);
            if(thisStoreId.equals(clickedId)){
                Store store = (Store) stores.get(thisStoreId);
                store.setSelected(!store.isSelected());
                thisMarker.showInfoWindow();
                if (store.isSelected()) {
                    thisMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    thisMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
            }else{
                thisMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Store store = (Store) stores.get(thisStoreId);
                store.setSelected(false);
            }
        }
        return true;
    }
}

