package com.example.smartdoorlock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Launcher extends AppCompatActivity {
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_launcher);

        mAuth= FirebaseAuth.getInstance();

        new Handler().postDelayed(() -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if(user == null){
                startActivity(new Intent(Launcher.this,Login.class));
            }
            else {
                startActivity(new Intent(Launcher.this,MainActivity.class));
            }
            finish();

        },2000);
    }
}