package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MatchingRun extends Activity {
    Button uploadBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_run);
        JSONObject matchingData = null;
        try {
            matchingData = this.createJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        submitData(matchingData);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private JSONObject createJSON() throws JSONException {
        // MatchingStart data
        // TODO: Encrypt
        JSONObject matchingData = new JSONObject();
        MatchingProperties matchingProps = MatchingProperties.getInstance();
        matchingData.put("PassportNumber", AES.encrypt(MatchingProperties.getInstance().getPassportId()));

        matchingData.put("FaceS3", AES.encrypt(matchingProps.getFacialScan()));
        matchingData.put("FingerprintS3_0", AES.encrypt(matchingProps.getFpS3()[0]));
        matchingData.put("FingerprintS3_1", AES.encrypt(matchingProps.getFpS3()[1]));
        matchingData.put("IrisS3_0", AES.encrypt(matchingProps.getIrisS3()[0]));
        matchingData.put("IrisS3_1", AES.encrypt(matchingProps.getIrisS3()[1]));
        return matchingData;
    }

    private void submitData(JSONObject userData) {
        String url = "https://ssx64936mh.execute-api.us-east-2.amazonaws.com/default/runmatching";
        // Create the HTTP Request Queue
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("MatchingData", userData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resp = response.getString("result");
                            Boolean verified = Boolean.valueOf(resp);
                            ImageView gifMatching;
                            TextView matchingTag;
                            gifMatching = findViewById(R.id.gifMatching);
                            gifMatching.getLayoutParams().height = 0;
                            gifMatching.requestLayout();

                            matchingTag = findViewById(R.id.matching);
                            if (verified) {
                                matchingTag.setText("Valid Match");
                            } else {
                                matchingTag.setText("Invalid Match");
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
