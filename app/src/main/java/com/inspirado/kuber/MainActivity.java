package com.inspirado.kuber;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.inspirado.kuber.domain.AppVersionInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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
        int fragment = intent.getIntExtra("fragment", 100);
        checkUpdate();

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
                displaySkeleton();
                displaySelectedScreen(R.id.ic_cash_requests);
            } else {
                createRegistrationIntent();
            }
            registerTokenIfRequired(TOKEN, user.getId());
        }
    }


    protected void displaySkeleton() {
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
        //navUsername.setText(user.getDisplayName());

        navUsername.setText(user.getName());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void registerTokenIfRequired(final String token, final Long userId) {
        if ((TOKEN != null) && !TOKEN_REGISTERED) {
            String url = getString(R.string.notification_ms_url) + "/installationinfo?userId=" + userId + "&registrationToken=" + token;
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

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getString(R.string.notification_ms_url) + "/installationinfo", postData, new Response.Listener<JSONObject>() {
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
        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.ic_cash_requests:
                fragment = new ICCashListFragment();
                break;
            case R.id.og_cash_requests:
                fragment = new OGCashListFragment();
                break;
            case R.id.profile:
                fragment = new ProfileFragment();
                break;
/*            case R.id.about:
                fragment = new AboutFragment();
                break;*/
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
            ft.replace(R.id.content_frame, fragment).addToBackStack(null);
            ;
            ;
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(getString(R.string.notification_ms_url) + "/appversioninfo/latest", null, new Response.Listener<JSONObject>() {
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
}
