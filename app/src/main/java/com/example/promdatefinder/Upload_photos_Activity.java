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
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Upload_photos_Activity extends AppCompatActivity {
    private DatabaseReference mUserDatabase, mUserDatabasePicture;
    private String userId, profileImageUrl, imageUrl;
    private FirebaseAuth mAuth;
    private ImageView mSlika1, mSlika2, mSlika3, mSlika4, mSlika5, mSlika6;
    private Uri resultUri;
    String stev;
    Integer stevec;
    ArrayList <String> slike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_images);
        mSlika1 = findViewById(R.id.slika1);
        mSlika2 = findViewById(R.id.slika2);
        mSlika3 = findViewById(R.id.slika3);
        mSlika4 = findViewById(R.id.slika4);
        mSlika5 = findViewById(R.id.slika5);
        mSlika6 = findViewById(R.id.slika6);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(userId);
        mUserDatabasePicture = FirebaseDatabase.getInstance().getReference().child("Uporabniki").child(userId).child("photos");
        getUserInfo();
    }

    private void getUserInfo() { //PRIDOBI ŽE OBSTOJEČE PODATKE UPORABNIKA
        mUserDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0)
                {
                    DataSnapshot dataSnapshot2 = dataSnapshot.child("photos");

                    slike = new ArrayList<>();
                    for (int i = 2; i <dataSnapshot2.getChildrenCount()+2 ; i++)
                    {
                        slike.add(String.valueOf(dataSnapshot2.child(Integer.toString(i)).getValue()));
                    }

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    //Glide.clear(mSlika1);
                    Glide.with(getApplication()).clear((mSlika1));
                    if (map.get("profileImageUrl") != null) {
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl) {
                            /*case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
                                break;
*/
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mSlika1);
                                break;
                        }
                    }
                    if (slike.size()!=0)
                    {
                        if (slike.get(0) != null) {
                            imageUrl = slike.get(0);
                            Glide.with(getApplication()).load(imageUrl).into(mSlika2);
                        }
                        if(slike.size()>1) {
                            if (slike.get(1) != null) {
                                imageUrl = slike.get(1);
                                Glide.with(getApplication()).load(imageUrl).into(mSlika3);
                            }
                        }
                        if(slike.size()>2){
                            if (slike.get(2) != null) {
                                imageUrl = slike.get(2);
                                Glide.with(getApplication()).load(imageUrl).into(mSlika4);
                            }
                        }
                        if(slike.size()>3) {
                            if (slike.get(3) != null) {
                                imageUrl = slike.get(3);
                                Glide.with(getApplication()).load(imageUrl).into(mSlika5);
                            }
                        }
                        if(slike.size()>4) {
                            if (slike.get(4) != null) {
                                imageUrl = slike.get(4);
                                Glide.with(getApplication()).load(imageUrl).into(mSlika6);
                            }
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goBack(View view) {
        finish();
        return;
    }
/*
    public void Slika1Clicked(View view) {
        stevec =1;

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Upload_photos_Activity.this);
    }
*/
    public void Slika2Clicked(View view) {
        stevec = 2;
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Upload_photos_Activity.this);
    }

    public void Slika3Clicked(View view) {
        stevec = 3;
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Upload_photos_Activity.this);
    }

    public void Slika4Clicked(View view) {
        stevec = 4;

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Upload_photos_Activity.this);
    }

    public void Slika5Clicked(View view) {
        stevec = 5;
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Upload_photos_Activity.this);
    }

    public void Slika6Clicked(View view) {
        stevec =6;
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Upload_photos_Activity.this);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                resultUri = result.getUri();
                for (int i = 0; i <1; i++) {
                     if(stevec==2) {
                         mSlika2.setImageURI(resultUri);
                     break;
                     }

                    else if(stevec==3)
                    {
                        if (mSlika2.getDrawable() != null) {
                            mSlika3.setImageURI(resultUri);
                            stevec = 3;
                            break;

                        }
                            else{
                            mSlika2.setImageURI(resultUri);
                            stevec = 2;
                            break;
                            }

                    }

                    else if(stevec==4) {
                        if (mSlika2.getDrawable() != null && mSlika3.getDrawable() != null) {
                            mSlika4.setImageURI(resultUri);
                            break;
                        }
                        else if (mSlika2.getDrawable() != null) {
                            mSlika3.setImageURI(resultUri);
                            stevec = 3;
                            break;
                        } else {
                            mSlika2.setImageURI(resultUri);
                            stevec = 2;
                            break;
                        }
                    }

                    else if(stevec==5) {
                        if (mSlika2.getDrawable() != null && mSlika3.getDrawable() != null && mSlika4.getDrawable() != null) {
                            mSlika5.setImageURI(resultUri);
                            break;
                        }
                        else if (mSlika2.getDrawable() != null) {
                            if (mSlika3.getDrawable() != null) {
                                mSlika4.setImageURI(resultUri);
                                stevec = 4;
                                break;
                            } else {
                                mSlika3.setImageURI(resultUri);
                                stevec = 3;
                                break;
                            }
                        }
                        else {
                            mSlika2.setImageURI(resultUri);
                            stevec = 2;
                            break;
                        }
                    }

                    else if(stevec==6) {
                        if (mSlika2.getDrawable() != null && mSlika3.getDrawable() != null && mSlika4.getDrawable() != null && mSlika5.getDrawable() != null) {
                            mSlika6.setImageURI(resultUri);
                            break;
                        }
                        else if (mSlika2.getDrawable() != null) {
                            if (mSlika3.getDrawable() != null) {
                                if (mSlika4.getDrawable() != null) {
                                    mSlika5.setImageURI(resultUri);
                                    stevec = 5;
                                    break;
                                } else {
                                    mSlika4.setImageURI(resultUri);
                                    stevec = 4;
                                    break;
                                }
                            } else {
                                mSlika3.setImageURI(resultUri);
                                stevec = 3;
                            }
                        }
                        else {
                            mSlika2.setImageURI(resultUri);
                            stevec = 2;
                            break;
                        }
                    }
                }

                if (resultUri != null) {
                     stev = Integer.toString(stevec);
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("userImages").child(userId).child(stev);
                    Bitmap bitmap = null;
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos); // SHRANJEVANJE SLIKE
                    byte[] data1 = baos.toByteArray();
                    UploadTask uploadTask = filePath.putBytes(data1);
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
                                    newImage.put(stev, uri.toString());
                                    mUserDatabasePicture.updateChildren(newImage);
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

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(Upload_photos_Activity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
