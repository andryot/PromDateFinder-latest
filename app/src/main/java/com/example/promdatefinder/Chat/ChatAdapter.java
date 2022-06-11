package com.example.promdatefinder.Chat;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.promdatefinder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {

    private List<ChatObject> chatList;
    private Context context;
    DatabaseReference mDatabaseSlika;

    public ChatAdapter(List<ChatObject> matchesList, Context context) {
        this.chatList = matchesList;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders((layoutView));

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolders matchesViewHolders, int i) {

        matchesViewHolders.mMessage.setText(chatList.get(i).getMessage());
        if(chatList.get(i).getCurrentUser())
        {

            //matchesViewHolders.mMessage.setLayoutParams(params);
            matchesViewHolders.mMessage.setTextColor(Color.WHITE);
            //matchesViewHolders.mMessage.setGravity(Gravity.END);

            // SPOROCILO PRIJAVLENEGA UPOR. DAM NA DESNO STRAN Z RELATIVE LAYOUT ALIGN_PARENT_RIGHT
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            matchesViewHolders.mMessage.setLayoutParams(rlp);

            matchesViewHolders.mMessage.setBackgroundResource(R.drawable.chat_radius_nasprotni);
            matchesViewHolders.mProfilka.setVisibility(View.INVISIBLE);
            //matchesViewHolders.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));

        }
        else
        {
            matchesViewHolders.mMessage.setGravity(Gravity.START);
            matchesViewHolders.mMessage.setTextColor(Color.WHITE);
            //matchesViewHolders.mMessage.setBackgroundResource(R.drawable.chat_radius);
            mDatabaseSlika = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(chatList.get(i).getUserForImage()).child("profileImageUrl");
            mDatabaseSlika.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String url = dataSnapshot.getValue().toString();
                    Glide.with(context.getApplicationContext()).load(url).into(matchesViewHolders.mProfilka);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //matchesViewHolders.mProfilka.setImageURI();

            //matchesViewHolders.mContainer.setBackgroundColor(Color.parseColor("#2DB4C8"));
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
/*
    void nalozisliko()
    {
        mDatabaseSlika = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child()
        mDatabaseSlika.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!= null) {
                    urlProfilke = dataSnapshot.getValue().toString();
                }
                if(urlProfilke!=null)
                    Glide.with(getApplication()).load(urlSlike).into(mProfilka);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
*/
}
