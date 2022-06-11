package com.example.promdatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button mReset;
    private EditText mEmail;
    private FirebaseAuth firebaseAuth;
    @Override
    // ON CREATE
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // LAYOUT IZ activity_forgot_password.xml
        mEmail = findViewById(R.id.email);
        mReset = findViewById(R.id.resetPassword);
        if(getIntent().getExtras().getString("email")!=null);
        mEmail.setText(getIntent().getExtras().getString("email").toString());
        firebaseAuth = FirebaseAuth.getInstance();
        // REFERENCA NA AVTENTIKATOR



    }

    public void goBack(View view) {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void resetPassword(View view) {
        firebaseAuth.sendPasswordResetEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgotPasswordActivity.this,"Geslo uspešno resetirano",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
