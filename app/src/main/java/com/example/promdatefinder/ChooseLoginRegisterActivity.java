package com.example.promdatefinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class ChooseLoginRegisterActivity extends AppCompatActivity {

    private Button mLogin, mRegister;
    @Override
    // ON CREATE
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_register); // LAYOUT IZ activity_choose_login_register.xml

        // REFERENCA NA AVTENTIKATOR
        FirebaseAuth auth = FirebaseAuth.getInstance();


        // Auto preusmeritev, če je uporabnik že loginan
        if(auth.getCurrentUser()!=null)
        {
            Intent intent = new Intent(ChooseLoginRegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // GUMBA ZA V AKTIVNOST LOGIN IN REGISTER
        mLogin = findViewById(R.id.login);
        mRegister = findViewById(R.id.register);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginRegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginRegisterActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
