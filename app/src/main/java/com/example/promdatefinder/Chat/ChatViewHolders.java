package com.example.promdatefinder.Chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.promdatefinder.R;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMessage;
    public LinearLayout mContainer;
    public ImageView mProfilka;
    //public String urlProfilke;
    public ChatViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
        mProfilka = itemView.findViewById(R.id.slikca);
        //urlProfilke = "abc";
    }

    @Override
    public void onClick(View v) {

    }
}
