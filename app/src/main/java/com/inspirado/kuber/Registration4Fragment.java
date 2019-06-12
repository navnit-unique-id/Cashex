package com.inspirado.kuber;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
//import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Belal on 18/09/16.
 */


public class Registration4Fragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList cashRequests = new ArrayList();
    User user;

    ArrayList<String> listSpinner=new ArrayList<String>();
    // to store the city and state in the format : City , State. Eg: New Delhi , India
    ArrayList<String> listAll=new ArrayList<String>();
    // for listing all states
    ArrayList<String> listState=new ArrayList<String>();
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    // access all auto complete text views
    AutoCompleteTextView act;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration_4, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callAll();
        setValues(user);
        getActivity().setTitle(R.string.registration3_title);
        Button nextBtn = (Button) getActivity().findViewById(R.id.updateBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validate()) {
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getContext().getString(R.string.registration3_progressbar_msg));
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = null;
                String paymentMode = "";
                try {
                    user.setAddress(((EditText) getActivity().findViewById(R.id.address)).getText().toString());
                    user.setBusinessName(((EditText) getActivity().findViewById(R.id.name)).getText().toString());
                    user.setEmail(((EditText) getActivity().findViewById(R.id.email)).getText().toString());
                    user.setName(((EditText) getActivity().findViewById(R.id.name)).getText().toString());
                    user.setPinCode(((EditText) getActivity().findViewById(R.id.pincode)).getText().toString());
                    user.setState(((AutoCompleteTextView) getActivity().findViewById(R.id.state)).getText().toString());
                    user.setCity(((AutoCompleteTextView) getActivity().findViewById(R.id.city)).getText().toString());
                    user.setReferralCode(((EditText) getActivity().findViewById(R.id.referral)).getText().toString());
                    String selectedUserType =  ((RadioButton)   getActivity().findViewById(   ((RadioGroup)getActivity().findViewById(R.id.radioGroup)).getCheckedRadioButtonId()    ) ).getText().toString();
                    int userType = selectedUserType.equalsIgnoreCase("businessOwner")?1:2;
                    user.setUserType(userType);


                    user.setStatus(4);
                    Gson gson = new Gson();
                    JSONObject postData = new JSONObject(gson.toJson(user));
                    Log.d("TAG", "putData: " + postData.toString());
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url) + "/users", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        //  BeanUtils.copyProperties(user,response);
                                        Fragment fragment = new Registration5Fragment();
                                        ((Registration5Fragment) fragment).setUser((new Gson()).fromJson(responseObj.toString(), User.class));
                                        FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                        //     fragment.getMapAsync(getActivity());
                                        ft.replace(R.id.registration_frame, fragment).addToBackStack(null);;
                                        ft.commit();
                                        Util.updateSharedPref(getContext().getSharedPreferences("pref", 0), responseObj);
                                    } catch (Exception e) {
                                    //    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        Snackbar
                                                .make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                //    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    Snackbar
                                            .make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
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
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
            }


            private boolean validate() {
                boolean cancel = false;
                EditText address = (EditText) getActivity().findViewById(R.id.address);
                EditText name = (EditText) getActivity().findViewById(R.id.name);
                AutoCompleteTextView state = (AutoCompleteTextView) getActivity().findViewById(R.id.state);
                EditText city = (EditText) getActivity().findViewById(R.id.city);
                EditText pincode = (EditText) getActivity().findViewById(R.id.pincode);
                EditText email = (EditText) getActivity().findViewById(R.id.email);

                if (address.getText().toString().equalsIgnoreCase("")) {
                    address.setError(getResources().getString(R.string.registration3_error_address_blank));
                    cancel = true;
                }
                if (name.getText().toString().equalsIgnoreCase("")) {
                    name.setError(getResources().getString(R.string.registration3_error_name_blank));
                    cancel = true;
                }
                if (state.getText().toString().equalsIgnoreCase("")) {
                    state.setError(getResources().getString(R.string.registration3_error_state_blank));
                    cancel = true;
                }
                if (city.getText().toString().equalsIgnoreCase("")) {
                    city.setError(getResources().getString(R.string.registration3_error_city_blank));
                    cancel = true;
                }
                if (pincode.getText().toString().equalsIgnoreCase("")) {
                    pincode.setError(getResources().getString(R.string.registration3_error_pincode_blank));
                    cancel = true;
                }
                if ( (!email.getText().toString().equalsIgnoreCase(""))&& (!email.getText().toString().contains("@"))) {
                    email.setError(getResources().getString(R.string.registration3_error_email_invalid));
                    cancel = true;
                }
                if ((address.getError() != null) || (name.getError() != null)|| (state.getError() != null) || (city.getError() != null)|| (pincode.getError() != null)|| (email.getError() != null)) {
                    cancel = true;
                }
                return cancel;
            }

        });
    }

    public void setValues(User user){
        ((EditText) getActivity().findViewById(R.id.address)).setText(user.getAddress());
        ((AutoCompleteTextView) getActivity().findViewById(R.id.state)).setText(user.getState());
        ((EditText) getActivity().findViewById(R.id.city)).setText(user.getCity());
        ((EditText) getActivity().findViewById(R.id.pincode)).setText(user.getPinCode());
    }


    public void callAll()
    {
        obj_list();
     //   addToSpinner();
     //   addToAll();
        addCity();
        addState();
    }

    // Get the content of cities.json from assets directory and store it as string
    public String getJson()
    {
        String json=null;
        try
        {
            // Opening cities.json file
            InputStream is = getActivity().getAssets().open("cities.json");
            // is there any content in the file
            int size = is.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            is.read(buffer);
            // close the stream --- very important
            is.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return json;
        }
        return json;
    }

    // This add all JSON object's data to the respective lists
    void obj_list()
    {
        // Exceptions are returned by JSONObject when the object cannot be created
        try
        {
            // Convert the string returned to a JSON object
            JSONObject jsonObject=new JSONObject(getJson());
            // Get Json array
            JSONArray array=jsonObject.getJSONArray("array");
            // Navigate through an array item one by one
            for(int i=0;i<array.length();i++)
            {
                // select the particular JSON data
                JSONObject object=array.getJSONObject(i);
                String city=object.getString("name");
                String state=object.getString("state");
                // add to the lists in the specified format
                listSpinner.add(String.valueOf(i+1)+" : "+city+" , "+state);
                listAll.add(city+" , "+state);
                listCity.add(city);
                listState.add(state);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    // Add the data items to the spinner
  /*  void addToSpinner()
    {
        Spinner spinner=(Spinner)getActivity().findViewById(R.id.state);
        // Adapter for spinner
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }*/

    // The first auto complete text view
/*    void addToAll()
    {
      //  act=(AutoCompleteTextView)getActivity().findViewById(R.id.actAll);
      //  adapterSetting(listAll);
    }
*/
    // The second auto complete text view
    void addCity()
    {
        act=(AutoCompleteTextView)getActivity().findViewById(R.id.city);
        adapterSetting(listCity);
    }

    // The third auto complete text view
    void addState()
    {
        Set<String> set = new HashSet<String>(listState);
        act=(AutoCompleteTextView)getActivity().findViewById(R.id.state);
        adapterSetting(new ArrayList(set));
    }

    // setting adapter for auto complete text views
    void adapterSetting(ArrayList arrayList)
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,arrayList);
        act.setAdapter(adapter);
        hideKeyBoard();
    }

    // hide keyboard on selecting a suggestion
    public void hideKeyBoard()
    {
        act.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
    }

}

