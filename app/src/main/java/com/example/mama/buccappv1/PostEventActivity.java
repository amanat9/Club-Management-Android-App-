package com.example.mama.buccappv1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostEventActivity extends AppCompatActivity {

    private EditText eventEditText;
    private Button EventPostButton;
    private ImageView uploadEventPhoto;
    private Uri postImageUri=null;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private EditText editTextTitlePost;
    private String longitude;
    private String latitude ;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_event);
        getSupportActionBar().setTitle("Post Event");

        storageReference=FirebaseStorage.getInstance().getReference();
                firebaseFirestore=FirebaseFirestore.getInstance();
        EventPostButton=findViewById(R.id.setup_btn);
        eventEditText = findViewById(R.id.editTextEvent);
        uploadEventPhoto=findViewById(R.id.uploadEventPhoto);
        editTextTitlePost=findViewById(R.id.editTextTitlePost);
        firebaseAuth=FirebaseAuth.getInstance();
        longitude="90.4072";latitude  ="23.7802";


        currentUserId= firebaseAuth.getCurrentUser().getUid();
        uploadEventPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(PostEventActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(PostEventActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(PostEventActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });


        EventPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String desc =eventEditText.getText().toString();
               // Toast.makeText(PostEventActivity.this,desc,Toast.LENGTH_LONG).show();
                if(!TextUtils.isEmpty(desc) && postImageUri!=null)
                {

                    //Toast.makeText(PostEventActivity.this,desc,Toast.LENGTH_LONG).show();
                    String randomName= UUID.randomUUID().toString();
                    final StorageReference filepath= storageReference.child("Event_images").child(randomName+".jpg");
                    filepath.putFile(postImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return filepath.getDownloadUrl();
                    }
                   }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {



                                String downloadUri= task.getResult().toString();
                                Map<String,Object> postMap=new HashMap<>();
                                postMap.put("image",downloadUri);
                        //String[] result = desc.split("\n", 2);
                        String title=editTextTitlePost.getText().toString();
                        postMap.put("title",title);

                       // if(result.length>1) {}
                            postMap.put("description", desc.toString());

                        /*else
                        {
                            postMap.put("description", "");
                        }*/

                     

                        Date currentTime = Calendar.getInstance().getTime();
                        String time=currentTime.toString();

                                postMap.put("time",time);

                                postMap.put("user_id",currentUserId);
                                postMap.put("timeStamp",FieldValue.serverTimestamp());
                                postMap.put("longitude",longitude);
                                postMap.put("latitude",latitude);

                                //post to algolia
                        List<JSONObject> array = new ArrayList<JSONObject>();
                        Client client = new Client("0PFP39GNSP", "e3071a92bcc82e216d147f25b4c27afb");
                        Index index= client.getIndex("Events");
                        try {
                            array.add(
                                    new JSONObject().put("title", title)
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        index.addObjectsAsync(new JSONArray(array), null);

                        //post to firestore
                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(PostEventActivity.this,"posted",Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(getApplicationContext(), AppFirstPage.class));
                                    finish();
                                }
                                else{
                                    String FireStorecry=task.getException().getMessage();

                                    Toast.makeText(PostEventActivity.this,FireStorecry,Toast.LENGTH_LONG).show();
                                }

                            }

                        });

                            }



                    });
                }

            }
        });



    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(16, 9)
                .start(PostEventActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                uploadEventPhoto.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }


}
