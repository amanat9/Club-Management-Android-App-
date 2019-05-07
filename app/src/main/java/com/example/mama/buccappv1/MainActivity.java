package com.example.mama.buccappv1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private EditText eTName;
    private EditText eTBRACid;
   // private DatabaseReference databaseReference;



    private TextView textViewSignin;

    private ProgressDialog progressDialog;


    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Registration Page");
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), AppFirstPage.class));
        }

        firebaseAuth= firebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        //databaseReference=FirebaseDatabase.getInstance().getReference();


        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        eTName=findViewById(R.id.eTName);
        eTBRACid=findViewById(R.id.eTBRACid);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){


         // put in restrictions here
        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        String uname = eTName.getText().toString().trim();
        String id = eTBRACid.getText().toString().trim();
        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email) ||TextUtils.isEmpty(uname)|| TextUtils.isEmpty(id) ){
            Toast.makeText(this,"Please fill up all fields",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
///////////////////////////////////////////////register ///////////////////////////////////////////////////////////////
        //creating a new user











        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            saveInfoToDB();
                            finish();
                            startActivity(new Intent(getApplicationContext(), AppFirstPage.class));
                        }else{
                            //display some message here
                            Toast.makeText(MainActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void saveInfoToDB()
    {
        String uname = eTName.getText().toString().trim();
        String id = eTBRACid.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        //User user = new User(name,id,email,password);
        String user_id=firebaseAuth.getCurrentUser().getUid();
        String pic="https://firebasestorage.googleapis.com/v0/b/buccapp-6d13f.appspot.com/o/defaultuser.png?alt=media&token=b9cf8747-2c33-4cc4-9dd1-f4ab9bc7f37b";


        Map<String,Object> postMap=new HashMap<>();
        postMap.put("name",uname);

        postMap.put("id",id);
        postMap.put("picture",pic);
        postMap.put("admin","0");

        firebaseFirestore.collection("Users").document(user_id).set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(MainActivity.this,"complete",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            registerUser();
        }


        if(view == textViewSignin){
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, Login_page.class));
        }

    }
}