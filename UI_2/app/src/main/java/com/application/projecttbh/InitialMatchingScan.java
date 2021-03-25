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


public class InitialMatchingScan extends Activity {
    ImageView chevron1;
    ImageView chevron2;
    ImageView chevron3;
    private Button startScan;
    private TextView scanTag;
    private String[] keys = new String[4];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inital_scan);
        keys[0] = getString(R.string.fp_scan_left_thumb);
        keys[1] = getString(R.string.fp_scan_right_thumb);
        keys[2] = getString(R.string.eye_scan_left);
        keys[3] = getString(R.string.eye_scan_right);


        chevron1 = findViewById(R.id.chevron1);
        chevron2 = findViewById(R.id.chevron2);
        chevron3 = findViewById(R.id.chevron3);
        startScan = findViewById(R.id.sys_admin);
        scanTag = findViewById(R.id.sensorTag);
        int key = AppProperties.getInstance().getSeqNum();
        scanTag.setText(keys[key]);
        animateChevron1 (chevron1);
        animateChevron2 (chevron2);
        animateChevron3(chevron3);

        startScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent;// Call a secondary view
                if (key < 2) {
                    intent = new Intent(InitialMatchingScan.this, FingerprintMatching.class);
                } else {
                    intent = new Intent(InitialMatchingScan.this, IrisMatchingScan.class);
                }
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
