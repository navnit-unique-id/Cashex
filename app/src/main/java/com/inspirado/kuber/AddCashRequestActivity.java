package com.inspirado.kuber;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AddCashRequestActivity extends MainActivity  {
    CashRequest cashRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cashRequest = (CashRequest) getIntent().getSerializableExtra("vehicle");
        setContentView(R.layout.activity_device_setting);
        ((TextInputEditText)findViewById(R.id.setting_name)).setText(cashRequest.getLndrTransactionId());
        ((TextInputEditText)findViewById(R.id.setting_driver_name)).setText(cashRequest.getLndrTransactionId());
        ((TextInputEditText)findViewById(R.id.setting_driver_phone)).setText(cashRequest.getLndrTransactionId());
        ((TextInputEditText)findViewById(R.id.setting_imei)).setText(cashRequest.getLndrTransactionId());
        ((TextInputEditText)findViewById(R.id.setting_vehicle_model)).setText(cashRequest.getLndrTransactionId());
        ((TextInputEditText)findViewById(R.id.setting_vehicle_number)).setText(cashRequest.getLndrTransactionId());
        ((TextInputEditText)findViewById(R.id.setting_engine_number)).setText(cashRequest.getLndrTransactionId());
        ((TextInputEditText)findViewById(R.id.setting_sim)).setText(cashRequest.getLndrTransactionId());
        setTitle(cashRequest.getLndrTransactionId());
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         Button submitButton = (Button) findViewById(R.id.setting_save);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                putData();
            }
        });
    }


    private void putData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JSONObject postData = new JSONObject();
        try {
            postData.put("id", cashRequest.getId());
            postData.put("imei", ((TextInputEditText)findViewById(R.id.setting_imei)).getText());
             postData.put("sim", ((TextInputEditText)findViewById(R.id.setting_sim)).getText());
            postData.put("vehicleName", ((TextInputEditText)findViewById(R.id.setting_name)).getText());
            postData.put("vehicleNumber", ((TextInputEditText)findViewById(R.id.setting_vehicle_number)).getText());
            postData.put("vehicleModels", ((TextInputEditText)findViewById(R.id.setting_vehicle_model)).getText());
            postData.put("driverName", ((TextInputEditText)findViewById(R.id.setting_driver_name)).getText());
            postData.put("driverPhone", ((TextInputEditText)findViewById(R.id.setting_driver_phone)).getText());
            postData.put("engineNumber", ((TextInputEditText)findViewById(R.id.setting_engine_number)).getText());
            Log.d("TAG", "putData: "+postData.toString());
         //   new SendDeviceDetails().execute("http://10.0.2.2:8081/rest/devices", postData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.PUT, getString(R.string.columbus_ms_url)+"/rest/devices", postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ((TextInputEditText)findViewById(R.id.setting_name)).setText(getStringFromJson(response ,"vehicleName"));
                ((TextInputEditText)findViewById(R.id.setting_driver_name)).setText(getStringFromJson(response ,"driverName"));
                ((TextInputEditText)findViewById(R.id.setting_driver_phone)).setText(getStringFromJson(response ,"driverPhone"));
                ((TextInputEditText)findViewById(R.id.setting_imei)).setText(getStringFromJson(response ,"imei"));
                ((TextInputEditText)findViewById(R.id.setting_vehicle_model)).setText(getStringFromJson(response ,"vehicleModels"));
                ((TextInputEditText)findViewById(R.id.setting_vehicle_number)).setText(getStringFromJson(response ,"vehicleNumber"));
                ((TextInputEditText)findViewById(R.id.setting_engine_number)).setText(getStringFromJson(response ,"engineNumber"));
                ((TextInputEditText)findViewById(R.id.setting_sim)).setText(getStringFromJson(response ,"sim"));
                CharSequence text = "Vehicle details saved successfully";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
                  progressDialog.dismiss();
                //  serverResp.setText("String Response : "+ response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CharSequence text = "System Error";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private String getStringFromJson(JSONObject jsonObject, String key ){
        String result = null;
        try{
            result  = jsonObject.getString(key);
        }catch (JSONException e){
        }
        return result;
    }


    private class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("TAG", result);
        }
    }
}
