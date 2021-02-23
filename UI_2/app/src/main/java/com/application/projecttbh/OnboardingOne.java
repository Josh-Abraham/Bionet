package com.application.projecttbh;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

public class OnboardingOne extends Activity {
    private TextView passportIdOnboarding;
    private TextView firstNameOnboarding;
    private TextView middleInitialOnboarding;
    private TextView lastNameOnboarding;
    private TextView dobOnboarding;
    private TextView addressOnboarding;

    private Button continueButton;
    private Boolean passportNumCheck;
    private Boolean firstNameCheck;

    private Boolean lastNameCheck;
    private Boolean dobCheck;
    private Boolean addressCheck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_one);
        OnboardData userData = new OnboardData();
        // Add UI items
        passportIdOnboarding = (TextView) findViewById(R.id.passport_id_onboarding);
        firstNameOnboarding = (TextView) findViewById(R.id.first_name_onboarding);
        middleInitialOnboarding = (TextView) findViewById(R.id.middle_initial_onboarding);
        lastNameOnboarding = (TextView) findViewById(R.id.last_name_onboarding);
        dobOnboarding = (TextView) findViewById(R.id.dob_onboarding);
        addressOnboarding = (TextView) findViewById(R.id.address_onboarding);
        continueButton = (Button) findViewById(R.id.login_button);

        passportIdOnboarding.addTextChangedListener(new TextValidator(passportIdOnboarding) {
            @Override public void validate(TextView textView, String text) {
                passportNumCheck = text.length() > 0;
                userData.setPassportId(text);
            }
        });

        firstNameOnboarding.addTextChangedListener(new TextValidator(firstNameOnboarding) {
            @Override public void validate(TextView textView, String text) {
                firstNameCheck = text.length() > 0;
                userData.setFirstName(text);
            }
        });

        middleInitialOnboarding.addTextChangedListener(new TextValidator(middleInitialOnboarding) {
            @Override public void validate(TextView textView, String text) {
                userData.setFirstName(text);
            }
        });

        lastNameOnboarding.addTextChangedListener(new TextValidator(lastNameOnboarding) {
            @Override public void validate(TextView textView, String text) {
                lastNameCheck = text.length() > 0;
                userData.setLastName(text);
            }
        });

        dobOnboarding.addTextChangedListener(new TextValidator(dobOnboarding) {
            @Override public void validate(TextView textView, String text) {
                dobCheck = text.length() > 0;
                userData.setDob(text);
            }
        });

        addressOnboarding.addTextChangedListener(new TextValidator(addressOnboarding) {
            @Override public void validate(TextView textView, String text) {
                addressCheck = text.length() > 0;
                userData.setAddress(text);
            }
        });
    }

}
