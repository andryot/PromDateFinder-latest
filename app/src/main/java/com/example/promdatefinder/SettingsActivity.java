package com.example.promdatefinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * aKTIVNOST SETTINGS
 * UPORABNIK LAHKO SPREMINJA SVOJ PROFIL
 */
public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mAboutField, mSchoolField, mInstaField;
    private TextView mChangePhoto;
    private ImageView mBackArrow, mCheckMark;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;
    private Switch mSwitch;

    private String userId, name, phone, profileImageUrl, school,about, spolUporabnika, insta, visible;
    private Uri resultUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // da se ne odrpe tipkovnica ob štartu aktivitija

        mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.phone);
        mAboutField = findViewById(R.id.about);
        mSchoolField = findViewById(R.id.school);
        mProfileImage = findViewById(R.id.profileImage);
        mChangePhoto = findViewById(R.id.changePhoto);

        mBackArrow = findViewById(R.id.loginBackArrow);
        mCheckMark = findViewById(R.id.checkMark);
        mInstaField = findViewById(R.id.ig);
        mSwitch = findViewById(R.id.showMeSwitch);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(userId);


        getUserInfo();
        //PROFILKA
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                /*
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);

                */
            }
        });

        //LAHKO TUDI KLIKNE NA TEKST DA SPREMENI PROFILKO
        mChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);

               /* Intent intent = new Intent(Intent.);
                intent.setType("image/*");
                startActivityForResult(intent,1);
                */
            }
        });
        //SAVE IN AVTOMATSKA PREUSMERITEV NA MAIN ACTIVITY
        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo(); // FUNKCIJA KI SHRANI DATA V BAZO
            }
        });
        // NE SHRANI AMPAK LE PREUSMERI NAZAJ
            mBackArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    return;
                }
            });

            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    changeShowMe(isChecked);
                   // Toast.makeText(SettingsActivity.this,  )
                }
            });

    }
    private void changeShowMe(final boolean state)
    {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child("visible").getValue() != null)
               {
                   Map newState = new HashMap();

                   if(!state)
                       newState.put("visible", "false");
                   else
                       newState.put("visible", "true");

                   mUserDatabase.updateChildren(newState);

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getUserInfo() { //PRIDOBI ŽE OBSTOJEČE PODATKE UPORABNIKA
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("ime")!= null)
                    {
                        name = map.get("ime").toString();
                        mNameField.setText(name);
                    }
                    if(map.get("phone")!= null)
                    {
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
                    if(map.get("spol")!= null)
                    {
                        spolUporabnika = map.get("spol").toString();
                    }
                    if(map.get("school")!=null)
                    {
                        school = map.get("school").toString();
                        mSchoolField.setText(school);
                    }
                    if(map.get("about")!=null)
                    {
                        about = map.get("about").toString();
                        mAboutField.setText(about);
                    }
                    if(map.get("instagram")!=null)
                    {
                        insta = map.get("instagram").toString();
                        mInstaField.setText(insta);
                    }
                    if(map.get("visible")!=null)
                    {
                        visible = map.get("visible").toString();
                        if (visible.equals("true"))
                            mSwitch.setChecked(true);
                        else
                            mSwitch.setChecked(false);
                    }

                        //Glide.clear(mProfileImage);
                    Glide.with(getApplication()).clear((mProfileImage));
                    if(map.get("profileImageUrl")!= null)
                    {
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl)
                        {
                            /*case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
                                break;
*/
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    // SAVE
    private void saveUserInfo() {
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();
        school = mSchoolField.getText().toString();
        about = mAboutField.getText().toString();
        insta = mInstaField.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("ime", name);
        userInfo.put("phone", phone);
        userInfo.put("school", school);
        userInfo.put("about", about);
        userInfo.put("instagram", insta);


        mUserDatabase.updateChildren(userInfo);

        if (resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos); // SHRANJEVANJE SLIKE
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mUserDatabase.updateChildren(newImage);
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }

            });
        }
        else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                 resultUri = result.getUri();
                mProfileImage.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SettingsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }


       /* super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
        */
    }


    public void goToUploadImages(View view) {
        Intent intent = new Intent(SettingsActivity.this, Upload_photos_Activity.class);
        startActivity(intent);
        return;
    }
}
