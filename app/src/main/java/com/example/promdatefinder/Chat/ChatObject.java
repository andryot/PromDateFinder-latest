package com.example.promdatefinder.Chat;

public class ChatObject {
    private String message;
    private Boolean currentUser;
    private String userForImage;

    public ChatObject(String message, Boolean currentUser, String userForImage)
    {
    this.message = message;
    this.currentUser = currentUser;
    this.userForImage = userForImage;
    }

    public String getMessage ()
    {
        return message;
    }
    public String getUserForImage ()
    {
        return userForImage;
    }
    public void setMessage (String userId)
    {
        this.message = message;
    }

    public Boolean getCurrentUser ()
    {
        return currentUser;
    }
    public void setCurrentUser (Boolean currentUser)
    {
        this.currentUser = currentUser;
    }
}
