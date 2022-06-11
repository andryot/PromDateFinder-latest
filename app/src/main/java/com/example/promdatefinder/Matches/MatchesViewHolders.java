package com.example.promdatefinder.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.promdatefinder.Chat.ChatActivity;
import com.example.promdatefinder.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId,mMatchName;
    public ImageView mMatchImage, mStatus;
    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = itemView.findViewById(R.id.Matchid);
        mMatchName = itemView.findViewById(R.id.MatchName);

        mMatchImage = itemView.findViewById(R.id.MatchImage);
        mStatus = itemView.findViewById(R.id.matchesStatus);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
