package com.example.promdatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * REGISTRATION AKTIVITY
 * NAREDI NOVEGA UPORABNIKA
 * LAHKO PRIDE NAZAJ NA FIRST PAGE
 */
public class RegistrationActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mIme;
    private RadioGroup mRadioGroup;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private ImageView mRegisterBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null)
                {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegister = findViewById(R.id.register);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mIme = findViewById(R.id.ime);
        mRadioGroup = findViewById(R.id.radioGroup);

        mRegisterBackArrow = findViewById(R.id.registrationBackArrow);
        mRegisterBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, ChooseLoginRegisterActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int selectId = mRadioGroup.getCheckedRadioButtonId();

                final RadioButton radioButton = findViewById(selectId);
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String ime = mIme.getText().toString();

                if(!(email.isEmpty() || password.isEmpty() || ime.isEmpty() || radioButton.getText() == null)) //preverjam če je uporabnik izpolnil vsa polja
                    {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful())
                            {
                                Toast.makeText(RegistrationActivity.this, "sign up error", Toast.LENGTH_SHORT).show(); // ČE NI USPEŠNO TOASTA ERROR
                            }

                            else
                            {
                                String userId = mAuth.getCurrentUser().getUid();

                                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(userId);
                                Map userInfo = new HashMap<>();
                                userInfo.put("ime",ime);
                                userInfo.put("spol",radioButton.getText().toString());
                                userInfo.put("profileImageUrl","https://bit.ly/2tEcWAM");// NASTAVIM DEFAULT PROFILKO, KI JO KASNEJE LAHKO SPREMENI
                                userInfo.put("visible", "true");
                                currentUserDb.updateChildren(userInfo);
                            }
                        }
                    });

                    }
                else
                        {
                            Toast.makeText(RegistrationActivity.this, "nisi izpolnil vseh polj!", Toast.LENGTH_SHORT).show();
                        }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
