package com.example.promdatefinder.Cards;
// CLASS CARDS V KATEREM SO VSI UPORABNIKI NASPROTNEGA SPOLA, KI JIH UPORABNIK ŠE NI OBRAVNAVAL
public class cards {
    private String userId;
    private String ime;
    private String profileImageUrl;
    public cards(String userId, String ime, String profileImageUrl)
    {
        this.userId = userId;
        this.ime = ime;
        this.profileImageUrl=profileImageUrl;
    }

    public String getUserId ()
    {
        return userId;
    }
    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public String getIme ()
    {
        return ime;
    }
    public void setIme (String ime)
    {
        this.ime = ime;
    }


    public String getProfileImageUrl ()
    {
        return profileImageUrl;
    }
    public void setProfileImageUrl (String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }
}
