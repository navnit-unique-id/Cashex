package com.inspirado.kuber;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inspirado.kuber.MainActivity;
import com.inspirado.kuber.PasswordResetActivity;
import com.inspirado.kuber.R;
import com.inspirado.kuber.RegistrationActivity;
import com.inspirado.kuber.User;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private PasswordResetTask mPwdRestTask = null;

    // UI references.
    private AutoCompleteTextView musernameView;
    private EditText mPasswordView;
    private EditText mClientCodeView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        musernameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mClientCodeView=(EditText) findViewById(R.id.clientCode);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    // attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button musernameSignInButton = (Button) findViewById(R.id.username_sign_in_button);
        musernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        TextView forgotPwd = (TextView) findViewById(R.id.forgotPassword);
        forgotPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startPwdReset();
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.signupBtn);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(myIntent);
            }
        });
        mLoginFormView = findViewById(R.id.login_page);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        musernameView.setError(null);
        mPasswordView.setError(null);
        mClientCodeView.setError(null);

        // Store values at the time of the login attempt.
        String username = musernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String clientCode = mClientCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            musernameView.setError(getString(R.string.error_field_required));
            focusView = musernameView;
            cancel = true;
        } else if (!isusernameValid(username)) {
            musernameView.setError(getString(R.string.error_invalid_email));
            focusView = musernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password,clientCode);
            mAuthTask.execute((Void) null);
        }
    }

    private void startPwdReset() {
        EditText  musername = (EditText)findViewById(R.id.username);
        String musernameTxt = musername.getText().toString();
        String mClientCodeTxt = mClientCodeView.getText().toString();
        if (musernameTxt.equalsIgnoreCase("")) {
            musername.setError(getString(R.string.error_field_required));
            musername.requestFocus();
            return;
        }
       // showProgress(true);
        mPwdRestTask = new PasswordResetTask(musernameTxt,mClientCodeTxt);
        mPwdRestTask.execute((Void) null);
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


    private boolean isusernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 1;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {


    }


    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private String mClientCode;
        private User user;

        UserLoginTask(String username, String password, String clientCode) {
            mUsername = username;
            mPassword = password;
            mClientCode=clientCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //  final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = null;
            //  progressDialog.setMessage("Authenticating ...");
            //  progressDialog.show();


            try {
                mClientCode= mClientCode.equalsIgnoreCase("")?"51":mClientCode;
                jsonObjectRequest = new JsonObjectRequest(getString(R.string.columbus_ms_url) + "/100/"+mClientCode+"/cashrequest"+"/users?username=" + URLEncoder.encode(mUsername, "UTF-8") + "&password=" + URLEncoder.encode(mPassword, "UTF-8")+ "&clientCode=" + URLEncoder.encode(mClientCode, "UTF-8"), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("pref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("user", response.toString());
                        user = (new Gson()).fromJson(response.toString(), User.class);
                        editor.commit();
                        int status = user.getStatus();
                        if ((status == 1) || (status == 2) || (status == 3) || (status == 4)) {
                            Intent myIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(myIntent);
                        } else {
                            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(myIntent);
                        }
                        showProgress(false);
                        //   progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String UIMessage = "System Exception";
                        if ((error.networkResponse != null) && (error.networkResponse.statusCode == 401)) {
                            UIMessage = "Invalid Username or Password";
                        }
                        if ((error.networkResponse != null) && (error.networkResponse.statusCode == 403)) {
                            UIMessage = "Account is locked out. Please use Forgot Password link to reset acocunt.";
                        }
                        if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                            UIMessage = "Unable to connect to internet.";
                        }
                        if (error.getClass().toString().contains("com.android.volley.NoConnectionError")) {
                            UIMessage = "Unable to connect to server.";
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), UIMessage, Toast.LENGTH_SHORT);
                        toast.show();
                        showProgress(false);
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            // showProgress(false);
            if (success) {

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public class PasswordResetTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUsername;
        private  String mClientCodeText;

        PasswordResetTask(String username, String clientCode) {
            mUsername = username;
            mClientCodeText=clientCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JsonObjectRequest jsonObjectRequest = null;
                Gson gson = new GsonBuilder().create();
                User user = new User();
                user.setUsername(mUsername);
                mClientCodeText= mClientCodeText.equalsIgnoreCase("")?"51":mClientCodeText;
                user.setClientCode(mClientCodeText);
                user.setStatus(6);
                JSONObject postData = new JSONObject(gson.toJson(user, User.class));
                jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) +"/100/"+mClientCodeText+"/cashrequest"+ "/users", postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent myIntent = new Intent(getApplicationContext(), PasswordResetActivity.class);
                        myIntent.putExtra("username",mUsername);
                        myIntent.putExtra("clientcode",mClientCodeText);
                        startActivity(myIntent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String UIMessage = "System Exception";
                        if ((error.networkResponse != null) && (error.networkResponse.statusCode == 401)) {
                            UIMessage = "Invalid Mobile Number";
                        }
                        if (error.getClass().toString().contains("com.android.volley.TimeoutError")) {
                            UIMessage = "Unable to connect to internet.";
                        }
                        if (error.getClass().toString().contains("com.android.volley.NoConnectionError")) {
                            UIMessage = "Unable to connect to server.";
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), UIMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        20 * 1000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mPwdRestTask = null;
            // showProgress(false);
            if (success) {

            } else {
                //   mPasswordView.setError(getString(R.string.error_incorrect_password));
                //    mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mPwdRestTask = null;
        }

    }
}

