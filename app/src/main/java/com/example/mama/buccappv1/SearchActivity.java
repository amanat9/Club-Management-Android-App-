package com.example.mama.buccappv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class SearchActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    //FirebaseAuth firebaseAuth;
    List<JSONObject> array;
    Index index;
    EditText searchEditText;
    ListView listViewForSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setTitle("Search");
        searchEditText = findViewById(R.id.searchEditText);
        listViewForSearch= findViewById(R.id.listViewForSearch);
        final List<String> list = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter;

        Client client = new Client("0PFP39GNSP", "e3071a92bcc82e216d147f25b4c27afb");
         index= client.getIndex("Events");

        firebaseFirestore=FirebaseFirestore.getInstance();
        Query firstQ= firebaseFirestore.collection("Posts").orderBy("timeStamp",Query.Direction.DESCENDING);
        boolean f=false;
        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
        listViewForSearch.setAdapter(arrayAdapter);
        array= new ArrayList<JSONObject>();
        firstQ.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                {
                    if(doc.getType() == DocumentChange.Type.ADDED)
                    {
                        //String uEventId=doc.getDocument().getId();
                        String title=doc.getDocument().getString("title");
                        Log.e("bhul",title);
                        list.add(title);
                        arrayAdapter.notifyDataSetChanged();

                        //UeventList.add(uEvent);
//                        try {
//                            array.add(
//                                    new JSONObject().put("title", title)
//                            );
//                            Log.e("bhul",title+"2");
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }


                    }

                }
//                Log.e("bhul",array.toString()+"3");
//                index.addObjectsAsync(new JSONArray(array), null);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                com.algolia.search.saas.Query query = new com.algolia.search.saas.Query(s.toString())
                        .setAttributesToRetrieve("title")
                        .setHitsPerPage(50);
                index.searchAsync(query, new CompletionHandler() {
                    @Override
                    public void requestCompleted(JSONObject content, AlgoliaException error) {
                        Log.e("BhulAlgo",content.toString());
                        try {
                            JSONArray hits = content.getJSONArray("hits");
                            list.clear();
                            //List list=new ArrayList<>();

                            for(int i=0; i <hits.length();i++)
                            {
                                JSONObject jsonObject=hits.getJSONObject(i);
                                String title=jsonObject.getString("title");
                                list.add(title);
                            }
                            arrayAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });





    }
}
