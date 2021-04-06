package com.application.projecttbh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MatchingForm extends Activity {
    private TextView passportIdMatching;
    private CheckBox lThumb, rThumb, rIris, lIris;
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
        rThumb = findViewById(R.id.r_thumb);
        lIris = findViewById(R.id.l_iris);
        rIris = findViewById(R.id.r_iris);
        Button continueButton = findViewById(R.id.continue_button);

        passportIdMatching.addTextChangedListener(new TextValidator(passportIdMatching) {
            @Override public void validate(TextView textView, String text) {
                passportNumCheck = text.length() > 0;
                MatchingProperties.getInstance().setPassportId(text);
                continueButton.setEnabled(checkContinueButton());
            }
        });

        facialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MatchingProperties.getInstance().setEnableFace(isChecked);
            continueButton.setEnabled(checkContinueButton());
        });

        fpSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MatchingProperties.getInstance().setEnableFP(isChecked);
            lThumb.setEnabled(isChecked);
            rThumb.setEnabled(isChecked);
            continueButton.setEnabled(checkContinueButton());
        });

        lThumb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableFP()) {
                MatchingProperties.getInstance().updateFpOptions(0, isChecked);
                continueButton.setEnabled(checkContinueButton());
            }
        });

        rThumb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableFP()) {
                MatchingProperties.getInstance().updateFpOptions(1, isChecked);
                continueButton.setEnabled(checkContinueButton());
            }
        });

        irisSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MatchingProperties.getInstance().setEnableIris(isChecked);
            lIris.setEnabled(isChecked);
            rIris.setEnabled(isChecked);
            continueButton.setEnabled(checkContinueButton());
        });

        lIris.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableIris()) {
                MatchingProperties.getInstance().updateIrisOptions(0, isChecked);
                continueButton.setEnabled(checkContinueButton());
            }
        });

        rIris.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MatchingProperties.getInstance().isEnableIris()) {
                MatchingProperties.getInstance().updateIrisOptions(1, isChecked);
                continueButton.setEnabled(checkContinueButton());
            }
        });

        continueButton.setOnClickListener(v -> {

            if (continueButton.isEnabled()) {
                AppProperties.getInstance().setSeqNum(0);
                setSeq();
                if (MatchingProperties.getInstance().isEnableFace()) {
                    Intent intent = new Intent(MatchingForm.this, MatchingCamera.class); // Call a secondary view
                    startActivity(intent);
                } else if (MatchingProperties.getInstance().isEnableFP()) {
                    // FP SENSOR
                    Intent intent = new Intent(MatchingForm.this, FingerprintScanning.class); // Call a secondary view
                    startActivity(intent);

                }  else {
                    // IRIS SENSOR
                    Intent intent = new Intent(MatchingForm.this, IrisScan.class); // Call a secondary view
                    startActivity(intent);
                }
            }
        });
    }

    private void setSeq() {
        List<Integer> al = new ArrayList<Integer>();
        if(MatchingProperties.getInstance().getFpOptions()[0]) {
            al.add(0);
        }
        if(MatchingProperties.getInstance().getFpOptions()[1]) {
            al.add(1);
        }
        if(MatchingProperties.getInstance().getIrisOptions()[0]) {
            al.add(2);
        }
        if(MatchingProperties.getInstance().getIrisOptions()[1]) {
            al.add(3);
        }
        Integer[] arg = (Integer[]) al.toArray(new Integer[0]);

        MatchingProperties.getInstance().setFullSeq(arg);
    }

    private boolean checkContinueButton() {
        boolean someBiometric = MatchingProperties.getInstance().isEnableIris() || MatchingProperties.getInstance().isEnableIris() || MatchingProperties.getInstance().isEnableFace();
        boolean valid = true;
        if (MatchingProperties.getInstance().isEnableIris()) {
            valid = valid && (MatchingProperties.getInstance().getIrisOptions()[0] || MatchingProperties.getInstance().getIrisOptions()[1]);
        }

        if (MatchingProperties.getInstance().isEnableFP()) {
            valid = valid && (MatchingProperties.getInstance().getFpOptions()[0] || MatchingProperties.getInstance().getFpOptions()[1]);
        }
        return passportNumCheck && valid && someBiometric;
    }
}
