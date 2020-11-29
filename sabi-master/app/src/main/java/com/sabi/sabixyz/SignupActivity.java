package com.sabi.sabixyz;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.sabi.sabixyz.helper.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private EditText useremail, userphone,userpassword,confirmpassword, firstname, lastname,school, faculty, department;
    private TextView returnmessage, signup_success_message, confirmation_code_link, signin_link, privacy_policy_link;
    private Button btn_signup, btn_signup_success_continue;
    private LinearLayout user_signup_form, layout_success_message, student_fields;
    private RadioButton Im_a_student, Im_not_a_studdent;
    private CheckBox checkbox_agree_policy;
    String password, confirm_password, user_email, str_firstname, str_lastname, str_school, str_faculty, str_deparment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_activity);
        firstname = findViewById(R.id.edt_user_fname);
        lastname = findViewById(R.id.edt_user_lname);
        useremail = findViewById(R.id.edt_user_email);
        userphone = findViewById(R.id.edt_user_phone);
        school = findViewById(R.id.edt_school);
        faculty = findViewById(R.id.edt_faculty);
        department = findViewById(R.id.edt_department);
        userpassword = findViewById(R.id.edt_user_password);
        confirmpassword = findViewById(R.id.edt_user_confirm_password);
        returnmessage = findViewById(R.id.tv_return_msg);
        checkbox_agree_policy = findViewById(R.id.checkbox_privacy_policy);
        signup_success_message = findViewById(R.id.tv_signup_success_message);
        user_signup_form = findViewById(R.id.linearlayout_user_signup);
        layout_success_message = findViewById(R.id.linearlayout_user_success_message);
        student_fields = findViewById(R.id.linear_layout_student_fields);
        btn_signup = findViewById(R.id.btn_signup);
        signin_link = findViewById(R.id.tv_already_have_account);
        privacy_policy_link = findViewById(R.id.txt_privacy_policy);
        confirmation_code_link = findViewById(R.id.tv_already_confirmation_code);
        btn_signup_success_continue = findViewById(R.id.btn_signup_success_continue);
        Im_a_student = findViewById(R.id.radio_im_a_student);
        Im_not_a_studdent = findViewById(R.id.radio_im_not_a_student);
        Im_a_student.setOnCheckedChangeListener(this);
        Im_not_a_studdent.setOnCheckedChangeListener(this);

        signin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, Login.class);
                i.putExtra("user_email","");
                startActivity(i);
            }
        });
        privacy_policy_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignupActivity.this, WebViewActivity.class);
                i.putExtra("url",Constants.LOCAL_PATH+"policy");
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
                //FragmentManager fragmentManager = getSupportFragmentManager();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                //fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnmessage.setVisibility(View.GONE);
                str_firstname = firstname.getText().toString();
                str_lastname = lastname.getText().toString();
                str_school = school.getText().toString();
                str_faculty = faculty.getText().toString();
                str_deparment = department.getText().toString();
            password = userpassword.getText().toString();
            confirm_password = confirmpassword.getText().toString();
            if(!checkbox_agree_policy.isChecked()){
                Toast.makeText(SignupActivity.this, "You have not agreed to our policy", Toast.LENGTH_SHORT).show();
            }
            else if(firstname.getText().toString().isEmpty()){
                firstname.setError("Please enter First Name");
            }
            else if(lastname.getText().toString().isEmpty()){
                lastname.setError("Please enter Last Name");
            }
            else if((school.getText().toString().isEmpty())&&(Im_a_student.isChecked())){
                school.setError("Please enter Name of School");
            }
            else if((faculty.getText().toString().isEmpty())&&(Im_a_student.isChecked())){
                faculty.setError("Please enter Name of Faculty");
            }
            else if((department.getText().toString().isEmpty())&&(Im_a_student.isChecked())){
                department.setError("Please enter Name of Department");
            }
            else if(useremail.getText().toString().isEmpty()){
                useremail.setError("Please enter email address");
            }
            else if(userphone.getText().toString().isEmpty()){
                userphone.setError("Please enter phone number");
            }

            else if(userpassword.getText().toString().isEmpty()){
                userpassword.setError("Please enter password");
            }
            else if(confirmpassword.getText().toString().isEmpty()){
                confirmpassword .setError("Please confirm password");
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
                map.put("first_name", firstname.getText().toString());
                map.put("last_name", lastname.getText().toString());
                map.put("email", useremail.getText().toString());
                map.put("phone", userphone.getText().toString());
                map.put("password", userpassword.getText().toString());
                map.put("confirm_password", confirmpassword.getText().toString());
                if(Im_a_student.isChecked()){
                    map.put("school", school.getText().toString());
                    map.put("faculty", faculty.getText().toString());
                    map.put("department", department.getText().toString());
                }

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



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean checked = ((RadioButton) buttonView).isChecked();
        switch(buttonView.getId()) {
            case R.id.radio_im_a_student:
                if (checked)
                    student_fields.setVisibility(View.VISIBLE);
                break;
            case R.id.radio_im_not_a_student:
                if (checked)
                    student_fields.setVisibility(View.GONE);
                break;
        }
    }
}
