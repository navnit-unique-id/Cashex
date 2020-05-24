package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

//import com.inspirado.kuber.tracking.MapsAllActivity;

public class PasswordResetActivity extends AppCompatActivity {
    private LinearLayoutManager linearLayoutManager;
    private static final int RC_SIGN_IN = 123;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        Button nextBtn = (Button) findViewById(R.id.continueBtn);
        ((TextView) findViewById(R.id.otpPhone)).setText(getIntent().getStringExtra("username"));
        user.setUsername(getIntent().getStringExtra("username"));
        user.setClientCode(getIntent().getStringExtra("clientcode"));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!validate()) return;
                final ProgressDialog progressDialog = new ProgressDialog(PasswordResetActivity.this);
                progressDialog.setMessage(getString(R.string.registration2_progressbar_msg));
                progressDialog.show();

                JsonObjectRequest jsonObjectRequest = null;
                try {
                    user.setOtp(((EditText) findViewById(R.id.otp)).getText().toString());
                    user.setStatus(5);
                    user.setMobileNumber(getIntent().getStringExtra("username"));
                    user.setPassword( ( (EditText)findViewById(R.id.password)).getText().toString());
                    Gson gson = new Gson();
                    JSONObject postData = new JSONObject(gson.toJson(user));
                    Log.d("TAG", "putData: " + postData.toString());
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) +"/100/"+user.getClientCode()+"/cashrequest"+ "/users", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(myIntent);
                                    } catch (Exception e) {
                                        Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                    Snackbar.make(v, getString(R.string.msg_pwd_changed), Snackbar.LENGTH_LONG).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if ((error.networkResponse != null) && (error.networkResponse.statusCode == 401)) {
                                        ((EditText) findViewById(R.id.otp)).setError("Incorrect or expired OTP");
                                    }
                                    /*if (error.getClass().toString().contains("com.android.volley.ParseError")) {
                                        Snackbar.make(v, getString(R.string.msg_pwd_changed), Snackbar.LENGTH_LONG).show();
                                        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(myIntent);
                                        progressDialog.dismiss();
                                        Snackbar.make(v, getString(R.string.msg_pwd_changed), Snackbar.LENGTH_LONG).show();
                                        return; //temporary
                                    }*/else {
                                        Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    private boolean validate(){
        View focusView = null;
        boolean result = true;
        EditText  mOTPView = (EditText)findViewById(R.id.otp);
        EditText  mPasswordView = (EditText)findViewById(R.id.password);
        EditText  mPasswordView1 = (EditText)findViewById(R.id.password1);

        String otp = mOTPView.getText().toString();
        String password=  mPasswordView.getText().toString();
        String password1=  mPasswordView1.getText().toString();

        if (password.equalsIgnoreCase("")) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            result=false;
        }
        if (password1.equalsIgnoreCase("")) {
            mPasswordView1.setError(getString(R.string.error_field_required));
            focusView = mPasswordView1;
            result=false;
        }
        if (otp.equalsIgnoreCase("")) {
            mOTPView.setError(getString(R.string.error_field_required));
            focusView = mOTPView;
            result=false;
        }

        if (!password.equalsIgnoreCase(password1)) {
            mPasswordView1.setError(getString(R.string.error_pwd_dont_match));
            focusView = mPasswordView1;
            result=false;
        }

        if (!result) {
              focusView.requestFocus();
        }
        return result;
    }


}
