package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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


public class MatchingRun extends Activity {
    Button uploadBtn;

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

    private JSONObject createJSON() throws JSONException {
        // MatchingStart data
        // TODO: Encrypt
        JSONObject matchingData = new JSONObject();
        matchingData.put("PassportNumber", MatchingProperties.getInstance().getPassportId());
        matchingData.put("EnableFacial", MatchingProperties.getInstance().isEnableFace());
        matchingData.put("FingerprintS3", MatchingProperties.getInstance().getFpS3());
        matchingData.put("IrisS3", MatchingProperties.getInstance().getIrisS3());
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
                            String dataSent = response.getString("result");
                            System.out.println(dataSent);
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
