package com.example.mama.buccappv1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppFirstPage extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;
    FirebaseFirestore firebaseFirestore;
    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_first_page);

        getSupportActionBar().setTitle("Homepage");
        try{

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        if (firebaseAuth.getCurrentUser() != null) {
            //initializing firebase authentication object


            //if the user is not logged in
            //that means current user will return null
            if (firebaseAuth.getCurrentUser() == null) {
                //closing this activity
                finish();
                //starting login activity
                startActivity(new Intent(this, Login_page.class));
            }

            //getting current user
            FirebaseUser user = firebaseAuth.getCurrentUser();

            //initializing views
            textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
            buttonLogout = (Button) findViewById(R.id.buttonLogout);

            DocumentReference docRef = firebaseFirestore.collection("Users").document("" + currentUserId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            //displaying logged in user name
                            textViewUserEmail.setText("Welcome " + name);
                        }
                    }
                }
            });


            //adding listener to button
            buttonLogout.setOnClickListener(this);
        }


    }catch (Exception e)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), Login_page.class));
        }




    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, Login_page.class));
        }
    }


    public void onUpcomingEvent(View view) {

        startActivity(new Intent(getApplicationContext(), UEActivity.class));

    }


    public void onPostEvent(View view) {
        DocumentReference docRef =  firebaseFirestore.collection("Users").document(""+currentUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String admin = document.getString("admin");
                        if (admin.equals("0")) {
                            Toast.makeText(getApplicationContext(), "you have to be admin to perform this action", Toast.LENGTH_LONG).show();
                        } else {
                            startActivity(new Intent(getApplicationContext(), PostEventActivity.class));

                        }


                    }
                } else {

                }


            }
        });
    }

    public void onSettings(View view) {

        startActivity(new Intent(getApplicationContext(), SetupActivity.class));

    }

    public void onSearch(View view) {

        startActivity(new Intent(getApplicationContext(), SearchActivity.class));

    }


}
