package com.example.sabixyz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;


public class UserProfileScreenFragment extends Fragment {
private SharedPreferences userInfoPreference;
private TextView mFullname, mFirstName, mLastName,mPhonenumber, mEmail, mEmail2;
private Button mBtnEditProfile;
private ImageView mProfileImageView;


    public UserProfileScreenFragment() {

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
        View view  =  inflater.inflate(R.layout.fragment_user_profile_screen, container, false);
        mFullname = view.findViewById(R.id.tv_user_fullname);
        mFirstName = view.findViewById(R.id.tv_user_firstname);
        mLastName = view.findViewById(R.id.tv_user_lastname);
        mEmail = view.findViewById(R.id.tv_user_email);
        mEmail2 = view.findViewById(R.id.tv_user_email2);
        mPhonenumber = view.findViewById(R.id.tv_user_phone);
        mBtnEditProfile = view.findViewById(R.id.btn_edit_profile);
        mProfileImageView = view.findViewById(R.id.circleimage_profile_pics);

        mFullname.setText(userInfoPreference.getString(getString(R.string.first_name), "")+" "+userInfoPreference.getString(getString(R.string.last_name), ""));
        mFirstName.setText(userInfoPreference.getString(getString(R.string.first_name), ""));
        mLastName.setText(userInfoPreference.getString(getString(R.string.last_name), ""));
        mEmail.setText(userInfoPreference.getString(getString(R.string.user_email), ""));
        mEmail2.setText(userInfoPreference.getString(getString(R.string.user_email), ""));
        mPhonenumber.setText(userInfoPreference.getString(getString(R.string.user_phone), ""));


        String profileImageBase64String = userInfoPreference.getString(getString(R.string.user_profile_image_base64_string),"");
        if (profileImageBase64String != null && !profileImageBase64String.isEmpty()) {
            byte[] b = Base64.decode(profileImageBase64String, Base64.DEFAULT);
            InputStream is = new ByteArrayInputStream(b);
            mProfileImageView.setImageBitmap(BitmapFactory.decodeStream(is));
        } else {
            mProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.default_user_icon));
        }


        mBtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserEditProfileScreenFragment fragment = new UserEditProfileScreenFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                fragmentManager.popBackStack();
            }
        });

        return view;
    }


    public void populateProfileImage() {
        String profileImageBase64String = userInfoPreference.getString(getString(R.string.user_profile_image_base64_string),"");
        Log.e("base64", profileImageBase64String+"got here");
        if (profileImageBase64String != null && !profileImageBase64String.isEmpty()) {
            byte[] b = Base64.decode(profileImageBase64String, Base64.DEFAULT);
            InputStream is = new ByteArrayInputStream(b);
            this.mProfileImageView.setImageBitmap(BitmapFactory.decodeStream(is));
        }
        else {
            String profileImageUrl = userInfoPreference.getString(getString(R.string.user_profile_image_url),"");
            //String profileImageUrl = userInfoPreference.getString(getString(R.string.key_profile_image_URL),"");// AccountPreferences.getProfileImageUrl(this);
            Log.e("profileUrl",profileImageUrl);
            /**
             * If profile image Url exist, Load it into the picasso imageview and update the account preference
             * Else load the anonymous avater
             */
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Picasso.get().load(profileImageUrl).into(mProfileImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap image = ((BitmapDrawable) mProfileImageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        Log.e("EncodedImage", encodedImage);
                        userInfoPreference.edit().putString(getString(R.string.user_profile_image_base64_string),encodedImage);
                    }

                    @Override
                    public void onError(Exception ex) {
                        Toast.makeText(getContext(),"Could not load profile image",Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                mProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.default_user_icon));
            }
        }

        Log.e("Populated Details", userInfoPreference.getString(getString(R.string.user_email), ""));
    }


}
