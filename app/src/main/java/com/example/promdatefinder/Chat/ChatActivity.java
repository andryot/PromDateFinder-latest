package com.example.promdatefinder.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.promdatefinder.ProfileActvity;
import com.example.promdatefinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CHAT AKTIVNOST
 * BASIC CHAT
 * POTREBEN UI REDESIGNA
 * DELAY PRIBLIŽNO 2 SEKUNDI
 */
public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManeger;
    private String currentUporabnikId, matchId, chatId;

    private EditText mSendEditText;
    private TextView mIme;
    private ImageView mSendButton;
    private ImageView mProfilka,mSlikca,mStatus, mShowMore;
    private String urlSlike, ime;
    private NestedScrollView mScrollView;

    DatabaseReference mDatabaseUser, mDatabaseChat,mDatabaseUporabnik;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        matchId = getIntent().getExtras().getString("matchId");
        currentUporabnikId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(currentUporabnikId).child("povezave").child("matches").child(matchId).child("ChatId");
        mDatabaseUporabnik = FirebaseDatabase.getInstance().getReference().child("Uporabniki");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");


        //mScrollView  = findViewById(R.id.chat_scroll_view);

        mRecyclerView = findViewById(R.id.chat_recyclerView);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(false);

        mChatLayoutManeger = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManeger);
        //mRecyclerView.scrollToPosition(10);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);

        mShowMore = findViewById(R.id.showMore);
        mProfilka = findViewById(R.id.profilna);
        mSlikca = findViewById(R.id.slikca);
        mStatus = findViewById(R.id.status);
        mIme = findViewById(R.id.chat_ime);
        //mScrollView = findViewById(R.id.chat_scroll_view);

       // mScrollView.scrollTo(0,0);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        getChatId();
        naloziprofilko();
        //mScrollView.fullScroll(View.FOCUS_DOWN);

        mShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ChatActivity.this, mShowMore);
                popupMenu.getMenuInflater().inflate(R.menu.remove_match_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().toString().equals("si prepričan?"))
                        {
                           mDatabaseChat.removeValue();
                            mDatabaseUporabnik.child(currentUporabnikId).child("povezave").child("matches").child(matchId).removeValue();
                            mDatabaseUporabnik.child(matchId).child("povezave").child("matches").child(currentUporabnikId).removeValue();

                            finish();
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });


    }
    void naloziprofilko()
    {
        mDatabaseUporabnik.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(matchId).child("profileImageUrl").getValue()!= null) {
                    urlSlike = dataSnapshot.child(matchId).child("profileImageUrl").getValue().toString();
                }
                if(urlSlike!=null) {
                    Glide.with(getApplication()).load(urlSlike).into(mProfilka);
                    if(dataSnapshot.child(matchId).child("status").getValue() != null)
                    {
                        if (dataSnapshot.child(matchId).child("status").getValue().equals("online"))
                            mStatus.setBackgroundResource(R.drawable.ic_online);
                        else
                            mStatus.setBackgroundResource(R.drawable.ic_offline);
                    }

                }
                if(dataSnapshot.child(matchId).child("ime").getValue()!= null) {
                    ime = dataSnapshot.child(matchId).child("ime").getValue().toString();
                    mIme.setText(ime);
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty())
        {
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUporabnikId);
            newMessage.put("text", sendMessageText);

            newMessageDb.setValue(newMessage);

        }
        mSendEditText.setText(null);
        mChatLayoutManeger.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);


}


    private void getChatId ()
    {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    String message = null;
                    String createdByUser = null;

                    if (dataSnapshot.child("text").getValue()!=null)
                    {
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("createdByUser").getValue()!=null)
                    {
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if (message!=null && createdByUser!=null)
                    {
                        Boolean currentUserBoolean = false;
                        if(createdByUser.equals(currentUporabnikId))
                        {
                            currentUserBoolean = true;

                        }


                        ChatObject newMessage = new ChatObject(message,currentUserBoolean, matchId);
                        //ChatObject newMessage2 = new ChatObject(matchId,true);
                        resultsChat.add(newMessage);
                        //resultsChat.add(newMessage2);
                      //  mChatLayoutManeger.smoothScrollToPosition(mRecyclerView, null,mChatAdapter.getItemCount()-1);
                        mChatAdapter.notifyDataSetChanged();
                        //mScrollView.fullScroll(View.FOCUS_DOWN);
                        mChatLayoutManeger.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);

                    }
                    //mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() -1);
                }
                //mScrollView.fullScroll(View.FOCUS_DOWN);
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

    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();

    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }

    public void nazaj(View view) {
        finish();
        return;
    }

    public void goToProfile(View view) {

        //Toast.makeText(MainActivity.this, profileId, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ChatActivity.this, ProfileActvity.class);
        Bundle b = new Bundle();
        b.putString("profileId", matchId);// ZBUNDLAM ID
        intent.putExtras(b); // ID POŠLEJM ZRAVNE V NOVO AKTIVNOST DA LAHKO TAM PRIKAŽEM USTREZEN PROFIL
        view.getContext().startActivity(intent);
        //String nekii = Integer.toString(stevec);
        //Toast.makeText(MainActivity.this, nekii, Toast.LENGTH_LONG).show();

    }







}
