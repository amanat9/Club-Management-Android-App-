package com.example.mama.buccappv1;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class Testattendance extends AppCompatActivity {


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    String currentUserId;
    String name;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testattendance);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId= firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore=FirebaseFirestore.getInstance();

        addToSheet();
        Intent i =new Intent(getApplicationContext(),UEActivity.class);
        startActivity(i);


    }

    private void addToSheet() {
        Log.e("bhul",currentUserId);

        DocumentReference docRef =  firebaseFirestore.collection("Users").document(""+currentUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                         name= document.getString("name");
                         id=document.getString("id");


                        if(true)
                        {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzRKtDrxp8XwVySVFGFXx5zmCg9hn-k0PcbqOibKoG03gjoI-E/exec",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            //loading.dismiss();
                                            //Toast.makeText(AddItem.this,response,Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent);

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> parmas = new HashMap<>();

                                    //here we pass params
                                    parmas.put("action","addItem");
                                    parmas.put("name",name);
                                    parmas.put("id",id);

                                    return parmas;
                                }
                            };

                            int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

                            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            stringRequest.setRetryPolicy(retryPolicy);

                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                            queue.add(stringRequest);


                        }
                        else
                        {



                        }


                    }
                } else {

                }
            }
        });
    }
}
