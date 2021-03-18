package com.application.projecttbh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mitre.jet.exceptions.EbtsBuildingException;

import java.io.IOException;


public class UploadOnboardData extends Activity {
    TextView confirmField;
    Button uploadBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_onboarding_data);

        uploadBtn = findViewById(R.id.confirm_and_upload);
        confirmField = findViewById(R.id.confirm_fields);



        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    JSONObject userData = this.createJSON();

                    Context context = getApplicationContext();
                    EBTSMaker.createRecord(userData, context);
                    submitData(userData);

                } catch (JSONException | EbtsBuildingException | IOException e) {
                    e.printStackTrace();
                }
            }

            private JSONObject createJSON() throws JSONException {
                // User data
                JSONObject userData = new JSONObject();
                userData.put("FirstName", OnboardData.getInstance().getFirstName());
                userData.put("MiddleInitial", OnboardData.getInstance().getMiddleInitial());
                userData.put("LastName", OnboardData.getInstance().getLastName());
                userData.put("PassportNumber", OnboardData.getInstance().getPassportId());
                userData.put("DOB", OnboardData.getInstance().getDob());
                userData.put("StreetAddress", OnboardData.getInstance().getStreetAddress());
                userData.put("City", OnboardData.getInstance().getCity());
                userData.put("Province", OnboardData.getInstance().getProvince());
                userData.put("Country", OnboardData.getInstance().getCountry());
                userData.put("PostalCode", OnboardData.getInstance().getPostalCode());

                //Biometric S3 Data
                userData.put("FACE", OnboardData.getInstance().getS3_facial_key());

                userData.put("FP_LT", OnboardData.getInstance().get_S3_fp_data()[0]);
                userData.put("FP_RT", OnboardData.getInstance().get_S3_fp_data()[1]);
                userData.put("FP_LI", OnboardData.getInstance().get_S3_fp_data()[2]);
                userData.put("FP_RI", OnboardData.getInstance().get_S3_fp_data()[3]);

                userData.put("IRIS_L", OnboardData.getInstance().get_S3_iris_data()[0]);
                userData.put("IRIS_R", OnboardData.getInstance().get_S3_iris_data()[1]);

                return userData;
            }
        });
    }


    private void submitData(JSONObject userData) {
        String url = "https://ssx64936mh.execute-api.us-east-2.amazonaws.com/default/addTraveller";
        // Create the HTTP Request Queue
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
//            postData.put("Username", AES.encrypt(employee_id));
//            postData.put("Password", AES.encrypt(password));
//            postData.put("Role", AES.encrypt(userType));

            postData.put("UserData", userData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean dataSent = Boolean.valueOf(response.getString("result"));

                            if (dataSent) {
                                OnboardData.getInstance().resetInstance();
                                Intent intent;
                              if (AppProperties.getInstance().getBatchMode()) {
                                  intent = new Intent(UploadOnboardData.this, OnboardingOne.class); // Call a secondary view
                              } else {
                                  intent = new Intent(UploadOnboardData.this, AgentHome.class); // Call a secondary view
                              }
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String test = "ERROR Loading Resource";
                Toast.makeText(getApplicationContext(), String.format("%s\n", test), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObjectRequest);
    }
}
