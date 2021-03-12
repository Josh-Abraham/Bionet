package com.application.projecttbh;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;


public class UploadData extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_data);

        Button uploadBtn = findViewById(R.id.confirm_and_upload);
        // TODO: S3 Storage function
        // TODO: Encrypt all data
        // TODO: Call BE endpoint
    }

    private JSONObject createJSON() throws JSONException {
        // User data
        JSONObject userData = new JSONObject();
        userData.put("Full Name", OnboardData.getInstance().getFullName());
        userData.put("Passport ID", OnboardData.getInstance().getPassportId());
        userData.put("DOB", OnboardData.getInstance().getDob());
        userData.put("Address", OnboardData.getInstance().getFormattedAddress());

        //Biometric S3 Data
        JSONObject biometricData = new JSONObject();
        biometricData.put("FACE", OnboardData.getInstance().getS3_facial_key());

        biometricData.put("FP_LT", OnboardData.getInstance().get_S3_fp_data()[0]);
        biometricData.put("FP_RT", OnboardData.getInstance().get_S3_fp_data()[1]);
        biometricData.put("FP_LI", OnboardData.getInstance().get_S3_fp_data()[2]);
        biometricData.put("FP_RI", OnboardData.getInstance().get_S3_fp_data()[3]);

        biometricData.put("IRIS_L", OnboardData.getInstance().get_S3_iris_data()[0]);
        biometricData.put("IRIS_R", OnboardData.getInstance().get_S3_iris_data()[1]);

        JSONObject data = new JSONObject();
        data.put("User Data", userData);
        data.put("Biometric Data", biometricData);

        return data;
    }
}
