package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

public class OnboardingTwo extends Activity {
    private TextView streetAddressOnboarding;
    private TextView unitNumberOnboarding;
    private TextView cityOnboarding;
    private TextView provinceOnboarding;
    private CountryCodePicker  countryOnboarding;
    private TextView postalCodeOnboarding;

    private Button continueButton;
    private Boolean streetAddressCheck = false;
    private Boolean cityCheck = false;
    private Boolean provinceCheck = false;
    private Boolean postalCodeCheck = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_two);

        streetAddressOnboarding = (TextView) findViewById(R.id.street_address_onboarding);
        unitNumberOnboarding = (TextView) findViewById(R.id.apt_num_onboarding);
        cityOnboarding = (TextView) findViewById(R.id.city_onboarding);
        provinceOnboarding = (TextView) findViewById(R.id.province_onboarding);
        countryOnboarding= (CountryCodePicker) findViewById(R.id.country_onboarding);
        postalCodeOnboarding = (TextView) findViewById(R.id.postal_code_onboarding);

        continueButton = (Button) findViewById(R.id.continue_button);

        streetAddressOnboarding.addTextChangedListener(new TextValidator(streetAddressOnboarding) {
            @Override public void validate(TextView textView, String text) {
                streetAddressCheck = text.length() > 0;
                OnboardData.getInstance().setStreetAddress(text);
                checkContinueButtonEnable();
            }
        });

        unitNumberOnboarding.addTextChangedListener(new TextValidator(unitNumberOnboarding) {
            @Override public void validate(TextView textView, String text) {
                OnboardData.getInstance().setUnitNumber(text);
            }
        });

        cityOnboarding.addTextChangedListener(new TextValidator(cityOnboarding) {
            @Override public void validate(TextView textView, String text) {
                cityCheck = text.length() > 0 && text.matches("[a-zA-Z]+");
                OnboardData.getInstance().setCity(text);
                checkContinueButtonEnable();
            }
        });

        provinceOnboarding.addTextChangedListener(new TextValidator(provinceOnboarding) {
            @Override public void validate(TextView textView, String text) {
                provinceCheck = text.length() > 0 && text.matches("[a-zA-Z]+");
                OnboardData.getInstance().setProvince(text);
                checkContinueButtonEnable();
            }
        });

        countryOnboarding.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                OnboardData.getInstance().setCountry(countryOnboarding.getSelectedCountryName());
            }
        });

        postalCodeOnboarding.addTextChangedListener(new TextValidator(postalCodeOnboarding) {
            @Override public void validate(TextView textView, String text) {
                postalCodeCheck = text.length() > 0 && text.matches("[a-zA-Z]+");
                OnboardData.getInstance().setPostalCode(text);
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
        Intent intent = new Intent(OnboardingTwo.this, OnboardingCamera.class); // Call a secondary view
        startActivity(intent);
    }

    private void checkContinueButtonEnable() {
        continueButton.setEnabled((cityCheck && provinceCheck && streetAddressCheck && postalCodeCheck) || AppProperties.getInstance().getDebugMode());
    }

}
