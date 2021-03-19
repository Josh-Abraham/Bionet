package com.application.projecttbh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

public class MatchingForm extends Activity {
    private TextView passportIdMatching;
    private CheckBox lThumb;
    private CheckBox lIndex;
    private CheckBox rThumb;
    private CheckBox rIndex;
    private CheckBox rIris;
    private CheckBox lIris;
    private Boolean passportNumCheck = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_form_one);
        passportIdMatching = findViewById(R.id.passport_id_matching);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch facialSwitch = findViewById(R.id.face_switch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch fpSwitch = findViewById(R.id.fp_switch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch irisSwitch = findViewById(R.id.iris_switch);
        lThumb = findViewById(R.id.l_thumb);
        lIndex = findViewById(R.id.l_index);
        rThumb = findViewById(R.id.r_thumb);
        rIndex = findViewById(R.id.r_index);
        lIris = findViewById(R.id.l_iris);
        rIris = findViewById(R.id.r_iris);
        Button continueButton = findViewById(R.id.continue_button);

        passportIdMatching.addTextChangedListener(new TextValidator(passportIdMatching) {
            @Override public void validate(TextView textView, String text) {
                passportNumCheck = text.length() > 0;
                MatchingProperties.getInstance().setPassportId(text);
                continueButton.setEnabled(passportNumCheck && (MatchingProperties.getInstance().isEnableIris() || MatchingProperties.getInstance().isEnableIris() || MatchingProperties.getInstance().isEnableFace()));
            }
        });

        facialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> MatchingProperties.getInstance().setEnableFace(isChecked));

        fpSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MatchingProperties.getInstance().setEnableFP(isChecked);
            lThumb.setEnabled(isChecked);
            lIndex.setEnabled(isChecked);
            rThumb.setEnabled(isChecked);
            rIndex.setEnabled(isChecked);
        });

        lThumb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableFP()) {
                MatchingProperties.getInstance().updateFpOptions(0, isChecked);
            }
        });

        lIndex.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableFP()) {
                MatchingProperties.getInstance().updateFpOptions(1, isChecked);
            }
        });

        rThumb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableFP()) {
                MatchingProperties.getInstance().updateFpOptions(2, isChecked);
            }
        });

        rIndex.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableFP()) {
                MatchingProperties.getInstance().updateFpOptions(3, isChecked);
            }
        });

        irisSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MatchingProperties.getInstance().setEnableIris(isChecked);
            lIris.setEnabled(isChecked);
            rIris.setEnabled(isChecked);
        });

        lIris.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableIris()) {
                MatchingProperties.getInstance().updateIrisOptions(0, isChecked);
            }
        });

        rIris.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableIris()) {
                MatchingProperties.getInstance().updateIrisOptions(1, isChecked);
            }
        });

        continueButton.setOnClickListener(v -> {
            if (continueButton.isEnabled()) {
                if (MatchingProperties.getInstance().isEnableFace()) {
                    Intent intent = new Intent(MatchingForm.this, MatchingCamera.class); // Call a secondary view
                    startActivity(intent);
                } else if (MatchingProperties.getInstance().isEnableFP()) {
                    for (int i = 0; i < 4; i++) {
                        if (MatchingProperties.getInstance().getFpOptions()[i]) {
                            Intent intent = new Intent(MatchingForm.this, MatchingCamera.class); // Call a secondary view
                            startActivity(intent);
                        }
                    }
                }  else {

                    if (MatchingProperties.getInstance().getIrisOptions()[0]) {
                        Intent intent = new Intent(MatchingForm.this, MatchingCamera.class); // Call a secondary view
                        startActivity(intent);
                    } else if (MatchingProperties.getInstance().getIrisOptions()[1]) {
                        Intent intent = new Intent(MatchingForm.this, MatchingCamera.class); // Call a secondary view
                        startActivity(intent);
                    }

                }

            }
        });

    }


}
