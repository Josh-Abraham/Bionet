package com.application.projecttbh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class FingerprintScan extends Activity {
    ImageView chevron1;
    ImageView chevron2;
    ImageView chevron3;
    private Button startScan;
    private TextView scanTag;
    private String[] fp_keys = new String[4];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_initial);
        fp_keys[0] = "@string/fp_scan_left_thumb";
        fp_keys[1] = "@string/fp_scan_right_thumb";
        fp_keys[2] = "@string/fp_scan_left_index";
        fp_keys[3] = "@string/fp_scan_left_index";


        chevron1 = findViewById(R.id.chevron1);
        chevron2 = findViewById(R.id.chevron2);
        chevron3 = findViewById(R.id.chevron3);
        startScan = findViewById(R.id.start_scan);
        scanTag = findViewById(R.id.sensorTag);
        int key = AppProperties.getInstance().getFp_seq_num();
        scanTag.setText(fp_keys[key]);
        animateChevron1 (chevron1);
        animateChevron2 (chevron2);
        animateChevron3(chevron3);

        startScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FingerprintScan.this, Scanning.class); // Call a secondary view
                startActivity(intent);
            }
        });

    }

    public void animateChevron1(ImageView view) {
        view.setAlpha(0.9f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(0.9f)
                .setDuration(1250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(0.9f);
                        view.animate()
                                .alpha(0f)
                                .setDuration(2500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        view.setAlpha(0f);
                                        view.animate()
                                                .alpha(0.9f)
                                                .setDuration(2500)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        view.setAlpha(0.9f);
                                                        view.animate()
                                                                .alpha(0.9f)
                                                                .setDuration(1250)
                                                                .setListener(new AnimatorListenerAdapter() {
                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {
                                                                        animateChevron1(view);
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    public void animateChevron2(ImageView view) {
        view.setAlpha(0.9f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(0.9f)
                .setDuration(625)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(0.9f);
                        view.animate()
                                .alpha(0f)
                                .setDuration(2500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        view.setAlpha(0f);
                                        view.animate()
                                                .alpha(0f)
                                                .setDuration(1250)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        view.setAlpha(0f);
                                                        view.animate()
                                                                .alpha(0.9f)
                                                                .setDuration(2500)
                                                                .setListener(new AnimatorListenerAdapter() {
                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {
                                                                        view.setAlpha(0.9f);
                                                                        view.animate()
                                                                                .alpha(0.9f)
                                                                                .setDuration(625)
                                                                                .setListener(new AnimatorListenerAdapter() {
                                                                                    @Override
                                                                                    public void onAnimationEnd(Animator animation) {
                                                                                        animateChevron2(view);
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    public void animateChevron3(ImageView view) {
        view.setAlpha(0.9f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(0f)
                .setDuration(2500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(0f);
                        view.animate()
                                .alpha(0f)
                                .setDuration(2500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        view.setAlpha(0f);
                                        view.animate()
                                                .alpha(0.9f)
                                                .setDuration(2500)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        animateChevron3(view);
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

}
