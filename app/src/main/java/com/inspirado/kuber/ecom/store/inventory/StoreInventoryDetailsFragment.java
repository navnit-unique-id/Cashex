package com.inspirado.kuber.ecom.store.inventory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.util.Util;
import com.inspirado.kuber.cash.CashRequest;
import com.inspirado.kuber.ecom.store.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//import android.widget.Toast;

/**
 * Created by Belal on 18/09/16.
 */


public class StoreInventoryDetailsFragment extends Fragment {

    CashRequest cashRequest;
    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter adapter;
    ArrayList cashRequests = new ArrayList();
    User user;
    Store store;
    Inventory inventory;

    ArrayList<String> listSpinner=new ArrayList<String>();
    // to store the city and state in the format : City , State. Eg: New Delhi , India
    ArrayList<String> listAll=new ArrayList<String>();
    // for listing all states
    ArrayList<String> listState=new ArrayList<String>();
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    // access all auto complete text views
    AutoCompleteTextView act;

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_store_inventory_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String userJson = pref.getString("user", "");
        String storeJson = pref.getString("store", "");
        user = (new Gson()).fromJson(userJson, User.class);
        store = (new Gson()).fromJson(storeJson, Store.class);

        callAll();
        setValues();
        getActivity().setTitle(R.string.inventory_details_title);
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
                try {
                    int method;
                    if ( ((inventory!=null)&&(inventory.getId()!=null))){
                        method = Request.Method.PUT;
                    }else{
                        method= Request.Method.POST;
                        inventory = new Inventory();
                    }
                    inventory.setBrand(((AutoCompleteTextView) getActivity().findViewById(R.id.brand)).getText().toString());
                    inventory.setName(((EditText) getActivity().findViewById(R.id.name)).getText().toString());
                    inventory.setDescription(((EditText) getActivity().findViewById(R.id.description)).getText().toString());
                    inventory.setMrp( Double.parseDouble(((EditText) getActivity().findViewById(R.id.mrp)).getText().toString()) );
                    inventory.setPrice(Double.parseDouble(((EditText) getActivity().findViewById(R.id.offerPrice)).getText().toString()));
                    inventory.setQuantity(Integer.parseInt(((EditText) getActivity().findViewById(R.id.quantity)).getText().toString()));
                    inventory.setUom(((AutoCompleteTextView) getActivity().findViewById(R.id.uom)).getText().toString());
                    inventory.setCategory(((AutoCompleteTextView) getActivity().findViewById(R.id.category)).getText().toString());
                    inventory.setLocationId(store.getId());
                    inventory.setStoreId(store.getId());
                    String clientCode = user.getClientCode();
                    inventory.setOrgChain("/"+clientCode);
                    inventory.setStatus(1);
                    Gson gson = new Gson();
                    JSONObject postData = new JSONObject(gson.toJson(inventory));
                    jsonObjectRequest = new JsonObjectRequest(method, getString(R.string.columbus_ms_url) +"/100/"+clientCode+"/inventories/stores/"+ store.getId(), postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                     //   Util.updateStoreInSharedPref(getContext().getSharedPreferences("pref", 0), responseObj);
                                        Fragment fragment = new StoreInventoryListFragment();
                                        FragmentTransaction ft = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                                        ft.commit();
                                    } catch (Exception e) {
                                        Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Snackbar.make(getView(), "System Error", Snackbar.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
            }


            private boolean validate() {
                boolean cancel = false;



                EditText name = (EditText) getActivity().findViewById(R.id.name);
                EditText description = (EditText) getActivity().findViewById(R.id.description);
                AutoCompleteTextView brand = (AutoCompleteTextView) getActivity().findViewById(R.id.brand);
                EditText mrp = (EditText) getActivity().findViewById(R.id.mrp);
                TextView price = (TextView) getActivity().findViewById(R.id.offerPrice);
                EditText uom = (AutoCompleteTextView) getActivity().findViewById(R.id.uom);
                EditText quantity = (EditText) getActivity().findViewById(R.id.quantity);
                EditText category = (AutoCompleteTextView) getActivity().findViewById(R.id.category);


                if (name.getText().toString().equalsIgnoreCase("")) {
                    name.setError(getResources().getString(R.string.registration3_error_blank));
                    cancel = true;
                }
                if (description.getText().toString().equalsIgnoreCase("")) {
                    description.setError(getResources().getString(R.string.registration3_error_blank));
                    cancel = true;
                }
                if (brand.getText().toString().equalsIgnoreCase("")) {
                    brand.setError(getResources().getString(R.string.registration3_error_blank));
                    cancel = true;
                }
                if (quantity.getText().toString().equalsIgnoreCase("")) {
                    quantity.setError(getResources().getString(R.string.registration3_error_blank));
                    cancel = true;
                }
                if (uom.getText().toString().equalsIgnoreCase("")) {
                    uom.setError(getResources().getString(R.string.registration3_error_blank));
                    cancel = true;
                }
                if (price.getText().toString().equalsIgnoreCase("")) {
                    price.setError(getResources().getString(R.string.registration3_error_blank));
                    cancel = true;
                }
                if (mrp.getText().toString().equalsIgnoreCase("")) {
                    mrp.setError(getResources().getString(R.string.registration3_error_blank));
                    cancel = true;
                }
                if (category.getText().toString().equalsIgnoreCase("")) {
                    category.setError(getResources().getString(R.string.registration3_error_blank));
                    cancel = true;
                }
                if ( Double.parseDouble("0"+mrp.getText().toString()) < Double.parseDouble("0"+price.getText().toString())) {
                    price.setError(getResources().getString(R.string.mrp_lessthan_price_error_name_blank));
                    cancel = true;
                }
               return cancel;
            }

        });
    }

    public void setValues(){
        if(inventory==null) return;
        ((AutoCompleteTextView) getActivity().findViewById(R.id.brand)).setText(inventory.getBrand());
        ((EditText) getActivity().findViewById(R.id.name)).setText(inventory.getName());
        ((EditText) getActivity().findViewById(R.id.description)).setText(inventory.getDescription());
        ((EditText) getActivity().findViewById(R.id.mrp)).setText(inventory.getMrp()+"");
        ((EditText) getActivity().findViewById(R.id.offerPrice)).setText(inventory.getPrice()+"");
        ((EditText) getActivity().findViewById(R.id.quantity)).setText(inventory.getQuantity()+"");
        ((EditText) getActivity().findViewById(R.id.category)).setText(inventory.getCategory()+"");

        ((AutoCompleteTextView) getActivity().findViewById(R.id.uom)).setText(inventory.getUom()+"");
    }


    public void callAll()
    {
        obj_list();
        //   addToSpinner();
        //   addToAll();
        //   addCity();
        //   addState();
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

