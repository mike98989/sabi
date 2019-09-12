package com.example.sabixyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sabixyz.helper.Requests;
import com.example.sabixyz.model.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_username, edt_password;
    private Button btn_login;
    private LinearLayout linearLayout_error_message;
    private TextView tv_error_message, signupLink;
    private SharedPreferences sharedPreferences;
    private String user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_email = getIntent().getStringExtra("user_email");
        linearLayout_error_message = findViewById(R.id.linearlayout_error_message);
        tv_error_message = findViewById(R.id.tv_error_message);
        signupLink = findViewById(R.id.tv_sign_up);
        signupLink.setOnClickListener(this);
        edt_username = findViewById(R.id.edt_username);

        ////IF THE EMAIL IS NOT EMPTY, FILL USERNAME FIELD
        if(!user_email.isEmpty()){
            edt_username.setText(user_email);
        }
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_login:
                UserLogin();
            break;
            case R.id.tv_sign_up:
                Intent i = new Intent(Login.this, SignupActivity.class);
                startActivity(i);

        }
    }


    public void UserLogin(){
    if(edt_username.getText().toString().isEmpty()){
      edt_username.setError("Please Enter Username");
    }
    else if(edt_password.getText().toString().isEmpty()){
        edt_password.setError("Password field cannot be empty");
    }else{
        final ProgressDialog pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading... Please wait.");
        pdialog.setCancelable(false);
        pdialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("username", edt_username.getText().toString());
        map.put("password", edt_password.getText().toString());


        Requests.login(map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pdialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //pdialog.dismiss();
                        tv_error_message.setText("Please check your internet connection");
                        linearLayout_error_message.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                pdialog.dismiss();
                String res = Objects.requireNonNull(response.body()).string();
                Log.e("msg", res);
                try {
                    JSONObject jObject =  new JSONObject(res);

                    String strStatus = jObject.optString("status");
                    final String strMessage = jObject.optString("message");
                    final String strData = jObject.optString("data");
                    final String strToken = jObject.optString("token");
                    if(!strStatus.equals("2")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //pdialog.dismiss();
                                tv_error_message.setText(strMessage);
                                linearLayout_error_message.setVisibility(View.VISIBLE);
                            }
                        });
                    }else{
                        Log.e("data", strData);
                        JSONArray array = new JSONArray(strData);
                        JSONObject jsonObject = array.getJSONObject(0);

                        sharedPreferences = getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);
                        Editor editor = sharedPreferences.edit();

                        editor.putString(getString(R.string.user_email),jsonObject.getString("email"));
                        editor.putString(getString(R.string.user_phone),jsonObject.getString("phone"));
                        editor.putString(getString(R.string.full_name),jsonObject.getString("fullname"));
                        editor.putString(getString(R.string.user_token),strToken);
                        editor.commit();

                        Intent i;
                        i = new Intent(Login.this, MainActivity.class);
                        startActivity(i);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, getApplicationContext());
    }


    }
}
