package com.example.mama.buccappv1;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class uEId {
    @Exclude
    public String uEventId;

    public <T extends uEId>T withId(@NonNull final String id)
    {
        this.uEventId=id;
        return (T)this;
    }


}
