package com.application.projecttbh;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amazonaws.mobile.client.AWSMobileClient;


public class FinishOnboarding extends Activity {
    private Button onSubmitOnboarding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_finish);
        AWSMobileClient.getInstance().initialize(this).execute();

        onSubmitOnboarding = (Button) findViewById(R.id.on_submit_onboarding);

        onSubmitOnboarding.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                String file_loc = OnboardData.getInstance().getDirectory() + "/" + OnboardData.getInstance().getFile();
                S3Client.uploadFile(file_loc, context);
            }
        });
    }
}
