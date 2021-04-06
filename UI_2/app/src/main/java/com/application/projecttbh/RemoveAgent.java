package com.application.projecttbh;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;


public class RemoveAgent extends Activity {

    private TextView userIdView;
    private TextView agentMessage;
    private Button removeAgentButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_agent);

        userIdView = findViewById(R.id.agentIdField);

        agentMessage = findViewById(R.id.addAgentMessage);
        agentMessage.animate().alpha(0);

        userIdView.addTextChangedListener(createAgentTextWatcher);


        removeAgentButton = findViewById(R.id.removeAgentButton);
        removeAgentButton.setEnabled(false);
        removeAgentButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String username = userIdView.getText().toString();
                removeAgent(username);
            }
        });

        // Reset User Data
        OnboardData.getInstance().resetInstance();
    }

    // Text watcher
    private TextWatcher createAgentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            agentMessage.animate().alpha(0);
            String userNameInput = userIdView.getText().toString().trim();
            removeAgentButton.setEnabled(!userNameInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void removeAgent(String employee_id) {
        String url = "https://ssx64936mh.execute-api.us-east-2.amazonaws.com/default/deleteagent";
        // Create the HTTP Request Queue
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("Username", AES.encrypt(employee_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean removeAgentSuccess = Boolean.valueOf(response.getString("result"));
                            Log.i("INFO Add Agent Result", Boolean.toString(removeAgentSuccess));
                            AppProperties.getInstance().setUsername(employee_id);
                            if (removeAgentSuccess || AppProperties.getInstance().getDebugMode()) {
                                agentMessage.setTextColor(Color.rgb(0,200,0));
                                agentMessage.animate().alpha(1).setDuration(1000);
                                agentMessage.setText("Agent Successfully Removed");
                            } else {
                                agentMessage.setTextColor(Color.rgb(200,0,0));
                                agentMessage.animate().alpha(1).setDuration(1000);
                                agentMessage.setText("Error: Agent Doesn't Exist");
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
