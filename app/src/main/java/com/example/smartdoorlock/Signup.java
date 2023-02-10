package com.example.smartdoorlock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView already_account;
    EditText inputemail,inputpassword,input_confirmpassword;
    Button signup;
    String Emailpattern ="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        inputemail =findViewById(R.id.email);
        inputpassword = findViewById(R.id.password);
        input_confirmpassword= findViewById(R.id.confirmpassword);
        progressDialog = new ProgressDialog(this); // progress Dialog
        mAuth= FirebaseAuth.getInstance(); // getting firebase instance
        mUser = mAuth.getCurrentUser(); // Getting Current User From firebase
        signup = findViewById(R.id.btn_signup);
        already_account = findViewById(R.id.already_account);

        already_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this,Login.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAuthentication();
            }
        });
    }

    private void performAuthentication(){
        String email = inputemail.getText().toString();
        String password = inputpassword.getText().toString();
        String confirmpassword = input_confirmpassword.getText().toString();

        if(!email.matches(Emailpattern))
        {
            inputemail.setError("Please Enter Correct Email");
        }
        else if(password.isEmpty() || password.length()<8)
        {
            inputpassword.setError("This field is required");
        }
        else if(!password.equals(confirmpassword))
        {
            input_confirmpassword.setError("Password Does not matched");
        }
        else
        {
            progressDialog.setMessage("Please Wait");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Intent intent= new Intent(Signup.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(Signup.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(Signup.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}