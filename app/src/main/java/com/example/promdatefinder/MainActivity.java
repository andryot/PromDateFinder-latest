package com.example.promdatefinder;

import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.promdatefinder.Cards.arrayAdapter;
import com.example.promdatefinder.Cards.cards;
import com.example.promdatefinder.Matches.MatchesActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private cards[] cards_data;
    private com.example.promdatefinder.Cards.arrayAdapter arrayAdapter;
    private int i;

    private FirebaseAuth mAuth; //Firebase baza
    private String currentUId; //ID uporabnika

    private DatabaseReference usersDb, google_nov; //Referenca na uporabnika - Firebase
    private ImageView mUserInfo;
    String profileId;
    ListView listView;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    Dialog myDialog;
    List<cards> rowItems; //kartice uporabnikov, ki se prikažejo



    public class AppLifecycleListener implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onAppDidEnterForeground() {
        usersDb.child(currentUId).child("status").setValue("online");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onAppDidEnterBackground() {
            usersDb.child(currentUId).child("status").setValue("offline");
        }
    }

    //FUNKCIJA SE IZVEDE OB ODPRTJU APLIKACIJE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //LAYOUT IT activity_main.xml

        //String neki = String.valueOf(ProcessLifecycleOwner.get().getLifecycle().getCurrentState());
        //Toast.makeText(MainActivity.this, neki, Toast.LENGTH_SHORT).show();
        // REFERENCA NA TABELO UPORABNIKI
        usersDb = FirebaseDatabase.getInstance().getReference().child("Uporabniki");
        // AVTENTIKACIJA UPORABNIKA
        mAuth = FirebaseAuth.getInstance();
        //TRENUTNI UPORABNIK
        currentUId = mAuth.getCurrentUser().getUid();
        checkUserSpol();//preveri kateri spol je prijavljen - FUNKCIJA

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        myDialog = new Dialog(this);



        google_nov = usersDb.child(currentUId).child("novU");
        google_nov.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    if(dataSnapshot.getValue().toString().equals("true"))
                    {
                        myDialog.setContentView(R.layout.choose_gender_google);
                        myDialog.setCanceledOnTouchOutside(false);
                        myDialog.setCancelable(false);
                        Button save;
                        final RadioGroup mRadioGroup = (RadioGroup) myDialog.findViewById(R.id.google_radioGroup);

                        save = (Button) myDialog.findViewById(R.id.google_shrani);
                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int selectId = mRadioGroup.getCheckedRadioButtonId();
                                final RadioButton radioButton = myDialog.findViewById(selectId);
                                String spol = "Moski";
                                if(radioButton.getText()!=null)
                                    spol = radioButton.getText().toString();
                                usersDb.child(currentUId).child("spol").setValue(spol);
                                usersDb.child(currentUId).child("novU").setValue("false");
                                checkUserSpol();
                                myDialog.dismiss();
                            }
                        });
                        myDialog.show();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rowItems = new ArrayList<cards>(); // list kartic z slikami uporabnikov nasprotnega spola

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleListener());

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                //Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) { // OB SWIPE LEFT
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("povezave").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "levo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) { // OB SWIPE RIGHT
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("povezave").child("yup").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "desno", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {


            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }

        });


        // Toast izpis clicked ko kliknemo na kartico
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

            }
        });



        //}

    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleListener());

    }
    @Override
    protected void onRestart()
    {
        super.onRestart();
        rowItems.clear();
        arrayAdapter.notifyDataSetChanged();
        checkUserSpol();
    }

    // FUNKCIJA KI PREVERI KDAJ SE ODPRE NOVA POVEZAVA
    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("povezave").child("yup").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Toast.makeText(MainActivity.this, "new Match!", Toast.LENGTH_SHORT).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("povezave").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(currentUId).child("povezave").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String spolUporabnika; //spremenljivka z vrednostjo spola prijavljenega uporabnika
    private String nasprotniSpol; //spremenljivka z nasprotno vrednostjo spola prijavljenega uporabnika

    public void checkUserSpol() //funkcija, ki preveri spol uporabnika
    {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //user ima vrednost IDja trenutnega uporabnika
        DatabaseReference userDb = usersDb.child(user.getUid()); // Tabela moskih
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        if(dataSnapshot.child("spol").getValue() != null)
                        {
                            spolUporabnika = dataSnapshot.child("spol").getValue().toString();
                            switch (spolUporabnika)
                            {
                                case "Moski":
                                    nasprotniSpol="Zenska";
                                    break;
                                case "Zenska":
                                    nasprotniSpol="Moski";
                                    break;
                            }
                            getNasprotniSpol();
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    public void getNasprotniSpol() // NASPROTNI SPOL KI ZAPOLNE CARDS Z UPORABNIKI NASPROTNEGA SPOLA
    {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("spol").getValue() != null && dataSnapshot.child("visible").getValue()!=null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("povezave").child("nope").hasChild(currentUId) && !dataSnapshot.child("povezave").child("yup").hasChild(currentUId) &&
                            dataSnapshot.child("spol").getValue().toString().equals(nasprotniSpol) && dataSnapshot.child("visible").getValue().toString().equals("true")) {
                        String profileImageUrl = "https://bit.ly/2tEcWAM";
                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("https://bit.ly/2tEcWAM")) {
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                        }
                        cards Item = new cards(dataSnapshot.getKey(), dataSnapshot.child("ime").getValue().toString(), profileImageUrl);
                        rowItems.add(Item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

        // GUMB LOGOUT
    public void logout(View view) {

        usersDb.child(currentUId).child("status").setValue("offline");
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ChooseLoginRegisterActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });

    }

    // GUMB DO NASTAVITEV
    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }

    // GUMB DO POVEZAV
    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }
    protected OnBackPressedListener onBackPressedListener;

    // GUMB DO PROFILA NASPROTNEGA SPOLA
    public void goToProfile(View view) {
        profileId = rowItems.get(0).getUserId(); //pridobi ID od zadnjega uporabnika v ArrayListu in preusmeri na intent ki pokaze njegov profil
        //Toast.makeText(MainActivity.this, profileId, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ProfileActvity.class);
        Bundle b = new Bundle();
        b.putString("profileId", profileId);// ZBUNDLAM ID
        intent.putExtras(b); // ID POŠLEJM ZRAVNE V NOVO AKTIVNOST DA LAHKO TAM PRIKAŽEM USTREZEN PROFIL
        view.getContext().startActivity(intent);
        //String nekii = Integer.toString(stevec);
        //Toast.makeText(MainActivity.this, nekii, Toast.LENGTH_LONG).show();
    }

    // DISKLIKE GUMB
    public void eventDislike(View view) {

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);
        if (!rowItems.isEmpty()) {
            SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
            flingContainer.setAdapter(arrayAdapter);

            flingContainer.getTopCardListener().selectLeft();
        }
    }

    // LIKE GUMB
    public void eventLike(View view) {
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);
        if (!rowItems.isEmpty()) {
            SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
            flingContainer.setAdapter(arrayAdapter);

            flingContainer.getTopCardListener().selectRight();
        }
    }


    // STARA KODA
    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

}