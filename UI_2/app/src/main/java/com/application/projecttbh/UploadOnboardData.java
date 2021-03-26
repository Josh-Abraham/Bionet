package com.application.projecttbh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mitre.jet.exceptions.EbtsBuildingException;

import java.io.IOException;


public class UploadOnboardData extends Activity {
    TextView confirmField;
    Button uploadBtn, restartBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_onboarding_data);

        uploadBtn = findViewById(R.id.confirm_and_upload);
        uploadBtn = findViewById(R.id.restart);
        confirmField = findViewById(R.id.confirm_fields);
        uploadBtn.setHeight(150);
        uploadBtn.requestLayout();
        restartBtn.setHeight(0);
        restartBtn.requestLayout();



        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    JSONObject userData = this.createJSON();
                    uploadS3Data(userData);
                    submitData(userData);


                } catch (JSONException | EbtsBuildingException | IOException e) {
                    e.printStackTrace();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            private JSONObject createJSON() throws JSONException {
                // User data
                JSONObject userData = new JSONObject();
                userData.put("FirstName", AES.encrypt(OnboardData.getInstance().getFirstName()));
                userData.put("MiddleInitial", AES.encrypt(OnboardData.getInstance().getMiddleInitial()));
                userData.put("LastName", AES.encrypt(OnboardData.getInstance().getLastName()));
                userData.put("PassportNumber", AES.encrypt(OnboardData.getInstance().getPassportId()));
                userData.put("DOB", AES.encrypt(OnboardData.getInstance().getDob()));
                userData.put("StreetAddress", AES.encrypt(OnboardData.getInstance().getStreetAddress()));
                userData.put("City", AES.encrypt(OnboardData.getInstance().getCity()));
                userData.put("Province", AES.encrypt(OnboardData.getInstance().getProvince()));
                userData.put("Country", AES.encrypt(OnboardData.getInstance().getCountry()));
                userData.put("PostalCode", AES.encrypt(OnboardData.getInstance().getPostalCode()));

                //Biometric S3 Data
                userData.put("FACE", AES.encrypt(OnboardData.getInstance().getS3_facial_key()));

                userData.put("FP_LT", AES.encrypt(OnboardData.getInstance().get_S3_fp_data()[0]));
                userData.put("FP_RT", AES.encrypt(OnboardData.getInstance().get_S3_fp_data()[1]));

                userData.put("IRIS_L", AES.encrypt(OnboardData.getInstance().get_S3_iris_data()[0]));
                userData.put("IRIS_R", AES.encrypt(OnboardData.getInstance().get_S3_iris_data()[1]));

                return userData;
            }
        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnboardData.getInstance().resetInstance();
                Intent intent;
                if (AppProperties.getInstance().getBatchMode()) {
                    intent = new Intent(UploadOnboardData.this, OnboardingOne.class); // Call a secondary view
                } else {
                    intent = new Intent(UploadOnboardData.this, AgentHome.class); // Call a secondary view
                }
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadS3Data(JSONObject userData) throws IOException, EbtsBuildingException, JSONException {
        Context context = getApplicationContext();
        EBTSMaker.createRecord(userData, context);
        S3Client.uploadFacialFile(OnboardData.getInstance().getS3_facial_key(), context);
        if (!OnboardData.getInstance().get_S3_fp_data()[0].equals("")) {
            S3Client.uploadBiometric(OnboardData.getInstance().get_S3_fp_data()[0], context, "FP");
        }

        if (!OnboardData.getInstance().get_S3_fp_data()[1].equals("")) {
            S3Client.uploadBiometric(OnboardData.getInstance().get_S3_fp_data()[1], context, "FP");
        }
        if (!OnboardData.getInstance().get_S3_iris_data()[0].equals("")) {
            S3Client.uploadBiometric(OnboardData.getInstance().get_S3_iris_data()[0], context, "IRIS");
        }
        if (!OnboardData.getInstance().get_S3_iris_data()[1].equals("")) {
            S3Client.uploadBiometric(OnboardData.getInstance().get_S3_iris_data()[1], context, "IRIS");
        }
    }


    private void submitData(JSONObject userData) {
        String url = "https://ssx64936mh.execute-api.us-east-2.amazonaws.com/default/addTraveller";
        // Create the HTTP Request Queue
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("UserData", userData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    try {
                        boolean dataSent = Boolean.parseBoolean(response.getString("result"));

                        if (dataSent) {
                            OnboardData.getInstance().resetInstance();
                            Intent intent;
                            if (AppProperties.getInstance().getBatchMode()) {
                                intent = new Intent(UploadOnboardData.this, OnboardingOne.class); // Call a secondary view
                            } else {
                                intent = new Intent(UploadOnboardData.this, AgentHome.class); // Call a secondary view
                            }
                            startActivity(intent);
                        } else {
                            confirmField.setText("Unsuccessful Upload!");
                            confirmField.requestLayout();
                            uploadBtn.setHeight(0);
                            uploadBtn.requestLayout();
                            restartBtn.setHeight(150);
                            restartBtn.requestLayout();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }, error -> {
                    String test = "ERROR Loading Resource";
                    Toast.makeText(getApplicationContext(), String.format("%s\n", test), Toast.LENGTH_LONG).show();
                });
        queue.add(jsonObjectRequest);
    }
}
