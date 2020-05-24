package com.inspirado.kuber;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inspirado.kuber.cash.CashRequestDetailsFragment;
import com.inspirado.kuber.domain.AppVersionInfo;
import com.inspirado.kuber.ecom.order.NewOrderFragment0;
import com.inspirado.kuber.ecom.order.NewOrderFragment1;
import com.inspirado.kuber.ecom.order.PaymentFragment;
import com.inspirado.kuber.ecom.store.Membership;
import com.inspirado.kuber.ecom.store.Privilege;
import com.inspirado.kuber.ecom.store.Store;
import com.inspirado.kuber.ecom.store.StoreAccountFragment;
import com.inspirado.kuber.ecom.store.StoreMapFragment;
import com.inspirado.kuber.ecom.store.StorePlanFragment;
import com.inspirado.kuber.ecom.store.inventory.StoreInventoryListFragment;
import com.inspirado.kuber.util.Util;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PaymentResultWithDataListener {
    private RecyclerView mList;
    ArrayList cashRequests = new ArrayList();
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private BroadcastReceiver mMyBroadcastReceiver;
    private static final int RC_SIGN_IN = 123;
    private User user;
    private static String TOKEN;
    private static boolean TOKEN_REGISTERED = false;
    private  Store store;
    //qr code scanner object
    private Fragment cashRequestDetailsFragment;
    private static final int PERMISSION_REQUEST_CODE = 200;
    View parentLayout;
    boolean isMembershipActive = false;
    private Menu menu;
    MenuItem toggleservice;
    Switch actionView;

    public void createSignInIntent() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        this.startActivity(myIntent);
    }

    public void createRegistrationIntent() {
        Intent myIntent = new Intent(this, RegistrationActivity.class);
        this.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent(); //internal screen navigation
        int fragment = intent.getIntExtra("fragment", 0);
        checkUpdate();
        if (!checkPermizons()) {
            requestPermission();
        }
        if (TOKEN == null) {
            TOKEN = FirebaseInstanceId.getInstance().getToken();
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");


        if (json.equalsIgnoreCase("")) {
            createSignInIntent();
        } else {
            user = (new Gson()).fromJson(json, User.class);
            if (user.getStatus() == 5) {
                if (savedInstanceState != null) {
                    //Restore the fragment's instance
                    cashRequestDetailsFragment = getSupportFragmentManager().getFragment(savedInstanceState, "cashRequestDetailsFragment");
                }
                displaySkeleton();
                displaySelectedScreen(fragment);
            } else {
                createRegistrationIntent();
            }
            registerTokenIfRequired(TOKEN, user.getId(), user.getClientCode());
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("21");
        filter.addAction("22");
        filter.addAction("34");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, filter);

    }

    /**** CAMERA PERMISSIONS ******************/

    private boolean checkPermizons() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("Kuber needs camera permissions to scan QR and complete transaction",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**** CAMERA PERMISSIONS ******************/

    protected void displaySkeleton() {
        parentLayout = findViewById(android.R.id.content);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.user_name);
        navUsername.setText(user.getName());

        Menu nav_Menu = navigationView.getMenu();
        if (user.getUserType() == 1) {
            nav_Menu.findItem(R.id.shop).setVisible(true);
        }
        navigationView.setNavigationItemSelectedListener(this);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if ((drawer != null) && (drawer.isDrawerOpen(GravityCompat.START))) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu= menu;
        if (user.getUserType() == 2)
            return false; // if user is individual, dont display the toggle bar
        setStoreStatusToggleBar(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setStoreStatusToggleBar(Menu menu) {
        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        //progressDialog.setMessage("Changing mode ...");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/properties/orgs/" + user.getClientCode() + "?ownerId="+user.getId(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("content");
                            if (jsonArray != null && jsonArray.length() > 0) {
                                store = (new Gson()).fromJson(jsonArray.getString(0), Store.class);
                                Util.updateStoreInSharedPref(getSharedPreferences("pref", 0),new JSONObject(jsonArray.get(0).toString()));
                            }
                            MenuInflater inflater = getMenuInflater();
                            inflater.inflate(R.menu.menu, menu);
                            toggleservice = menu.findItem(R.id.atm_mode);
                            actionView = (Switch) toggleservice.getActionView();
                            actionView.setTextColor(getColor(R.color.textColor)); //red color for displayed text of Switch
                            actionView.setChecked( (store != null) && (store.getOpenClose()==1)&& (!store.getMarketplaceVisibilityEndDate().before(new Date()) ) ? true : false);
                            getSupportActionBar().setBackgroundDrawable((store != null) && (!store.getMarketplaceVisibilityEndDate().before(new Date()) ) && (store.getOpenClose()==1)  ? new ColorDrawable(getColor(R.color.colorPrimary)) : new ColorDrawable(getColor(R.color.atmOff)));
                            actionView.setText((store != null)&& (!store.getMarketplaceVisibilityEndDate().before(new Date()) )  && (store.getOpenClose()==1) ? "Open" : "Closed");

                            actionView.setOnClickListener(new Switch.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (((Switch)view).isChecked()) {
                                        if ( (store!=null )&& !store.getMarketplaceVisibilityEndDate().before(new Date())) {
                                            actionView.setText("Open");
                                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
                                            store.setOpenClose(1);
                                            updateStore();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "Membership inactive. Please recharge.", Snackbar.LENGTH_LONG).show();
                                            actionView.setChecked(false);
                                            store.setOpenClose(0);
                                            getStoreInfo();
                                        }
                                    }else {
                                        actionView.setText("Closed");
                                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.atmOff)));
                                        store.setOpenClose(0);
                                        updateStore();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(findViewById(android.R.id.content), "System Error", Snackbar.LENGTH_LONG).show();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void updateStore() {
        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
    //      progressDialog.setMessage("Changing mode ...");
        JsonObjectRequest jsonObjectRequest = null;
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
            JSONObject postData = new JSONObject(gson.toJson(store, Store.class));

//                 progressDialog.show();
            jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/properties/orgs/"+user.getClientCode(), postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject responseObj) {
                            try {
                                store = (new Gson()).fromJson(responseObj.toString(), Store.class);
                                Util.updateStoreInSharedPref(getSharedPreferences("pref", 0),new JSONObject(responseObj.toString()));
                            } catch (Exception e) {
                                Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        //    progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Snackbar.make(findViewById(android.R.id.content), "Store status not changed", Snackbar.LENGTH_LONG).show();
                            store.setOpenClose(store.getOpenClose()==0?1:0);
                            try {
                                Util.updateStoreInSharedPref(getSharedPreferences("pref", 0), new JSONObject(gson.toJson(store)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                         //   progressDialog.dismiss();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }


    private void getStoreInfo() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/properties/orgs/" + user.getClientCode() + "?ownerId="+user.getId(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("content");
                            if (jsonArray != null && jsonArray.length() > 0) {
                                store = (new Gson()).fromJson(jsonArray.getString(0), Store.class);
                                Util.updateStoreInSharedPref(getSharedPreferences("pref", 0),new JSONObject(jsonArray.get(0).toString()));
                            }
                        } catch (Exception e) {
                            Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(findViewById(android.R.id.content), "System Error", Snackbar.LENGTH_LONG).show();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }
    private void registerTokenIfRequired(final String token, final Long userId, final String clientCode) {
        if ((TOKEN != null) && !TOKEN_REGISTERED) {
            String url = getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/infra" + "/installationinfo?userId=" + userId + "&registrationToken=" + token;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response.length() == 0) {
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("userId", user.getId());
                            postData.put("registrationToken", token);
                            //    postData.put("registrationTime", Util.getCurrentGMTTime(0));
                            postData.put("valid", "true");
                            Log.d("TAG", "sendRegistrationToServer: " + postData.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/infra" + "/installationinfo", postData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("TAG", "onResponse: " + "Token registered successfully with FIrebase");
                                TOKEN_REGISTERED = true;
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                CharSequence text = "System Error";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                toast.show();
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(jsonObjectRequest);
                    }
                    TOKEN_REGISTERED = true;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    CharSequence text = "System Error";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonArrayRequest);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(true);
        item.setEnabled(true);
        displaySelectedScreen(item.getItemId());
        return true;
    }


    private void displaySelectedScreen(int itemId) {
        int clickedMenu = 0;
        if (user.getUserType() == 2) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.shop).setVisible(false);
            nav_Menu.findItem(R.id.membership).setVisible(false);
            nav_Menu.findItem(R.id.inventoryMenuItem).setVisible(false);
            nav_Menu.findItem(R.id.shop_payments).setVisible(false);
            nav_Menu.findItem(R.id.ic_cash_requests).setVisible(false);
            clickedMenu=R.id.og_cash_requests;
        } if (user.getUserType() == 1) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_Menu = navigationView.getMenu();
            clickedMenu=R.id.ic_cash_requests;
        }
        if(itemId !=0) {
            clickedMenu=itemId;
        }
        Fragment fragment = null;
        String fragmentName = "";
        //initializing the fragment object which is selected
        switch (clickedMenu) {
            case R.id.ic_cash_requests:
                fragment = new IncomingRequestListFragment();
                break;
            case R.id.og_cash_requests:
                fragment = new OGCashListFragment();
                break;
            case R.id.profile:
                fragment = new ProfileFragment();
                break;
            case R.id.shop:
                fragment = new StoreMapFragment();
                break;
            case R.id.membership:
                fragment = new StorePlanFragment();
                fragmentName = "membership";
                break;
            case R.id.shop_payments:
                fragment = new StoreAccountFragment();
                break;
            case R.id.inventoryMenuItem:
                fragment = new StoreInventoryListFragment();
                break;
            case R.id.logout:
                logout();
                //  fragment = new Menu3();
                break;
            case 4:
                fragment = new CashRequestDetailsFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, fragmentName).addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        finish();
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
    }


    //////////////////////////////////////////////////////// FORCE UPDATE ///////////////////////////////////////////////////////////
    private void checkUpdate() {
        // get current version
        Activity mainActivity = this;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(getString(R.string.columbus_ms_url) + "/100/default/infra" + "/appversioninfo/latest", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                    int verCode = pInfo.versionCode;
                    AppVersionInfo upgradeInfo = (new Gson()).fromJson(response.toString(), AppVersionInfo.class);
                    if ((upgradeInfo != null) && (verCode < upgradeInfo.getVersion()) && (upgradeInfo.getUpdateCompulsion() == 2)) {
                        Intent upgradeIntent = new Intent(mainActivity, UpgradeActivity.class);
                        upgradeIntent.putExtra("url", upgradeInfo.getUrl());
                        upgradeIntent.putExtra("url1", upgradeInfo.getUrl1());
                        upgradeIntent.putExtra("message", upgradeInfo.getUpdateInfo());
                        mainActivity.startActivity(upgradeIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Main", "Upgrade Response error");
                //silently fail
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        PaymentFragment paymentFragment = (PaymentFragment) getSupportFragmentManager().findFragmentByTag("paymentFragment");
        StorePlanFragment storePlanFragment = (StorePlanFragment) getSupportFragmentManager().findFragmentByTag("membership");
        if (paymentFragment != null && paymentFragment.isVisible()) {
            paymentFragment.onPaymentSuccess(s, paymentData);
        } else if (storePlanFragment != null && storePlanFragment.isVisible()) {
            storePlanFragment.onPaymentSuccess(s, paymentData);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        PaymentFragment paymentFragment = (PaymentFragment) getSupportFragmentManager().findFragmentByTag("paymentFragment");
        if (paymentFragment != null && paymentFragment.isVisible()) {
            paymentFragment.onPaymentError(i, s, paymentData);
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase("21")){
                actionView.setText("Open");
                actionView.setChecked(true);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
                store.setOpenClose(1);
                getStoreInfo();
            }
            if(intent.getAction().equalsIgnoreCase("22")) {
                actionView.setText("Closed");
                actionView.setChecked(false);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.atmOff)));
                store.setOpenClose(0);
                getStoreInfo();
            }
            if(intent.getAction().equalsIgnoreCase("34")) {
                NewOrderFragment0 fragment = new NewOrderFragment0();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                ft.commit();

            }
        }
    };
}
