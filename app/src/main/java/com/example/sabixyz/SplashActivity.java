package com.example.sabixyz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;

public class SplashActivity extends AppCompatActivity {
    protected boolean active = true;
    protected int splashTime = 1000; //1000
    ImageView goToTitleActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        goToTitleActivity = findViewById(R.id.img_goto_next_activity);
        final SharedPreferences userInfoPreference = getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);
        goToTitleActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//            if (AccountPreferences.isLoggedIn(activity)) {
                if (userInfoPreference.getString(SplashActivity.this.getString(R.string.user_token), null) != null) {
                    Intent i_title = new Intent(SplashActivity.this.getApplicationContext(), MainActivity.class);
                    SplashActivity.this.startActivity(i_title);
                    SplashActivity.this.finish();
                } else {
                    Intent i_title = new Intent(SplashActivity.this.getApplicationContext(), TitleActivity.class);
                    SplashActivity.this.startActivity(i_title);
                    SplashActivity.this.finish();
                }
            }
        });


        if (userInfoPreference.getString(getString(R.string.user_token), null) != null) {
            Thread splashThread = new Thread() {
                public void run() {
                    try {
                        int waited = 0;
                        while (active && waited < splashTime) {
                            sleep(100);//100
                            if (active)
                                waited += 100; //100
                        }
                    } catch (Exception ignored) {
                    } finally {
                        finish();
                        Intent i_main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i_main);
                        SplashActivity.this.finish();
                    }
                }
            };
            splashThread.start();
        }

    }
}
