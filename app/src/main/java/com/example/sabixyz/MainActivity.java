package com.example.sabixyz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;

import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences userInfoPreference;
    public static TextView tv_header_title;
    private TextView tv_nav_user_name, tv_nav_user_email, tv_nav_user_phone;
    ImageView mProfileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_icon);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_menu_icon);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        tv_nav_user_email = headerLayout.findViewById(R.id.tv_nav_user_email);
        tv_nav_user_name = headerLayout.findViewById(R.id.tv_nav_user_name);
        tv_nav_user_phone = headerLayout.findViewById(R.id.tv_nav_user_phone);
        tv_header_title = toolbar.findViewById(R.id.tv_app_title_header);
        mProfileImageView = headerLayout.findViewById(R.id.circleImage_nav_header_profile_image);



        populateProfileDetailsOnDrawer();

        Fragment landingscreenfragment = new LandingScreenFragment();
        get_fragment_to_display(landingscreenfragment);
    }

    public void populateProfileDetailsOnDrawer() {
        userInfoPreference = getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);

        tv_nav_user_email.setText(userInfoPreference.getString(getString(R.string.user_email), ""));
        tv_nav_user_name.setText(userInfoPreference.getString(getString(R.string.first_name), "")+" "+userInfoPreference.getString(getString(R.string.last_name), ""));
        tv_nav_user_phone.setText(userInfoPreference.getString(getString(R.string.user_phone), ""));

        /**
         * GET THE STORED PROFILE IMAGE BASE64 STRING
         * if the string exist, convert to image, else it should display an anonymous avater
         */

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
                        userInfoPreference.edit().apply();
                    }

                    @Override
                    public void onError(Exception ex) {
                        Toast.makeText(getBaseContext(),"Could not load profile image",Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                mProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.default_user_icon));
            }
        }

        Log.e("Populated Details", userInfoPreference.getString(getString(R.string.user_email), ""));


    }

    private void get_fragment_to_display(Fragment fragmentselected) {

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentselected);
        fragmentTransaction.commit();
        fragmentManager.popBackStack();

        //getSupportFragmentManager().popBackStack("profile", androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);




/*
        LandingScreenFragment fragment = new LandingScreenFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
*/
    }

    @Override
    public void onBackPressed() {

        Log.e("BackPressed", "back is pressed");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            } else {
                finish();
            }

        }

        //super.onBackPressed();
         /*
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getFragmentManager().getBackStackEntryCount() == 0) {
                 Log.e("BackPressed2", "back is pressed but entered here");
            //super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        //}

        }
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            tv_header_title.setText("Books");
            Fragment landingscreenfragment = new LandingScreenFragment();
            get_fragment_to_display(landingscreenfragment);
        } else if (id == R.id.nav_user) {
            tv_header_title.setText(R.string.menu_my_profile);
            Toast.makeText(getBaseContext(), "nav_user is clicked", Toast.LENGTH_LONG).show();
            Fragment userprofilescreenfragment = new UserProfileScreenFragment();
            get_fragment_to_display(userprofilescreenfragment);
        }  else if (id == R.id.nav_credit_card) {
            tv_header_title.setText(R.string.menu_payments);
            Fragment payments = new PaymentFragment();
            get_fragment_to_display(payments);
        } else if (id == R.id.nav_categories) {
            tv_header_title.setText(R.string.menu_categories);
            Fragment CategoriesScreenFragment = new CategoriesScreenFragment();
            get_fragment_to_display(CategoriesScreenFragment);
        } else if (id == R.id.nav_books) {
            tv_header_title.setText(R.string.menu_my_books);
            Fragment mybooks = new MyBooksFragment();
            get_fragment_to_display(mybooks);
        } else if (id == R.id.nav_settings) {
            tv_header_title.setText(R.string.menu_settings);
        } else if (id == R.id.nav_logout) {
            userInfoPreference.edit().clear().apply();
            Intent i = new Intent(MainActivity.this, Login.class);
            i.putExtra("user_email","");
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onResume(){
        super.onResume();
        //OnResume Fragment
    }


}
