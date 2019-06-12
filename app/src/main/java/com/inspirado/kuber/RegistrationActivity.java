package com.inspirado.kuber;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;

import com.google.gson.Gson;

//import com.inspirado.kuber.tracking.MapsAllActivity;

public class RegistrationActivity extends AppCompatActivity {
    private LinearLayoutManager linearLayoutManager;
    private static final int RC_SIGN_IN = 123;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        displayFragment();
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
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
             editor.commit();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    private void displayFragment() {
        Fragment fragment = null;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);


        if (json.equalsIgnoreCase("")) {
            fragment = new Registration1Fragment();
        } else { // registration was left midway..
           // user = (new Gson()).fromJson(json, User.class);
            if (user.getStatus() == 1) {
                fragment = new Registration2Fragment();
                ((Registration2Fragment) fragment).setUser(user);
            }
            if (user.getStatus() == 2) {
                fragment = new Registration3Fragment();
                ((Registration3Fragment) fragment).setUser(user);
            }
            if (user.getStatus() == 3) {
                fragment = new Registration3Fragment();
                ((Registration3Fragment) fragment).setUser(user);
            }
            if (user.getStatus() == 4) {
                fragment = new Registration5Fragment();
                ((Registration5Fragment) fragment).setUser(user);
            }
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.registration_frame, fragment);
            ft.commit();
        }
    }

}
