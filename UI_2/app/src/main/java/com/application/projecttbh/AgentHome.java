package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AgentHome extends Activity {
    private TextView welcomeNameTextField;
    private Button singleOnboarding;
    private Button batchModeOnboarding;
    private Button matchingMode;
    private Button clearData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agent_home);
        welcomeNameTextField = findViewById(R.id.welcomeNameTextField);
        String username = AppProperties.getInstance().getUsername();
        welcomeNameTextField.setText(username);

        singleOnboarding = findViewById(R.id.addSingleButton);

        singleOnboarding.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onboardClick(false);
            }
        });

        batchModeOnboarding = findViewById(R.id.batchModeButton);

        batchModeOnboarding.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onboardClick(true);
            }
        });

        matchingMode = findViewById(R.id.matchUserButton);

        matchingMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentHome.this, MatchingForm.class); // Call a secondary view
                startActivity(intent);
            }
        });

        clearData = findViewById(R.id.clearData);
        clearData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentHome.this, FingerprintScanning.class); // Call a secondary view
                startActivity(intent);
            }
        });
        // Reset User Data
        OnboardData.getInstance().resetInstance();
    }

    private void onboardClick(boolean batchMode) {
        AppProperties.getInstance().setBatchMode(batchMode);
        Intent intent = new Intent(AgentHome.this, OnboardingOne.class); // Call a secondary view
        startActivity(intent);
    }
}
