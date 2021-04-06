package com.application.projecttbh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
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
        submitData(matchingData);
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
                        ImageView gifMatching;
                        TextView matchingTag, faceTag, fpTag0, fpTag1, irisTag0, irisTag1;
                        gifMatching = findViewById(R.id.gifMatching);
                        gifMatching.getLayoutParams().height = 0;
                        gifMatching.requestLayout();
                        matchingTag = findViewById(R.id.matching);

                        JSONObject resp = response.getJSONObject("result");
                        boolean verified = true;
                        if (MatchingProperties.getInstance().isEnableFace()) {
                            verified = verified && Boolean.parseBoolean(resp.getString("face"));
                            faceTag = findViewById(R.id.face);
                            if (Boolean.parseBoolean(resp.getString("face"))) {
                                faceTag.setText("Facial Scan: Valid");
                            } else {
                                faceTag.setText("Facial Scan: Invalid");
                            }
                            faceTag.setHeight(60);
                            faceTag.requestLayout();
                        }

                        if (MatchingProperties.getInstance().isEnableFP()) {
                            if (MatchingProperties.getInstance().getFpOptions()[0]) {
                                verified = verified && MatchingProperties.getInstance().getFpMatches()[0];
                                fpTag0 = findViewById(R.id.fp_tag_0);
                                if (MatchingProperties.getInstance().getFpMatches()[0]) {
                                    fpTag0.setText("Left Thumb Scan: Valid");
                                } else {
                                    fpTag0.setText("Left Thumb Scan: Invalid");
                                }
                                fpTag0.setHeight(60);
                                fpTag0.requestLayout();
                            }
                            if (MatchingProperties.getInstance().getFpOptions()[1]) {
                                verified = verified && MatchingProperties.getInstance().getFpMatches()[1];
                                fpTag1 = findViewById(R.id.fp_tag_1);
                                if (MatchingProperties.getInstance().getFpMatches()[1]) {
                                    fpTag1.setText("Right Thumb Scan: Valid");
                                } else {
                                    fpTag1.setText("Right Thumb Scan: Invalid");
                                }
                                fpTag1.setHeight(60);
                                fpTag1.requestLayout();
                            }
                        }

                        if (MatchingProperties.getInstance().isEnableIris()) {
                            if (MatchingProperties.getInstance().getIrisOptions()[0]) {
                                irisTag0 = findViewById(R.id.iris_tag_0);
                                verified = verified && Boolean.parseBoolean(resp.getString("iris_0"));
                                if (Boolean.parseBoolean(resp.getString("iris_0"))) {
                                    irisTag0.setText("Left Iris Scan: Valid");
                                } else {
                                    irisTag0.setText("Left Iris Scan: Invalid");
                                }
                                irisTag0.setHeight(60);
                                irisTag0.requestLayout();
                            }
                            if (MatchingProperties.getInstance().getIrisOptions()[1]) {
                                irisTag1 = findViewById(R.id.iris_tag_1);
                                verified = verified && Boolean.parseBoolean(resp.getString("iris_1"));
                                if (Boolean.parseBoolean(resp.getString("iris_1"))) {
                                    irisTag1.setText("Right Iris Scan: Valid");
                                } else {
                                    irisTag1.setText("Right Iris Scan: Invalid");
                                }
                                irisTag1.setHeight(60);
                                irisTag1.requestLayout();
                            }
                        }


                        if (verified) {
                            matchingTag.setText("Valid Match");
                        } else {
                            matchingTag.setText("Invalid Match");
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
