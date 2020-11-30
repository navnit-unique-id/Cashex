package com.inspirado.kuber.ecom.store;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.inspirado.kuber.R;
import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.payment.Ledger;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.app.Activity.RESULT_OK;

public class StoreAccountFragment extends Fragment {

    private LinearLayoutManager linearLayoutManager;
    User user;
    Store store;
    Ledger ledger;
    BankAccount bankAccount;
    private static final int BUFFER_SIZE = 1024 * 2;
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";


    private static final String ROOT_URL = "http://seoforworld.com/api/v1/file-upload.php";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private String filePath;

    List<Attachment> panAttachments;
    List<Attachment> chequeAttachments;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ecom_store_setup_3, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = getContext().getSharedPreferences("pref", 0);
        String json = pref.getString("user", "");
        user = (new Gson()).fromJson(json, User.class);
        getActivity().setTitle(R.string.store_payment_options_title);
        getLedgerDetails();
        getPanAttachments(user.getLedgerId());
        getChequeAttachments(user.getLedgerId());
        requestMultiplePermissions();

        Switch directPaymentCheckBox = (Switch) getActivity().findViewById(R.id.directPaymentCheckBox);
        directPaymentCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Switch)v).isChecked()){
                    getActivity().findViewById(R.id.directPaymentSection).setVisibility(View.VISIBLE);
                }else{
                    getActivity().findViewById(R.id.directPaymentSection).setVisibility(View.GONE);
                }
            }
        });

        Switch plataIntegrationCheckBox = (Switch) getActivity().findViewById(R.id.plataIntegrationCheckBox);
        plataIntegrationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Switch)v).isChecked()){
                    getActivity().findViewById(R.id.kycSection).setVisibility(View.VISIBLE);
                }else{
                    getActivity().findViewById(R.id.kycSection).setVisibility(View.GONE);
                }
            }
        });

        ImageButton uploadChequeBtn = (ImageButton) getActivity().findViewById(R.id.uploadChequeBtn);
        uploadChequeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.putExtra("type", "cheque");
                startActivityForResult(intent, 1);
            }
        });

        ImageButton chequeImgView = (ImageButton) getActivity().findViewById(R.id.chequeImgView);
        chequeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadChequeAttachments(user.getLedgerId());
            }
        });

        ImageButton chequeDeleteBtn = (ImageButton) getActivity().findViewById(R.id.chequeDeleteBtn);
        chequeDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCheque(user.getLedgerId());
            }
        });


        ImageButton uploadPanBtn = (ImageButton) getActivity().findViewById(R.id.uploadPanBtn);
        uploadPanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 2);
            }
        });

        ImageButton panView = (ImageButton) getActivity().findViewById(R.id.panView);
        panView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPanAttachments(user.getLedgerId());
            }
        });

        ImageButton panDeleteBtn = (ImageButton) getActivity().findViewById(R.id.panDeleteBtn);
        panDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePan(user.getLedgerId());
            }
        });


        Button nextBtn = (Button) getActivity().findViewById(R.id.updateBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validate()) {
                    return;
                }
                if(ledger==null) return;
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getContext().getString(R.string.registration3_progressbar_msg));
                progressDialog.show();
                JsonObjectRequest jsonObjectRequest = null;
                try {
                    int method = Request.Method.PUT;
                    if (ledger.getBankAccount() == null) {
                        method = Request.Method.PUT;
                        bankAccount = new BankAccount();
                    } else {
                        bankAccount = ledger.getBankAccount();
                    }
                    bankAccount.setBankAccountNumber((((EditText) getActivity().findViewById(R.id.accountNumber)).getText().toString()));
                    bankAccount.setIfsCode((((EditText) getActivity().findViewById(R.id.ifscode)).getText().toString()));
                    bankAccount.setLedgerId(user.getLedgerId());
                    ledger.setBankAccount(bankAccount);
                    ledger.setPan((((EditText) getActivity().findViewById(R.id.pan)).getText().toString()));
                    ledger.setAcceptsCOD(  ((Switch)getActivity().findViewById(R.id.codCheckBox)).isChecked());
                    ledger.setAcceptsDirectPayment(  ((Switch)getActivity().findViewById(R.id.directPaymentCheckBox)).isChecked());
                    ledger.setAcceptsPaymentViaPlatform(  ((Switch)getActivity().findViewById(R.id.plataIntegrationCheckBox)).isChecked());
                    if(  ((Switch)getActivity().findViewById(R.id.plataIntegrationCheckBox)).isChecked()){
                        ledger.setKycStatus(1);
                    }
                    ledger.setDirectPaymentNotes((((EditText) getActivity().findViewById(R.id.directPaymentInstruction)).getText().toString()));
                    Customer customer = new Customer();
                    customer.setAddress(user.getAddress());
                    customer.setName(user.getName());
                    customer.setEmail(user.getEmail());
                    customer.setPhone(user.getMobileNumber());
                    customer.setStatus(1);
                    customer.setOrgChain("/"+user.getClientCode());
                    customer.setCustomerType(2);
                    ledger.setCustomer(customer);
                    String clientCode = user.getClientCode();
                    Gson gson = new Gson();
                    JSONObject postData = new JSONObject(gson.toJson(ledger));
                    jsonObjectRequest = new JsonObjectRequest(method, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/accounting/ledgers/", postData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject responseObj) {
                                    try {
                                        Fragment fragment = new StorePaymentSetupSuccessFragment();
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
        });
    }


    private boolean validate() {
        boolean cancel = false;
        boolean plataIntegrationChecked = ((Switch) getActivity().findViewById(R.id.plataIntegrationCheckBox)).isChecked();
        boolean directPaymentCheckBox = ((Switch) getActivity().findViewById(R.id.directPaymentCheckBox)).isChecked();
        if( plataIntegrationChecked && ( (ledger.getKycStatus()==0) || (ledger.getKycStatus()==2))) {
            EditText accountNumber = (EditText) getActivity().findViewById(R.id.accountNumber);
            EditText retypeAccountNumber = (EditText) getActivity().findViewById(R.id.retypeAccountNumber);
            EditText ifscode = (EditText) getActivity().findViewById(R.id.ifscode);
            EditText pan = (EditText) getActivity().findViewById(R.id.pan);
            if (accountNumber.getText().toString().equalsIgnoreCase("")) {
                accountNumber.setError(getResources().getString(R.string.bankaccount_error_account_number_blank));
                cancel = true;
            }
            if (retypeAccountNumber.getText().toString().equalsIgnoreCase("")) {
                retypeAccountNumber.setError(getResources().getString(R.string.bankaccount_error_account_number_blank));
                cancel = true;
            }
            if (pan.getText().toString().equalsIgnoreCase("")) {
                pan.setError(getResources().getString(R.string.bankaccount_error_pan_number_blank));
                cancel = true;
            }
            if (ifscode.getText().toString().equalsIgnoreCase("")) {
                ifscode.setError(getResources().getString(R.string.bankaccount_error_ifs_number_blank));
                cancel = true;
            }
            if (!accountNumber.getText().toString().equalsIgnoreCase(retypeAccountNumber.getText().toString())) {
                accountNumber.setError(getResources().getString(R.string.bankaccount_error_account_number_unmatch));
                cancel = true;
            }
        }

        if(directPaymentCheckBox){
            EditText directPaymentInstruction = (EditText) getActivity().findViewById(R.id.directPaymentInstruction);
            if (directPaymentInstruction.getText().toString().equalsIgnoreCase("")) {
                directPaymentInstruction.setError(getResources().getString(R.string.direct_payment_instruction_blank));
                cancel = true;
            }
        }
        return cancel;
    }


    private void deletePan(Long ledgerId) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getContext().getString(R.string.registration3_progressbar_msg));
        progressDialog.show();
        StringRequest jsonArrayRequest = null;
        try {
            Attachment attachment = null;
            if (panAttachments.size() > 0) {
                attachment = (Attachment) panAttachments.get(0);
            } else {
                return;
            }

            String clientCode = user.getClientCode();
            Gson gson = new Gson();
            jsonArrayRequest = new StringRequest(Request.Method.DELETE, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/accounting/attachments/ledgers/" + ledgerId + "/pan/" + attachment.getFileName(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getPanAttachments(user.getLedgerId());
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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void deleteCheque(Long ledgerId) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getContext().getString(R.string.registration3_progressbar_msg));
        progressDialog.show();
        StringRequest jsonArrayRequest = null;
        try {
            Attachment attachment = null;
            if (chequeAttachments.size() > 0) {
                attachment = (Attachment) chequeAttachments.get(0);
            } else {
                progressDialog.dismiss();
                return;
            }

            String clientCode = user.getClientCode();
            Gson gson = new Gson();
            jsonArrayRequest = new StringRequest(Request.Method.DELETE, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/accounting/attachments/ledgers/" + ledgerId + "/cheque/" + attachment.getFileName(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getChequeAttachments(user.getLedgerId());
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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }


    private void getPanAttachments(Long ledgerId) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getContext().getString(R.string.registration3_progressbar_msg));
        progressDialog.show();
        JsonArrayRequest jsonArrayRequest = null;
        try {
            String clientCode = user.getClientCode();
            Gson gson = new Gson();
            jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/accounting/attachments/ledgers/" + ledgerId+"/pan", null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray responseArray) {
                            try {
                                panAttachments = Arrays.asList((new Gson()).fromJson(responseArray.toString(), Attachment[].class));
                                if (panAttachments.size() > 0) {
                                    getActivity().findViewById(R.id.panDeleteBtn).setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.panView).setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.uploadPanBtn).setVisibility(View.INVISIBLE);
                                } else {
                                    getActivity().findViewById(R.id.panDeleteBtn).setVisibility(View.INVISIBLE);
                                    getActivity().findViewById(R.id.panView).setVisibility(View.INVISIBLE);
                                    getActivity().findViewById(R.id.uploadPanBtn).setVisibility(View.VISIBLE);
                                }
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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void getChequeAttachments(Long ledgerId) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getContext().getString(R.string.registration3_progressbar_msg));
        progressDialog.show();
        JsonArrayRequest jsonArrayRequest = null;
        try {
            String clientCode = user.getClientCode();
            Gson gson = new Gson();
            jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/accounting/attachments/ledgers/" + ledgerId+"/cheque", null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray responseArray) {
                            try {
                                chequeAttachments = Arrays.asList((new Gson()).fromJson(responseArray.toString(), Attachment[].class));
                                if (chequeAttachments.size() > 0) {
                                    getActivity().findViewById(R.id.chequeDeleteBtn).setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.chequeImgView).setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.uploadChequeBtn).setVisibility(View.INVISIBLE);
                                } else {
                                    getActivity().findViewById(R.id.chequeDeleteBtn).setVisibility(View.INVISIBLE);
                                    getActivity().findViewById(R.id.chequeImgView).setVisibility(View.INVISIBLE);
                                    getActivity().findViewById(R.id.uploadChequeBtn).setVisibility(View.VISIBLE);
                                }
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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void downloadPanAttachments(Long ledgerId) {
        String clientCode = user.getClientCode();
        Attachment attachment = null;
        if (panAttachments.size() > 0) {
            attachment = (Attachment) panAttachments.get(0);
        } else {
            return;
        }
        String url = getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/accounting/attachments/ledgers/" + ledgerId + "/pan/" + attachment.getFileName();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Pan Copy");
        request.setTitle("Pan Copy");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


    private void downloadChequeAttachments(Long ledgerId) {
        String clientCode = user.getClientCode();
        Attachment attachment = null;
        if (panAttachments.size() > 0) {
            attachment = (Attachment) chequeAttachments.get(0);
        } else {
            return;
        }
        String url = getString(R.string.columbus_ms_url) + "/100/" + clientCode + "/accounting/attachments/ledgers/" + ledgerId + "/cheque/" + attachment.getFileName();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Cheque Copy");
        request.setTitle("Cheque Copy");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void setValues(Ledger ledger) {
        String bankAccount = ledger.getBankAccount()==null?"":ledger.getBankAccount().getBankAccountNumber()+"";
        String ifsc = ledger.getBankAccount()==null?"":ledger.getBankAccount().getIfsCode();
        ((EditText) getActivity().findViewById(R.id.accountNumber)).setText(bankAccount);
        ((EditText) getActivity().findViewById(R.id.ifscode)).setText(ifsc);
        ((EditText) getActivity().findViewById(R.id.pan)).setText(ledger.getPan());
        ((EditText) getActivity().findViewById(R.id.directPaymentInstruction)).setText(ledger.getDirectPaymentNotes());
        ((Switch) getActivity().findViewById(R.id.codCheckBox)).setChecked(ledger.isAcceptsCOD());
        if(ledger.isAcceptsDirectPayment()){
            (getActivity().findViewById(R.id.directPaymentSection)).setVisibility(View.VISIBLE);
        }
        if(ledger.isAcceptsPaymentViaPlatform()){
            ( getActivity().findViewById(R.id.kycSection)).setVisibility(View.VISIBLE);
            if(! ((ledger.getKycStatus()==0) || (ledger.getKycStatus()==2)) ){
                ((EditText) getActivity().findViewById(R.id.accountNumber)).setEnabled(false);
                ((EditText) getActivity().findViewById(R.id.retypeAccountNumber)).setVisibility(View.GONE);
                ((EditText) getActivity().findViewById(R.id.ifscode)).setEnabled(false);
                ((EditText) getActivity().findViewById(R.id.pan)).setEnabled(false);
                (getActivity().findViewById(R.id.chequeDeleteBtn)).setEnabled(false);
                (getActivity().findViewById(R.id.panDeleteBtn)).setEnabled(false);
                (getActivity().findViewById(R.id.chequeDeleteBtn)).setEnabled(false);
                (getActivity().findViewById(R.id.uploadChequeBtn)).setEnabled(false);
                (getActivity().findViewById(R.id.uploadPanBtn)).setEnabled(false);
                (getActivity().findViewById(R.id.panView)).setEnabled(false);
                (getActivity().findViewById(R.id.chequeImgView)).setEnabled(false);
            }
        }
        ((Switch) getActivity().findViewById(R.id.directPaymentCheckBox)).setChecked(ledger.isAcceptsDirectPayment());
        ((Switch) getActivity().findViewById(R.id.plataIntegrationCheckBox)).setChecked(ledger.isAcceptsPaymentViaPlatform());
    }


    private void getLedgerDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getContext().getString(R.string.getting_ledger_details));
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = null;
        try {
            jsonObjectRequest = new JsonObjectRequest(getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/accounting/ledgers/" + user.getLedgerId(), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ledger = (new Gson()).fromJson(response.toString(), Ledger.class);
                    setValues(ledger);
                    progressDialog.hide();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String UIMessage = "System Exception";
                    Toast toast = Toast.makeText(getContext(), UIMessage, Toast.LENGTH_SHORT);
                    toast.show();
                    progressDialog.hide();
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = getFilePathFromURI(getActivity(), uri);
            if(requestCode==1){
                uploadChequePDF(uri);
            }else{
                uploadPanPDF(uri );
            }
        }

    }

    private void uploadPanPDF(Uri uri) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/accounting/attachments/ledgers/"+user.getLedgerId()+"/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        String path = getFilePathFromURI(getActivity(), uri);
        RequestBody requestBody = RequestBody.create(MediaType.parse(getMimeType(uri)), new File(path));
        String displayName = getDisplayName(uri);
        RequestBody filename = RequestBody.create(MediaType.parse(getMimeType(uri)), displayName);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", displayName, requestBody);

        PANInterface getResponse = retrofit.create(PANInterface.class);
        Call<String> call = getResponse.uploadImage(fileToUpload, filename);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                getPanAttachments(user.getLedgerId());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("gttt", call.toString());
            }
        });

    }


    private void uploadChequePDF(Uri uri) {
        String pdfname = String.valueOf(Calendar.getInstance().getTimeInMillis());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.columbus_ms_url) + "/100/" + user.getClientCode() + "/accounting/attachments/ledgers/"+user.getLedgerId()+"/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        String path = getFilePathFromURI(getActivity(), uri);
        RequestBody requestBody = RequestBody.create(MediaType.parse(getMimeType(uri)), new File(path));
        String displayName = getDisplayName(uri);
        RequestBody filename = RequestBody.create(MediaType.parse(getMimeType(uri)), displayName);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", displayName, requestBody);

        ChequeInterface getResponse = retrofit.create(ChequeInterface.class);
        Call<String> call = getResponse.uploadImage(fileToUpload, filename);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                getChequeAttachments(user.getLedgerId());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("gttt", call.toString());
            }
        });

    }

    public String getDisplayName(Uri uri){
        String displayName = null;
        String path = getFilePathFromURI(getActivity(), uri);
        File file = new File(path);
        if (uri.toString().startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (path.startsWith("file://")) {
            displayName = file.getName();
        }
        return displayName;
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        String fileName = getFileName(contentUri);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(wallpaperDirectory + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(getActivity())
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getActivity().getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
}

