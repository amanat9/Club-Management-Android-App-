package com.example.mama.buccappv1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UEAdapter extends RecyclerView.Adapter<UEAdapter.ViewHolder>

{
    List<UE> ueventList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    String currentUserId;
    Index index;
    String lon ;
    String lat;
    Intent imap;


    public UEAdapter(List<UE> ueventList)
    {
        this.ueventList = ueventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upcomingevent_list_item, viewGroup,false);
        context=viewGroup.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final String eventId = ueventList.get(i).uEventId;
        currentUserId= firebaseAuth.getCurrentUser().getUid();

        String descData= ueventList.get(i).getDescription();
        viewHolder.setDescView(descData);

        String image_url= ueventList.get(i).getImage();
        viewHolder.setEventImage(image_url);


        String time= ueventList.get(i).getTime();
        final String title= ueventList.get(i).getTitle();

        viewHolder.setTimeandTitle(time,title);
        //attendance
        viewHolder.attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               imap =new Intent(context,MapsActivity.class);
                DocumentReference dr=firebaseFirestore.collection("Posts").document(""+eventId);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            lon=document.getString("longitude");
                            lat=document.getString("latitude");
                            Log.e("bhul","mama1"+lat +" "+lon );
                            imap.putExtra("lon",lon);
                            imap.putExtra("lat",lat);
                            context.startActivity(imap);

                        }
                    }

                });

                Log.e("bhul","mama2"+lat +" "+lon );



            }
        });

        viewHolder.textViewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imap =new Intent(context,MapsActivity.class);
                DocumentReference dr=firebaseFirestore.collection("Posts").document(""+eventId);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            lon=document.getString("longitude");
                            lat=document.getString("latitude");
                            Log.e("bhul","mama1"+lat +" "+lon );
                            imap.putExtra("lon",lon);
                            imap.putExtra("lat",lat);
                            context.startActivity(imap);

                        }
                    }

                });

                Log.e("bhul","mama2"+lat +" "+lon );



            }
        });



        //delete on click code
        viewHolder.deletePostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef =  firebaseFirestore.collection("Users").document(""+currentUserId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String admin= document.getString("admin");
                                if(admin.equals("0"))
                                {
                                    Toast.makeText(context,"you have to be admin to perform this action",Toast.LENGTH_LONG).show();
                                }
                                else
                                {

                                    //admin deletes item from firestore
                                    firebaseFirestore.collection("Posts").document(""+eventId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Intent i =new Intent(context,AppFirstPage.class);
                                                    //i.putExtra("EventId",eventId);

                                                    context.startActivity(i);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Log.w(TAG, "Error deleting document", e);
                                                }
                                            });


                                    //admin deletes item from Agoila
                                    Client client = new Client("0PFP39GNSP", "e3071a92bcc82e216d147f25b4c27afb");
                                    index= client.getIndex("Events");
                                    com.algolia.search.saas.Query query = new com.algolia.search.saas.Query(title)
                                            .setAttributesToRetrieve("title")
                                            .setHitsPerPage(50);
                                    index.searchAsync(query, new CompletionHandler() {
                                        @Override
                                        public void requestCompleted(JSONObject content, AlgoliaException error) {
                                            Log.e("BhulAlgo",content.toString());
                                            try {
                                                JSONArray hits = content.getJSONArray("hits");
                                                String deleteItem=null;

                                                for(int i=0; i <hits.length();i++)
                                                {
                                                    JSONObject jsonObject=hits.getJSONObject(i);
                                                    deleteItem=jsonObject.getString("title");
                                                }
                                                index.deleteObjectAsync(deleteItem, null);



                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                }


                            }
                        } else {

                        }
                    }
                });
            }
        });
//delete code ends here

        //comment code
        viewHolder.commentEventimagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,CommentsActivity.class);
                i.putExtra("EventId",eventId);
                context.startActivity(i);


            }
        });
       //comment on textclick same as above
        viewHolder.commentEventTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,CommentsActivity.class);
                i.putExtra("EventId",eventId);
                context.startActivity(i);
            }
        });









        String user_id = ueventList.get(i).getUser_id();
        //User Data will be retrieved here...
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("picture");

                   viewHolder.setUserData(userName, userImage);


                } else {

                    //Firebase Exception

                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return ueventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private View mview;
        private TextView descView;
        private ImageView eventImageView;
        private TextView etime;
        private TextView etitle;
        private ImageView commentEventimagebtn;
        private  ImageView deletePostbutton;
        private TextView commentEventTextview;
        private TextView usernamePost;
        private ImageView imageViewPostUser;
        private ImageView attendance;
        private TextView textViewAttendance;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mview =itemView;
            commentEventimagebtn=mview.findViewById(R.id.commentEventimageView);
            commentEventTextview=mview.findViewById(R.id.commentEventtextView);
            deletePostbutton=mview.findViewById(R.id.deletePostbutton);
            attendance=mview.findViewById(R.id.attendance);
            textViewAttendance=mview.findViewById(R.id.textViewAttendance);

        }

        public void setDescView(String text)
        {
            descView=mview.findViewById(R.id.editTextEventPost);
            descView.setText(text);
        }

        public void setEventImage(String downloadUri)
        {
            eventImageView=mview.findViewById(R.id.eventPostPhoto1);
            Glide.with(context).load(downloadUri).into(eventImageView);

        }

        public void setTimeandTitle(String time,String title)
        {
            etime=mview.findViewById(R.id.time);
            etime.setText(time);
            etitle=mview.findViewById(R.id.titleEvent);
            etitle.setText(title);

        }

        public void setUserData(String userName, String userImage)
        {
            usernamePost=mview.findViewById(R.id.usernamePost);
            usernamePost.setText(userName);
            imageViewPostUser=mview.findViewById(R.id.imageViewUserPost);

            Glide.with(context).load(userImage).into(imageViewPostUser);


        }
    }

}
