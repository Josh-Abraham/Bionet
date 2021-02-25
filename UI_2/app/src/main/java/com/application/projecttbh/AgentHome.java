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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agent_home);
        welcomeNameTextField = (TextView) findViewById(R.id.welcomeNameTextField);
        String username = AppProperties.getInstance().getUsername();
        welcomeNameTextField.setText(username);

        singleOnboarding = (Button) findViewById(R.id.addSingleButton);

        singleOnboarding.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onboardClick();
            }
        });

        // Reset User Data
        OnboardData.getInstance().resetInstance();
    }

    private void onboardClick() {
        Intent intent = new Intent(AgentHome.this, OnboardingOne.class); // Call a secondary view
        startActivity(intent);
    }
}
