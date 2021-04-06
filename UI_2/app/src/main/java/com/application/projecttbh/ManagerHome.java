package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ManagerHome extends Activity {
    private TextView welcomeNameTextField;
    private Button singleOnboarding;
    private Button addAgentButton;
    private Button removeAgentButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_home);
        welcomeNameTextField = (TextView) findViewById(R.id.welcomeNameTextField);
        String username = AppProperties.getInstance().getUsername();
        welcomeNameTextField.setText(username);

        singleOnboarding = (Button) findViewById(R.id.addSingleButton);
        addAgentButton = (Button) findViewById(R.id.addAgentButton);
        removeAgentButton = (Button) findViewById(R.id.removeAgentData);


        singleOnboarding.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onboardClick();
            }
        });

        addAgentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addAgentClick();
            }
        });

        removeAgentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                removeAgentClick();
            }
        });




        // Reset User Data
        OnboardData.getInstance().resetInstance();
    }

    private void onboardClick() {
        Intent intent = new Intent(ManagerHome.this, OnboardingOne.class); // Call a secondary view
        startActivity(intent);
    }

    private void addAgentClick() {
        Log.i("INFO", "Add Agent Button Pressed");
        Intent intent = new Intent(ManagerHome.this, AddAgent.class); // Call a secondary view
        startActivity(intent);
    }

    private void removeAgentClick() {
        Log.i("INFO", "Remove Agent Button Pressed");
        Intent intent = new Intent(ManagerHome.this, RemoveAgent.class); // Call a secondary view
        startActivity(intent);
    }

}