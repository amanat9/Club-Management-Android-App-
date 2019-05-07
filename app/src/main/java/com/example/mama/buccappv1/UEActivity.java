package com.example.mama.buccappv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class UEActivity extends AppCompatActivity {

    private RecyclerView EventPostView;
    private List<UE> ueventList;
    private FirebaseFirestore firebaseFirestore;
    private UEAdapter UEAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_event);
        getSupportActionBar().setTitle("Upcoming Event");

        EventPostView=findViewById(R.id.EventPostView);
        ueventList =new ArrayList<>();
        UEAdapter =new UEAdapter(ueventList);
        EventPostView.setLayoutManager(new LinearLayoutManager(this));
        EventPostView.setAdapter(UEAdapter);

        firebaseFirestore=FirebaseFirestore.getInstance();
        Query firstQ= firebaseFirestore.collection("Posts").orderBy("timeStamp",Query.Direction.DESCENDING);

        firstQ.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
             for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
             {
                 if(doc.getType() == DocumentChange.Type.ADDED)
                 {
                     String uEventId=doc.getDocument().getId();
                     UE UE =doc.getDocument().toObject(UE.class).withId(uEventId);
                     ueventList.add(UE);


                     UEAdapter.notifyDataSetChanged();
                 }

             }
            }
        });
    }
}
