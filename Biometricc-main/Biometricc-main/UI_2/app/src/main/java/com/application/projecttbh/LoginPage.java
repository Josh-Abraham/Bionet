package com.application.projecttbh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


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
        empIdTextview = (TextView) findViewById(R.id.newUserName);
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
    private void validateLogin(String username, String password) {
        String url = "https://w57jsrc6wj.execute-api.us-east-2.amazonaws.com/test/getuserinfo?userId=1";
        // Create the HTTP Request Queue
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string
                        Toast.makeText(getApplicationContext(), String.format("%s\n", response), Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String test = "ERROR Loading Resource";
                Toast.makeText(getApplicationContext(), String.format("%s\n", test), Toast.LENGTH_LONG).show();
            }
        });
//		queue.add(jsonObjectRequest);
//		loginError.setVisibility(View.VISIBLE);
      //  Intent intent = new Intent(this, IOIOConnector.class); // Call a secondary view
       // startActivity(intent);
    }
}
