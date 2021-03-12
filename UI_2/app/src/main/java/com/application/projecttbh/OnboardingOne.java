package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OnboardingOne extends Activity {
    private TextView passportIdOnboarding;
    private TextView firstNameOnboarding;
    private TextView middleInitialOnboarding;
    private TextView lastNameOnboarding;
    private TextView dobOnboarding;

    private Button continueButton;
    private Boolean passportNumCheck = false;
    private Boolean firstNameCheck = false;
    private Boolean middleInitialCheck = true;  // Not a required param
    private Boolean lastNameCheck = false;
    private Boolean dobCheck = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_one);

        // Add UI items
        passportIdOnboarding = (TextView) findViewById(R.id.passport_id_onboarding);
        firstNameOnboarding = (TextView) findViewById(R.id.first_name_onboarding);
        middleInitialOnboarding = (TextView) findViewById(R.id.middle_initial_onboarding);
        lastNameOnboarding = (TextView) findViewById(R.id.last_name_onboarding);
        dobOnboarding = (TextView) findViewById(R.id.dob_onboarding);

        continueButton = (Button) findViewById(R.id.login_button);

        passportIdOnboarding.addTextChangedListener(new TextValidator(passportIdOnboarding) {
            @Override public void validate(TextView textView, String text) {
                passportNumCheck = text.length() > 0;
                OnboardData.getInstance().setPassportId(text);
                checkContinueButtonEnable();
            }
        });

        firstNameOnboarding.addTextChangedListener(new TextValidator(firstNameOnboarding) {
            @Override public void validate(TextView textView, String text) {
                firstNameCheck = text.length() > 0 && text.matches("[a-zA-Z]+");
                OnboardData.getInstance().setFirstName(text);
                checkContinueButtonEnable();
            }
        });

        middleInitialOnboarding.addTextChangedListener(new TextValidator(middleInitialOnboarding) {
            @Override public void validate(TextView textView, String text) {
                middleInitialCheck = text.matches("[a-zA-Z]+");
                OnboardData.getInstance().setMiddleInitial(text);
                checkContinueButtonEnable();
            }
        });

        lastNameOnboarding.addTextChangedListener(new TextValidator(lastNameOnboarding) {
            @Override public void validate(TextView textView, String text) {
                lastNameCheck = text.length() > 0 && text.matches("[a-zA-Z]+");
                OnboardData.getInstance().setLastName(text);
                checkContinueButtonEnable();
            }
        });

        dobOnboarding.addTextChangedListener(new TextValidator(dobOnboarding) {
            @Override public void validate(TextView textView, String text) {
                dobCheck = text.length() > 0;
                OnboardData.getInstance().setDob(text);
                checkContinueButtonEnable();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onContinueClick();
            }
        });
    }

    private void onContinueClick() {
        Intent intent = new Intent(OnboardingOne.this, OnboardingTwo.class); // Call a secondary view
        startActivity(intent);
    }

    private void checkContinueButtonEnable() {
        continueButton.setEnabled((passportNumCheck && firstNameCheck && middleInitialCheck && lastNameCheck && dobCheck) || AppProperties.getInstance().getDebugMode());
    }
}
