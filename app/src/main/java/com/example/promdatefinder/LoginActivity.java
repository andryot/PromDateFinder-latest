package com.example.promdatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button mLogin;
    private EditText mEmail, mPassword;
    private ImageView mLoginBackArrow;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private SignInButton mSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 2;
    private GoogleApiClient mGoogleApiClient;

    @Override
    // ON CREATE
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // LAYOUT IZ activity_login.xml
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // ČAKA DA SE UPORABNIK LOGINA
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null) // ČE JE LOGIN RPAVILEN SPUSTI NAPREJ
                {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

            }
        };

        mLogin = findViewById(R.id.login);
        mSignIn = findViewById(R.id.sign_in_button);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBackArrow = findViewById(R.id.loginBackArrow);
        mLoginBackArrow.setOnClickListener(new View.OnClickListener() { // GUMB ZA NAZAJ V PRVI PAGE
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ChooseLoginRegisterActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // CLICK LISTENER ZA LOGIN GUMB
                final String mail = mEmail.getText().toString();
                final String geslo = mPassword.getText().toString();
                if(!(mail.isEmpty() || geslo.isEmpty() )) // preverjam če email in geslo nista prazna, da ne crasha app
                {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                                Toast.makeText(LoginActivity.this, "Login uspesen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
                {
                        Toast.makeText(LoginActivity.this, "prazno polje!", Toast.LENGTH_SHORT).show();
                }
        }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestScopes(
                        new Scope(Scopes.PLUS_ME), new Scope(Scopes.PROFILE)
                )
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

mSignIn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
                signIn();
    }
});



}
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() { //POBRIŠE ZA SABO
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

            // Signed in successfully, show authenticated UI.
           // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(LoginActivity.this, "Error!",Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
       // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew)
                            {

                                String userId = mAuth.getCurrentUser().getUid();

                                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(userId);
                                Map userInfo = new HashMap<>();
                                String ime="Abc";
                                String uri = "https://bit.ly/2tEcWAM";
                                if(acct.getDisplayName()!=null)
                                 ime = acct.getDisplayName();
                                if(acct.getPhotoUrl()!=null)
                                    uri= acct.getPhotoUrl().toString();
                                userInfo.put("ime", ime);
                                userInfo.put("spol","");
                                userInfo.put("profileImageUrl",uri);// NASTAVIM DEFAULT PROFILKO, KI JO KASNEJE LAHKO SPREMENI
                                userInfo.put("visible", "true");
                                userInfo.put("status","online");
                                userInfo.put("novU","true");
                                currentUserDb.updateChildren(userInfo);
                                Toast.makeText(LoginActivity.this, "Registracija uspešna",Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(LoginActivity.this, "Login uspešen",Toast.LENGTH_SHORT).show();
                        }
                        else
                            {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Avtentikacija ni uspela!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void forgotPassword(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        if(mEmail.getText() !=null)
        {
            String mail = mEmail.getText().toString();
            Bundle b = new Bundle();
            b.putString("email", mail);
            intent.putExtras(b);
        }
        view.getContext().startActivity(intent);
    }
}