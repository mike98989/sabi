package com.example.sabixyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.sabixyz.helper.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {
    private EditText useremail, userphone,userpassword,confirmpassword;
    private TextView returnmessage, signup_success_message, confirmation_code_link, signin_link;
    private Button btn_signup, btn_signup_success_continue;
    private LinearLayout user_signup_form, layout_success_message;
    String password, confirm_password, user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_activity);

        useremail = findViewById(R.id.edt_user_email);
        userphone = findViewById(R.id.edt_user_phone);
        userpassword = findViewById(R.id.edt_user_password);
        confirmpassword = findViewById(R.id.edt_user_confirm_password);
        returnmessage = findViewById(R.id.tv_return_msg);
        signup_success_message = findViewById(R.id.tv_signup_success_message);
        user_signup_form = findViewById(R.id.linearlayout_user_signup);
        layout_success_message = findViewById(R.id.linearlayout_user_success_message);
        btn_signup = findViewById(R.id.btn_signup);
        signin_link = findViewById(R.id.tv_already_have_account);
        confirmation_code_link = findViewById(R.id.tv_already_confirmation_code);
        btn_signup_success_continue = findViewById(R.id.btn_signup_success_continue);

        signin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, Login.class);
                i.putExtra("user_email","");
                startActivity(i);
            }
        });

        confirmation_code_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("email", "");
                SignupConfirmationFragment fragment = new SignupConfirmationFragment();
                fragment.setArguments(b);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });


        btn_signup_success_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("email", useremail.getText().toString());
                SignupConfirmationFragment fragment = new SignupConfirmationFragment();
                fragment.setArguments(b);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnmessage.setVisibility(View.GONE);
            password = userpassword.getText().toString();
            confirm_password = confirmpassword.getText().toString();
            if(useremail.getText().toString().isEmpty()){
                useremail.setError("Please enter email address");
            }
            else if(userphone.getText().toString().isEmpty()){
                userphone.setError("Please enter phone number");
            }
           else if (!password.equals(confirm_password)){
                returnmessage.setText(getString(R.string.not_match_password));
                returnmessage.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this, getString(R.string.not_match_password), Toast.LENGTH_LONG).show();
            }
           else {
                final ProgressDialog pdialog = new ProgressDialog(SignupActivity.this);
                pdialog.setMessage("Loading... Please wait.");
                pdialog.setCancelable(false);
                pdialog.show();

                Map<String, Object> map = new HashMap<>();
                map.put("email", useremail.getText().toString());
                map.put("phone", userphone.getText().toString());
                map.put("password", userpassword.getText().toString());
                map.put("confirm_password", confirmpassword.getText().toString());

                Requests.signup(map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        pdialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //pdialog.dismiss();
                                returnmessage.setText("Please check your internet connection");
                                returnmessage.setVisibility(View.VISIBLE);
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        pdialog.dismiss();
                        String res = Objects.requireNonNull(response.body()).string();
                        Log.e("msg", res);

                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            String strStatus = jsonObject.optString("status");
                            final String strMessage = jsonObject.optString("msg");
                            user_email = jsonObject.optString("email");
                            final String strData = jsonObject.optString("data");

                            if(!strStatus.equals("1")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        returnmessage.setText(strMessage);
                                        returnmessage.setVisibility(View.VISIBLE);
                                    }
                                });


                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        user_signup_form.setVisibility(View.GONE);
                                        signup_success_message.setText(strMessage);
                                        layout_success_message.setVisibility(View.VISIBLE);
                                    }
                                });

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, getApplicationContext());

            }

            }
        });

    }
}
