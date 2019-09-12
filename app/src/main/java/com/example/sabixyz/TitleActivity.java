package com.example.sabixyz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TitleActivity extends AppCompatActivity {
    private Button btnSignup, btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        btnSignin = findViewById(R.id.btn_signin);
        btnSignup = findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i_title = new Intent(TitleActivity.this.getApplicationContext(), SignupActivity.class);
                TitleActivity.this.startActivity(i_title);
                TitleActivity.this.finish();

            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_title = new Intent(TitleActivity.this.getApplicationContext(), Login.class);
                i_title.putExtra("user_email","");
                TitleActivity.this.startActivity(i_title);
                TitleActivity.this.finish();
            }
        });
    }
}
