package com.example.promdatefinder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;


// DISPLAY PROFILA NASPROTNEGA SPOLA

/**
 * KLIČE SE IZ MAIN ACTIVITY
 * ČUDEŽNO DELUJE ?!
 */
public class ProfileActvity extends AppCompatActivity {

    private String userId;
    private EditText mNameField, mAboutField, mSchoolField;
    private TextView mImeView;
    private ImageView mBackArrow; // BACK V MAIN ACTIVITY

    private ImageView mProfileImage, mInstabtn;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase, usersDb;

    private String name, phone, profileImageUrl, school, about, insta;
    SliderLayout sliderLayout;
    @Override
    // ON CREATE
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // LAYOUT IZ activity_profile.xml
        usersDb = FirebaseDatabase.getInstance().getReference().child("Uporabniki"); // REFERENCA NA TABELO UPORABNIKI DA JE KASNEJE KRAJŠA KODA

        /**
         * ID uporabnika, katerega bo prikazan profil
         */
        userId = getIntent().getExtras().getString("profileId");

        /**
         * GUMBI
         */
        mNameField = findViewById(R.id.profileName);
        mAboutField = findViewById(R.id.about);
        mSchoolField = findViewById(R.id.school);
        mProfileImage = findViewById(R.id.profileImage);
        // mChangePhoto = (TextView) findViewById(R.id.changePhoto);
        mImeView = findViewById(R.id.ime);
        mBackArrow = findViewById(R.id.loginBackArrow);
        mInstabtn = findViewById(R.id.insta);
        //mCheckMark = (ImageView) findViewById(R.id.checkMark);


        getUserInfo(); // KLIC FUNKCIJE KI ZAPOLNE VSA POLJA
        mInstabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!insta.isEmpty()) {
                    Uri uri = Uri.parse("http://instagram.com/_u/" + insta);
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://instagram.com/" + insta)));
                    }
                }
                else
                    Toast.makeText(ProfileActvity.this, "Oseba ni dodala Instagram profila",Toast.LENGTH_SHORT).show();

            }
        });

        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(10000);
        setSliderViews();

        //set scroll delay in seconds :

    }
    /*
    protected void onRestart()
    {
        super.onRestart();

    }
    */
    /*
    protected void onStart()
    {
        super.onStart();
        getUserInfo();
        setSliderViews();

    }
*/
    private void getUserInfo() {
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userId).child("ime").getValue()!= null)
                name = dataSnapshot.child(userId).child("ime").getValue().toString();
                if(dataSnapshot.child(userId).child("school").getValue()!= null)
                school = dataSnapshot.child(userId).child("school").getValue().toString();
                if(dataSnapshot.child(userId).child("about").getValue()!= null)
                about= dataSnapshot.child(userId).child("about").getValue().toString();
                if(dataSnapshot.child(userId).child("profileImageUrl").getValue()!= null)
                profileImageUrl= dataSnapshot.child(userId).child("profileImageUrl").getValue().toString();
                if(dataSnapshot.child(userId).child("instagram").getValue()!= null)
                    insta = dataSnapshot.child(userId).child("instagram").getValue().toString();

                if (name != null) {
                    mNameField.setText(name);
                    mImeView.setText(name);
                }
                if (school != null) {
                    mSchoolField.setText(school);
                }
                if (about != null) {

                    mAboutField.setText(about);
                }
               // Glide.clear(mProfileImage);
                //Glide.with(getApplication()).clear((mProfileImage));
               // Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goBack(View view) { //GUMB ZA NAZAJ V MAIN ACTIVITY
        finish();
        return;
    }

    private void setSliderViews() {

        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SliderView sliderView = new SliderView(ProfileActvity.this);
                if(dataSnapshot.child(userId).child("profileImageUrl").getValue()!= null)
                    profileImageUrl = dataSnapshot.child(userId).child("profileImageUrl").getValue().toString();
                sliderView.setImageUrl(profileImageUrl);
                sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                    @Override
                    public void onSliderClick(SliderView sliderView) {
                    }
                });

                //at last add this view in your layout :
                sliderLayout.addSliderView(sliderView);


                DataSnapshot dataSnapshot2 = dataSnapshot.child(userId).child("photos");
                //sliderView.setImageUrl("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/hbz-emrata-jacket-index-1523470004.jpg");

                for (int i = 2; i < dataSnapshot2.getChildrenCount()+2; i++) {
                    SliderView sliderView2 = new SliderView(ProfileActvity.this);
                    profileImageUrl = dataSnapshot2.child(Integer.toString(i)).getValue().toString();
                    sliderView2.setImageUrl(profileImageUrl);
                    sliderView2.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                    sliderView2.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(SliderView sliderView) {
                        }
                    });

                    //at last add this view in your layout :
                    sliderLayout.addSliderView(sliderView2);

                   // if(sliderLayout.getChildCount()>7)
                      //  sliderLayout.removeViews(5, sliderLayout.getChildCount());

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}

