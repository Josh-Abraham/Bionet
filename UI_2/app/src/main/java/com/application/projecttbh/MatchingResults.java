package com.application.projecttbh;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import org.json.JSONObject;

public class MatchingResults extends Activity {

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_results);
        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MatchingProperties.getInstance().resetInstance();
                Intent intent = new Intent(MatchingResults.this, AgentHome.class); // Call a secondary view
                startActivity(intent);
            }
        });


        JSONObject resp = MatchingProperties.getInstance().getMatchingData();
        TextView matchingTag, faceTag, fpTag0, fpTag1, irisTag0, irisTag1;
        matchingTag = findViewById(R.id.matching);
        try {
            boolean verified = true;
            if (MatchingProperties.getInstance().isEnableFace()) {
                verified = verified && Boolean.parseBoolean(resp.getString("face"));

                faceTag = findViewById(R.id.face);
                if (Boolean.parseBoolean(resp.getString("face"))) {
                    faceTag.setText("Facial Scan: Valid");
                    faceTag.setTextColor(Color.rgb(0, 200, 0));

                } else {
                    faceTag.setText("Facial Scan: Invalid");
                    faceTag.setTextColor(Color.rgb(200, 0, 0));
                }
                faceTag.setHeight(60);
                faceTag.requestLayout();
                faceTag.animate().alpha(1).setDuration(1000);
            }

            if (MatchingProperties.getInstance().isEnableFP()) {
                if (MatchingProperties.getInstance().getFpOptions()[0]) {
                    verified = verified && MatchingProperties.getInstance().getFpMatches()[0];
                    fpTag0 = findViewById(R.id.fp_tag_0);
                    if (MatchingProperties.getInstance().getFpMatches()[0]) {
                        fpTag0.setText("Left Thumb\nScan: Valid");
                        fpTag0.setTextColor(Color.rgb(0, 200, 0));
                    } else {
                        fpTag0.setText("Left Thumb\nScan: Invalid");
                        fpTag0.setTextColor(Color.rgb(200, 0, 0));
                    }
                    fpTag0.setHeight(60);
                    fpTag0.requestLayout();
                    fpTag0.animate().alpha(1).setDuration(1000);
                }
                if (MatchingProperties.getInstance().getFpOptions()[1]) {
                    verified = verified && MatchingProperties.getInstance().getFpMatches()[1];
                    fpTag1 = findViewById(R.id.fp_tag_1);
                    if (MatchingProperties.getInstance().getFpMatches()[1]) {
                        fpTag1.setText("Right Thumb\nScan: Valid");
                        fpTag1.setTextColor(Color.rgb(0, 200, 0));
                    } else {
                        fpTag1.setText("Right Thumb\nScan: Invalid");
                        fpTag1.setTextColor(Color.rgb(200, 0, 0));
                    }
                    fpTag1.setHeight(60);
                    fpTag1.requestLayout();
                    fpTag1.animate().alpha(1).setDuration(1000);
                }
            }

            if (MatchingProperties.getInstance().isEnableIris()) {
                if (MatchingProperties.getInstance().getIrisOptions()[0]) {
                    irisTag0 = findViewById(R.id.iris_tag_0);
                    verified = verified && Boolean.parseBoolean(resp.getString("iris_0"));
                    if (Boolean.parseBoolean(resp.getString("iris_0"))) {
                        irisTag0.setText("Right Iris\nScan: Valid");
                        irisTag0.setTextColor(Color.rgb(0, 200, 0));
                    } else {
                        irisTag0.setText("Right Iris\nScan: Invalid");
                        irisTag0.setTextColor(Color.rgb(200, 0, 0));
                    }
                    irisTag0.setHeight(60);
                    irisTag0.requestLayout();
                    irisTag0.animate().alpha(1).setDuration(1000);
                }
                if (MatchingProperties.getInstance().getIrisOptions()[1]) {
                    irisTag1 = findViewById(R.id.iris_tag_1);
                    verified = verified && Boolean.parseBoolean(resp.getString("iris_1"));
                    if (Boolean.parseBoolean(resp.getString("iris_1"))) {
                        irisTag1.setText("Right Iris\nScan: Valid");
                        irisTag1.setTextColor(Color.rgb(0, 200, 0));
                    } else {
                        irisTag1.setText("Right Iris\nScan: Invalid");
                        irisTag1.setTextColor(Color.rgb(200, 0, 0));
                    }
                    irisTag1.setHeight(60);
                    irisTag1.requestLayout();
                    irisTag1.animate().alpha(1).setDuration(1000);
                }
            }


            if (verified) {
                matchingTag.setText("Valid Match");
                matchingTag.setTextColor(Color.rgb(0, 200, 0));
            } else {
                matchingTag.setText("Invalid Match");
                matchingTag.setTextColor(Color.rgb(200, 0, 0));
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), String.format("%s\n", e), Toast.LENGTH_LONG).show();
        }

    }

}