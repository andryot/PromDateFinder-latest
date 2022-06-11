package com.example.promdatefinder.Matches;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.promdatefinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManeger;
    private ImageView mMatchesBackArrow,mStatus;
    private String currentUporabnikId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUporabnikId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManeger = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManeger);
        mMatchesBackArrow = findViewById(R.id.matchesBackArrow);


        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);

        //getUserMatchId();

        mMatchesBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        resultsMatches.clear();
        mMatchesAdapter.notifyDataSetChanged();
        getUserMatchId();
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(currentUporabnikId).child("povezave").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot match : dataSnapshot.getChildren())
                    {
                        FetchMatchInformation(match.getKey());
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /*
    private void reloadi ()
    {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(currentUporabnikId).child("povezave").child("matches");
        matchDb.ev

    }
    */
    private void FetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String userId = dataSnapshot.getKey();
                    String ime = "";
                    String profileImageUrl = "";

                    if(dataSnapshot.child("ime").getValue()!= null)
                    {
                        ime=dataSnapshot.child("ime").getValue().toString();
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue()!= null)
                    {
                        profileImageUrl=dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    MatchesObject obj = new MatchesObject(userId, ime, profileImageUrl);
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();

    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }
}
