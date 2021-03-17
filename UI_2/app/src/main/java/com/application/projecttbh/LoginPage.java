package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginPage extends Activity {
    private TextView empIdTextview;
    private TextView pswdTextField;
    private TextView loginError;
    private TextView userTypeTextField;

    private String userType = "TSA Agent";
    private final String TSA_AGENT = "TSA Agent";
    private final String TSA_MANAGER = "Manager";
    private final String SYS_ADMIN = "System Admin";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Set object instances for UI items
        Button loginBtn = findViewById(R.id.login_button);
        Button sysAdminBtn = findViewById(R.id.start_scan);
        Button tsaManagerBtn = findViewById(R.id.tsaManagerBtn);

        empIdTextview = findViewById(R.id.emp_id_onboarding);
        pswdTextField = findViewById(R.id.pswdTextField);
        loginError = findViewById(R.id.loginError);
        userTypeTextField = findViewById(R.id.userTypeTextField);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String username = empIdTextview.getText().toString();
                String password = pswdTextField.getText().toString();
                validateLogin(username, password);
            }
        });

        sysAdminBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (userType.equals(SYS_ADMIN)) {
                    userType = TSA_AGENT;

                } else {
                    userType = SYS_ADMIN;
                }
                userTypeTextField.setText(userType);
            }
        });

        tsaManagerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (userType.equals(TSA_MANAGER)) {
                    userType = TSA_AGENT;

                } else {
                    userType = TSA_MANAGER;
                }
                userTypeTextField.setText(userType);
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void validateLogin(String employee_id, String password) {
        String url = "https://ssx64936mh.execute-api.us-east-2.amazonaws.com/default/loginService";
        // Create the HTTP Request Queue
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("Username", AES.encrypt(employee_id));
            postData.put("Password", AES.encrypt(password));
            postData.put("Role", AES.encrypt(userType));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean loginSuccess = Boolean.valueOf(response.getString("result"));
                            AppProperties.getInstance().setUsername(employee_id);
                            if (loginSuccess || AppProperties.getInstance().getDebugMode()) {

                                loginError.setVisibility(View.INVISIBLE);
                                if (userType.equals(TSA_AGENT)) {
                                    Intent intent = new Intent(LoginPage.this, AgentHome.class); // Call a secondary view
                                    startActivity(intent);
                                }
                                else if (userType.equals(TSA_MANAGER)) {
                                    Intent intent = new Intent(LoginPage.this, ManagerHome.class); // Call a secondary view
                                    startActivity(intent);
                                }

                            } else {
                                loginError.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String test = "ERROR Loading Resource";
                Toast.makeText(getApplicationContext(), String.format("%s\n", test), Toast.LENGTH_LONG).show();
            }
        });
		queue.add(jsonObjectRequest);
    }
}


// For Testing
// Toast.makeText(getApplicationContext(), String.format("%s\n", loginSuccess), Toast.LENGTH_LONG).show();
