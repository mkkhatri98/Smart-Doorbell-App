package com.example.smartdoorlock;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    boolean state;
    ImageView picture;
    Button takePicture,unlockDoor,logOut;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        initialize();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Smart Bell");
        mAuth= FirebaseAuth.getInstance();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                //code to do in new thread
                if(isServiceRunning() == false)
                {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(MainActivity.this, BackgroundService.class));
                    }else {
                        startService(new Intent(MainActivity.this, BackgroundService.class));
                    }
                }
            }
        });
        thread.start();

        imageRead();
        stateRead();
        onClick();





    }

    public void initialize(){
        picture=(ImageView) findViewById(R.id.picture);
        takePicture=(Button) findViewById(R.id.take_picture);
        unlockDoor=(Button) findViewById(R.id.unlock_door);
        logOut = (Button) findViewById(R.id.logOut);
    }

    public void stateRead(){
        databaseReference.child("unlockDoor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                state=snapshot.getValue(boolean.class);
                if(state)
                {
                    unlockDoor.setText("Door Open");
                }
                else
                    unlockDoor.setText("Door Locked");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClick(){
        takePicture.setOnClickListener(view -> {
            databaseReference.child("takePicture").setValue(true);
            Toast.makeText(MainActivity.this, "Taking Picture. Please Wait", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            imageRead();
        });

        unlockDoor.setOnClickListener(view -> {
            if(state==true)
            {
                databaseReference.child("unlockDoor").setValue(false);
                //state=false;
            }
            else
                databaseReference.child("unlockDoor").setValue(true);
            // state=true;
        });

        logOut.setOnClickListener(view -> {
            mAuth.signOut();
            stopService(new Intent(MainActivity.this, BackgroundService.class));
            startActivity(new Intent(MainActivity.this,Login.class));
            finish();
            new BackgroundService().count=0;
        });
    }

    public void imageRead(){
        DatabaseReference getImage = databaseReference.child("image");

        getImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.getValue(String.class);

                Picasso.get().load(link).into(picture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.smartdoorlock.BackgroundService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}