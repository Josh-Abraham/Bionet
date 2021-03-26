package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


public class AgentHome extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agent_home);
        TextView welcomeNameTextField = findViewById(R.id.welcomeNameTextField);
        String username = AppProperties.getInstance().getUsername();
        welcomeNameTextField.setText(username);

        Button singleOnboarding = findViewById(R.id.addSingleButton);

        singleOnboarding.setOnClickListener(v -> onboardClick(false));

        Button batchModeOnboarding = findViewById(R.id.batchModeButton);

        batchModeOnboarding.setOnClickListener(v -> onboardClick(true));

        Button matchingMode = findViewById(R.id.matchUserButton);

        matchingMode.setOnClickListener(v -> {
            AppProperties.getInstance().setType("matching");
            Intent intent = new Intent(AgentHome.this, MatchingForm.class); // Call a secondary view
            startActivity(intent);
        });

        Button clearData = findViewById(R.id.clearData);

        // Reset Instance Data
        OnboardData.getInstance().resetInstance();
        MatchingProperties.getInstance().resetInstance();
        AppProperties.getInstance().resetInstance();
    }

    private void onboardClick(boolean batchMode) {
        AppProperties.getInstance().setType("onboarding");
        AppProperties.getInstance().setBatchMode(batchMode);
        Intent intent = new Intent(AgentHome.this, OnboardingOne.class); // Call a secondary view
        startActivity(intent);
    }
}
