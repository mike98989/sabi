package com.example.sabixyz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences userInfoPreference;
    private TextView tv_nav_user_name, tv_nav_user_email, tv_nav_user_phone, tv_header_title;

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


        userInfoPreference = getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);

        Log.e("Pref", userInfoPreference.getString(getString(R.string.user_email), ""));
        tv_nav_user_email.setText(userInfoPreference.getString(getString(R.string.user_email), ""));
        tv_nav_user_name.setText(userInfoPreference.getString(getString(R.string.full_name), ""));
        tv_nav_user_phone.setText(userInfoPreference.getString(getString(R.string.user_phone), ""));

        Fragment landingscreenfragment = new LandingScreenFragment();
        get_fragment_to_display(landingscreenfragment);
    }

    private void get_fragment_to_display(Fragment fragmentselected) {

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentselected);
        fragmentTransaction.commit();
/*
        LandingScreenFragment fragment = new LandingScreenFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        } else if (id == R.id.nav_categories) {
            tv_header_title.setText(R.string.menu_categories);
        } else if (id == R.id.nav_books) {
            tv_header_title.setText(R.string.menu_my_books);
        } else if (id == R.id.nav_settings) {
            tv_header_title.setText(R.string.menu_settings);
        } else if (id == R.id.nav_logout) {
            userInfoPreference.edit().clear().apply();
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
