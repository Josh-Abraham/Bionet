package com.application.projecttbh;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amazonaws.mobile.client.AWSMobileClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.mitre.jet.exceptions.EbtsBuildingException;

import java.io.IOException;


public class UploadData extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AWSMobileClient.getInstance().initialize(this).execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_data);

        Button uploadBtn = findViewById(R.id.confirm_and_upload);
        // TODO: S3 Storage function
        // TODO: Encrypt all data
        // TODO: Call BE endpoint
        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    JSONObject userData = this.createJSON();

                    Context context = getApplicationContext();
                    EBTSMaker.createRecord(userData, context);
                } catch (JSONException | EbtsBuildingException | IOException e) {
                    e.printStackTrace();
                }
            }

            private JSONObject createJSON() throws JSONException {
                // User data
                JSONObject userData = new JSONObject();
                System.out.println(OnboardData.getInstance().getPassportId());
                userData.put("Full Name", OnboardData.getInstance().getFullName());
                userData.put("Passport ID", OnboardData.getInstance().getPassportId());
                userData.put("DOB", OnboardData.getInstance().getDob());
                userData.put("Address", OnboardData.getInstance().getFormattedAddress());

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
}
