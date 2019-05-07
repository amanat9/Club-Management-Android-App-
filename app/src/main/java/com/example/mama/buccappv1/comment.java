package com.example.mama.buccappv1;

import java.util.Date;

public class comment {
    private String message, user_id;
    private Date timeStamp;
public comment(){

    }

    public comment(String message, String user_id, Date timeStamp) {
        this.message = message;
        this.user_id = user_id;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
