package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginPage extends Activity {
    private Button loginBtn;
    private TextView empIdTextview;
    private TextView pswdTextField;
    private TextView loginError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Set object instances for UI items
        loginBtn = (Button) findViewById(R.id.loginBtn);
        empIdTextview = (TextView) findViewById(R.id.empIdTextview);
        pswdTextField = (TextView) findViewById(R.id.pswdTextField);
        loginError = (TextView) findViewById(R.id.loginError);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String username = empIdTextview.getText().toString();
                String password = pswdTextField.getText().toString();
                validateLogin(username, password);
            }
        });

    }
    private void validateLogin(String employee_id, String password) {
        String url = "https://ssx64936mh.execute-api.us-east-2.amazonaws.com/default/loginService";
        // Create the HTTP Request Queue
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("employee_id", employee_id);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean loginSuccess = Boolean.valueOf(response.getString("result"));
                            if (loginSuccess) {
                                Intent intent = new Intent(LoginPage.this, AgentHome.class); // Call a secondary view
                                startActivity(intent);
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
