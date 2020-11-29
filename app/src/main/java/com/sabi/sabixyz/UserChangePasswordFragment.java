package com.sabi.sabixyz;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabi.sabixyz.helper.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class UserChangePasswordFragment extends Fragment {
    private SharedPreferences userInfoPreference;
    private EditText mOldPassword,mNewPassword,mConfirmPassword;
    private TextView mReturnMsg;
    private Button mBtnChangePassword;
    private ImageView mProfileImageView;
    private String old_password, new_password, confirm_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfoPreference = getActivity().getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_change_password, container, false);
        mOldPassword = view.findViewById(R.id.edt_old_password);
        mNewPassword = view.findViewById(R.id.edt_new_password);
        mConfirmPassword = view.findViewById(R.id.edt_confirm_password);
        mProfileImageView = view.findViewById(R.id.circleimage_profile_pics);
        mBtnChangePassword = view.findViewById(R.id.btn_change_profile);
        mReturnMsg = view.findViewById(R.id.tv_return_msg);
        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordForm();
            }
        });

        String profileImageBase64String = userInfoPreference.getString("profile_image_base_64_string","");
        if (profileImageBase64String != null && !profileImageBase64String.isEmpty()) {
            byte[] b = Base64.decode(profileImageBase64String, Base64.DEFAULT);
            InputStream is = new ByteArrayInputStream(b);
            mProfileImageView.setImageBitmap(BitmapFactory.decodeStream(is));
        } else {
            mProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.default_user_icon));
        }
        return view;
    }

    private void ChangePasswordForm() {
        mReturnMsg.setVisibility(View.GONE);
        old_password = mOldPassword.getText().toString();
        new_password = mNewPassword.getText().toString();
        confirm_password = mConfirmPassword.getText().toString();

        if(old_password.isEmpty()){
            mOldPassword.setError("Please enter old password!");
            return;
        }else if(new_password.isEmpty()){
            mNewPassword.setError("Please enter old password!");
            return;
        }else if(confirm_password.isEmpty()){
            mConfirmPassword.setError("Please enter old password!");
            return;
        }else{
            if(new_password.equals(confirm_password)){
                Map<String, Object> map = new HashMap<>();
                map.put("DeviceToken", userInfoPreference.getString(getString(R.string.user_token), ""));
                map.put("old_password", old_password);
                map.put("new_password", new_password);
                map.put("confirm_password",  confirm_password);
                final ProgressDialog mProgressDialog = new ProgressDialog(getContext());
                final String defaultMsg = "Could not update records. Please check your connection";
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Saving Update... please wait");
                mProgressDialog.show();

                Requests.changePassword(map, new Callback() {
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
                                        mReturnMsg.setText(message);
                                        mReturnMsg.setVisibility(View.VISIBLE);
                                        Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
                                getActivity().runOnUiThread(new Thread() {
                                    @Override
                                    public void run() {
                                        mReturnMsg.setText(message);
                                        mReturnMsg.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },getContext());
            }else{
                mReturnMsg.setText("Passwords do not match!");
                mReturnMsg.setVisibility(View.VISIBLE);
                return;
            }
        }

    }
}