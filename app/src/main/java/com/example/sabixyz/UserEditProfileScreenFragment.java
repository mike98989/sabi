package com.example.sabixyz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sabixyz.helper.FilePath;
import com.example.sabixyz.helper.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class UserEditProfileScreenFragment extends Fragment implements View.OnClickListener {
private CircleImageView mProfileImage;
private static final int sRESULT_LOAD_IMAGE = 1;
private String selectedFilePath;
private Button mBtnProfileImage, mBtnProfileDetailsSubmit;
private SharedPreferences userInfoPreference;
private ProgressDialog mProgressDialog;
private static final int REQUEST_WRITE_PERMISSION = 786;
private TextView mReturnMessage;
private EditText mFirstName, mLastName, mEmail, mPhone;


    public UserEditProfileScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfoPreference = getActivity().getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_edit_profile_screen, container, false);
        mProfileImage = view.findViewById(R.id.circleimage_profile_pics);
        mReturnMessage = view.findViewById(R.id.tv_return_msg);
        mProfileImage.setOnClickListener(this);
        mBtnProfileImage = view.findViewById(R.id.btn_profile_image_upload);
        mBtnProfileDetailsSubmit = view.findViewById(R.id.btn_edit_profile);
        mFirstName = view.findViewById(R.id.edt_user_firstname);
        mLastName = view.findViewById(R.id.edt_user_lastname);
        mEmail = view.findViewById(R.id.edt_user_email2);
        mPhone = view.findViewById(R.id.edt_user_phone);

        mBtnProfileDetailsSubmit.setOnClickListener(this);
        mBtnProfileImage.setOnClickListener(this);

        populateProfileDetails();
        return view;
    }

    private void populateProfileDetails() {
        String profileImageBase64String = userInfoPreference.getString(getString(R.string.user_profile_image_base64_string),"");
        mFirstName.setText(userInfoPreference.getString(getString(R.string.first_name), ""));
        mLastName.setText(userInfoPreference.getString(getString(R.string.last_name), ""));
        mEmail.setText(userInfoPreference.getString(getString(R.string.user_email), ""));
        mEmail.setFocusable(false);
        mPhone.setText(userInfoPreference.getString(getString(R.string.user_phone), ""));

        if (profileImageBase64String != null && !profileImageBase64String.isEmpty()) {
            byte[] b = Base64.decode(profileImageBase64String, Base64.DEFAULT);
            InputStream is = new ByteArrayInputStream(b);
            mProfileImage.setImageBitmap(BitmapFactory.decodeStream(is));
        } else {
            mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.default_user_icon));
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circleimage_profile_pics:
                showFileChooser();
                break;
            case R.id.btn_profile_image_upload:
                Bitmap image = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
                new uploadImage(image, "profile_image", selectedFilePath).execute();
            case R.id.btn_edit_profile:
                submitProfileDetails();
        }
        
    }

    private void submitProfileDetails() {
      if(mFirstName.getText().toString().isEmpty()){
          mFirstName.setError("Please Enter First Name");
      }
      else if(mLastName.getText().toString().isEmpty()){
            mLastName.setError("Please Enter First Name");
      }
      else if(mPhone.getText().toString().isEmpty()){
          mPhone.setError("Please Enter First Name");
      }else{
          Map<String, Object> map = new HashMap<>();
          map.put("DeviceToken", userInfoPreference.getString(getString(R.string.user_token), ""));
          map.put("first_name", mFirstName.getText().toString());
          map.put("last_name",  mLastName.getText().toString());
          map.put("phone",  mPhone.getText().toString());

          final ProgressDialog mProgressDialog = new ProgressDialog(getContext());
          final String defaultMsg = "Could not update records. Please check your connection";
          mProgressDialog.setIndeterminate(true);
          mProgressDialog.setMessage("Saving Update... please wait");
          mProgressDialog.show();

        Requests.updateUserDetails(map, new Callback() {
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
                                mReturnMessage.setText(message);
                                mReturnMessage.setVisibility(View.VISIBLE);

                                Editor editor = userInfoPreference.edit();
                                editor.putString(getString(R.string.user_phone),mPhone.getText().toString());
                                editor.putString(getString(R.string.last_name),mLastName.getText().toString());
                                editor.putString(getString(R.string.first_name),mFirstName.getText().toString());
                                editor.apply();

                                ((MainActivity) getActivity()).populateProfileDetailsOnDrawer();

                                Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();

                            }
                        });
                    }else{

                        getActivity().runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                mReturnMessage.setText(message);
                                mReturnMessage.setVisibility(View.VISIBLE);
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

    private void showFileChooser() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
                Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryintent, sRESULT_LOAD_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        populateProfileDetails();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == sRESULT_LOAD_IMAGE && data != null) {
                Uri selectedImage = data.getData();
                selectedFilePath = FilePath.getPath(getActivity(), selectedImage);
                mProfileImage.setImageURI(selectedImage);
                mBtnProfileImage.setVisibility(View.VISIBLE);

            }
        }
//        }
    }

    private class uploadImage extends AsyncTask <Void, Void, Request> {
        Bitmap image;
        String name;
        String selectedFilePath;

        public uploadImage(Bitmap image, String profile_image, String selectedFilePath) {
            this.image = image;
            this.name = name;
            this.selectedFilePath = selectedFilePath;

            ProgressDialog mProgressDialog2 = new ProgressDialog(getContext());
            mProgressDialog2.setIndeterminate(true);
            mProgressDialog2.setMessage("Uploading... please wait");
            mProgressDialog2.show();

            mProgressDialog=mProgressDialog2;
        }

        @Override
        protected Request doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            final String defaultMessage = "Could not update profile";
            File selectedFile = new File(selectedFilePath);
            Map<String, Object> map = new HashMap<>();
            map.put("DeviceToken", userInfoPreference.getString(getString(R.string.user_token), ""));
            map.put("Action", "1");
            map.put("OverwriteDuplicate", "1");

            Requests.uploadImage(map, selectedFile, userInfoPreference.getString("cookie", ""), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.e("defaultmsg", defaultMessage);
                    mProgressDialog.dismiss();
                    getActivity().runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            //mProgressDialog.cancel();
                            Toast.makeText(getContext(), defaultMessage,Toast.LENGTH_LONG).show();
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
                        String data = object.optString("data");
                        JSONObject data_object = new JSONObject(data);
                        final String imageUrl = data_object.optString("image");
                        Log.e("message", message);
                        Log.e("base64profile",encodedImage);
                        if(status.equals("1")){
                            getActivity().runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    mReturnMessage.setText(message);
                                    mReturnMessage.setVisibility(View.VISIBLE);
                                    String profileImageUrl = imageUrl.replace("\"", "");

                                    Editor editor = userInfoPreference.edit();
                                    editor.putString(getString(R.string.user_profile_image_url),profileImageUrl);
                                    editor.putString(getString(R.string.user_profile_image_base64_string),encodedImage);
                                    //editor.commit();
                                    editor.apply();
                                    ((MainActivity) getActivity()).populateProfileDetailsOnDrawer();

                                    //UserProfileScreenFragment.populateProfileImage();
                                    //UserProfileScreenFragment.populateProfileImage();

                                    Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                                    mBtnProfileImage.setVisibility(View.GONE);
                                }
                            });


                        }else{
                            getActivity().runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    mReturnMessage.setText(message);
                                    mReturnMessage.setVisibility(View.VISIBLE);
                                }
                            });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                }
            },getContext());

            return null;
        }
    }
}
