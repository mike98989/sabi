package com.sabi.sabixyz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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


public class SignupConfirmationFragment extends Fragment {
private String email;
private EditText edtConfirmUserEmail, edtConfirmUserCode;
private TextView tvReturnMsg, tv_return_success_msg;
private Button btnConfirmUserCodeSubmit,btnConfirmUserCodeSubmitSuccess;
private LinearLayout linearlayout_user_confirm_form_success, linearlayout_user_confirm_form;
    public SignupConfirmationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_signup_confirmation, container, false);
        edtConfirmUserEmail = view.findViewById(R.id.edt_confirm_user_email);
        edtConfirmUserCode  = view.findViewById(R.id.edt_confirm_user_code);
        tvReturnMsg = view.findViewById(R.id.tv_return_msg);
        btnConfirmUserCodeSubmit = view.findViewById(R.id.btn_confirm_user_code_submit);
        linearlayout_user_confirm_form_success = view.findViewById(R.id.linearlayout_user_confirm_form_success);
        linearlayout_user_confirm_form = view.findViewById(R.id.linearlayout_user_confirm_form);
        btnConfirmUserCodeSubmitSuccess = view.findViewById(R.id.btn_confirm_user_code_submit_successful);
        tv_return_success_msg = view.findViewById(R.id.tv_return_success_msg);
        if (getArguments() != null) {
            email = getArguments().getString("email");
            if(!email.isEmpty()){
                edtConfirmUserEmail.setText(email);
                edtConfirmUserEmail.setFocusable(false);
            }
        }


        btnConfirmUserCodeSubmitSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(getActivity(), Login.class);
            i.putExtra("user_email", edtConfirmUserEmail.getText().toString());
            startActivity(i);
            }
        });

        btnConfirmUserCodeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtConfirmUserEmail.getText().toString().isEmpty()){
                    edtConfirmUserEmail.setError("Please enter email address");
                }
                else if(edtConfirmUserCode.getText().toString().isEmpty()){
                    edtConfirmUserCode.setError("Please enter confirmation code");
                }else{
                    final ProgressDialog pDialog = new ProgressDialog(getContext());
                    pDialog.setMessage("Confirming Code. Please wait...");
                    pDialog.show();

                    Map<String, Object> map = new HashMap<>();
                    map.put("confirm_email", edtConfirmUserEmail.getText().toString());
                    map.put("confirm_code", edtConfirmUserCode.getText().toString());

                    Requests.confirmSignup(map, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            pDialog.dismiss();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //pdialog.dismiss();
                                    tvReturnMsg.setText("Please check your internet connection");
                                    tvReturnMsg.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            pDialog.dismiss();
                            String res = Objects.requireNonNull(response.body()).string();
                            Log.e("msg", res);

                            try {
                                JSONObject jObject =  new JSONObject(res);
                                String strStatus = jObject.optString("status");
                                final String strMessage = jObject.optString("msg");
                                if(!strStatus.equals("1")){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            linearlayout_user_confirm_form.setVisibility(View.VISIBLE);
                                            tvReturnMsg.setText(strMessage);
                                            tvReturnMsg.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }else{
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            linearlayout_user_confirm_form.setVisibility(View.GONE);
                                            tv_return_success_msg.setText(strMessage);
                                            linearlayout_user_confirm_form_success.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }

                                //final String strData = jObject.optString("data");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, getContext());
                }

            }
        });




        return view;
    }


}
