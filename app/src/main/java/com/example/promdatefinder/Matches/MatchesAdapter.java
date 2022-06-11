package com.example.promdatefinder.Matches;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.promdatefinder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {

    private List<MatchesObject> matchesList;
    private Context context;
    private DatabaseReference databaseReference;
    public  MatchesAdapter(List<MatchesObject> matchesList, Context context) {
        this.matchesList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders((layoutView));

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final MatchesViewHolders matchesViewHolders, int i) {
    matchesViewHolders.mMatchId.setText(matchesList.get(i).getUserId());
    matchesViewHolders.mMatchName.setText(matchesList.get(i).getIme());
    Glide.with(context).load(matchesList.get(i).getProfileImageUrl()).into(matchesViewHolders.mMatchImage);
    databaseReference = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(matchesList.get(i).getUserId()).child("status");
    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                String status = dataSnapshot.getValue().toString();
                if (status != null) {
                    if (status.equals("online"))
                        matchesViewHolders.mStatus.setBackgroundResource(R.drawable.ic_online);
                    else
                        matchesViewHolders.mStatus.setBackgroundResource(R.drawable.ic_offline);
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });


    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
