package com.application.projecttbh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MatchingRun extends Activity {

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
        MatchingProperties matchingProps = MatchingProperties.getInstance();
        if (matchingProps.getFacialScan().equals("") && matchingProps.getIrisS3()[0].equals("") && matchingProps.getIrisS3()[1].equals("")) {
            Intent intent = new Intent(MatchingRun.this, MatchingResults.class); // Call a secondary view
            startActivity(intent);
        } else {
            submitData(matchingData);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private JSONObject createJSON() throws JSONException {
        // MatchingStart data
        JSONObject matchingData = new JSONObject();
        MatchingProperties matchingProps = MatchingProperties.getInstance();
        matchingData.put("PassportNumber", AES.encrypt(MatchingProperties.getInstance().getPassportId()));

        matchingData.put("FaceS3", AES.encrypt(matchingProps.getFacialScan()));
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

        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    try {
                        JSONObject resp = response.getJSONObject("result");
                        MatchingProperties.getInstance().setMatchingData(resp);
                        Intent intent = new Intent(MatchingRun.this, MatchingResults.class); // Call a secondary view
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }, error -> {
                    String test = "ERROR Loading Resource";
                    Toast.makeText(getApplicationContext(), String.format("%s\n", error), Toast.LENGTH_LONG).show();
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
