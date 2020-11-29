package com.sabi.sabixyz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {
private EditText mEmail, edtConfirmEmail, edtConfirmCode, edtNewPassword, edtConfirmPassword;
private Button mSubmitEmail, mSubmitConfirmationCode;
private TextView mReturnMsg, mReturnMsgConfirmation, mIhaveConfCode;
private LinearLayout llSendConfirmation, llConfirmCode;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forgot_password, container, false);
        mEmail = view.findViewById(R.id.edt_user_email);
        edtConfirmEmail = view.findViewById(R.id.edt_confirm_email);
        edtConfirmCode = view.findViewById(R.id.edt_confirmation_code);
        edtNewPassword = view.findViewById(R.id.edt_new_password);
        edtConfirmPassword = view.findViewById(R.id.edt_confirm_password);
        mReturnMsg = view.findViewById(R.id.tv_return_msg);
        mReturnMsgConfirmation = view.findViewById(R.id.tv_return_msg_confirmation);
        mSubmitEmail = view.findViewById(R.id.btn_submit_email_forgot_password);
        mSubmitConfirmationCode = view.findViewById(R.id.btn_confirm_code_submit);
        mIhaveConfCode = view.findViewById(R.id.tv_i_have_conf_code);
        llSendConfirmation = view.findViewById(R.id.linearlayout_send_code);
        llConfirmCode = view.findViewById(R.id.linearlayout_confirm_code);
        mIhaveConfCode.setOnClickListener(this);
        mSubmitEmail.setOnClickListener(this);
        mSubmitConfirmationCode.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_submit_email_forgot_password:
                if(mEmail.getText().toString().isEmpty()){
                    mEmail.setError("Please enter email address");
                    return;
                }else if(mEmail.getText().toString().length()<5){
                    mEmail.setError("Please enter a valid email address");
                    return;
                }else{
                    mReturnMsg.setVisibility(View.GONE);
                    sendConfirmationCodeToEmail(mEmail.getText().toString());
                }
                break;
            case R.id.tv_i_have_conf_code:
                llSendConfirmation.setVisibility(View.GONE);
                llConfirmCode.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_confirm_code_submit:
                if(edtConfirmEmail.getText().toString().isEmpty()){
                    edtConfirmEmail.setError("Please enter email address");
                    return;
                }else if(edtConfirmEmail.getText().toString().length()<5){
                    edtConfirmEmail.setError("Please enter a valid email address");
                    return;
                }else if(edtNewPassword.getText().toString().isEmpty()){
                    edtNewPassword.setError("Please enter new password");
                    return;
                }else if(edtConfirmPassword.getText().toString().isEmpty()){
                    edtConfirmPassword.setError("Please confirm your password");
                    return;
                }else{
                    if(edtConfirmCode.getText().toString().isEmpty()){
                        edtConfirmCode.setError("Please enter confirmation code");
                        return;
                    }else {
                        mReturnMsgConfirmation.setVisibility(View.GONE);
                        submitConfirmationCode(edtConfirmEmail.getText().toString(), edtConfirmCode.getText().toString());
                    }
                }
                break;


        }
    }

    private void submitConfirmationCode(final String email_address, String confirmation_code) {
        Map<String, Object> map = new HashMap<>();
        map.put("email_address", email_address);
        map.put("confirmation_code", confirmation_code);
        map.put("new_password", edtNewPassword.getText().toString());
        map.put("confirm_password", edtConfirmPassword.getText().toString());
        map.put("type", "forgot_password");
        final ProgressDialog mProgressDialog = new ProgressDialog(getContext());
        final String defaultMsg = "Could not send record. Please check your connection";
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Checking... please wait");
        mProgressDialog.show();
        Requests.submitConfirmationCode(map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mProgressDialog.dismiss();
                getActivity().runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                        //mProgressDialog.cancel();
                        Toast.makeText(getContext(), defaultMsg,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mProgressDialog.dismiss();
                String res = Objects.requireNonNull(response.body()).string();
                Log.e("res", res);

                try {
                    JSONObject object = new JSONObject(res);
                    final String message = object.optString("message");
                    String status = object.optString("status");
                    if(status.equals("1")){
                        getActivity().runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getContext(), Login.class);
                                i.putExtra("user_email",email_address);
                                startActivity(i);


                            }
                        });
                    }else{
                        getActivity().runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                                mReturnMsgConfirmation.setText(message);
                                mReturnMsgConfirmation.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },getContext());
    }

    private void sendConfirmationCodeToEmail(String email_address) {
        final String emailAddress = email_address;
        Map<String, Object> map = new HashMap<>();
        map.put("email_address", email_address);
        map.put("type", "forgot_password");
        final ProgressDialog mProgressDialog = new ProgressDialog(getContext());
        final String defaultMsg = "Could not send record. Please check your connection";
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Checking... please wait");
        mProgressDialog.show();
        Requests.sendConfirmationCode(map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mProgressDialog.dismiss();
                getActivity().runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                        //mProgressDialog.cancel();
                        Toast.makeText(getContext(), defaultMsg,Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mProgressDialog.dismiss();
                String res = Objects.requireNonNull(response.body()).string();
                Log.e("res", res);

                try {
                    JSONObject object = new JSONObject(res);
                    final String message = object.optString("message");
                    String status = object.optString("status");
                    if(status.equals("1")){
                        getActivity().runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                mReturnMsgConfirmation.setText(message);
                                mReturnMsgConfirmation.setVisibility(View.VISIBLE);
                                edtConfirmEmail.setText(emailAddress);
                                llSendConfirmation.setVisibility(View.GONE);
                                llConfirmCode.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        getActivity().runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                mReturnMsg.setText(message);
                                mReturnMsg.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },getContext());
    }
}