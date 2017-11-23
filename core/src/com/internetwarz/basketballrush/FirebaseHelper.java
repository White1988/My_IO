package com.internetwarz.basketballrush;


import com.internetwarz.basketballrush.model.UserScore;

import de.tomgrill.gdxfirebase.core.GDXFirebase;
import de.tomgrill.gdxfirebase.core.database.DatabaseReference;

import static com.internetwarz.basketballrush.Constants.STATS_TABLE;

public  class FirebaseHelper
{

    private static String playerId; // google acc id in android case, random email in desctop case

    public static void setPlayerId(String id)
    {
        playerId = id;
    }


    //String playerId = Games.Players.getCurrentPlayerId(getApiClient());

    public static void  getCurrentUserScore()
    {
        if (playerId == null) throw new IllegalStateException("User id is not set");
        DatabaseReference reference = GDXFirebase.FirebaseDatabase().getReference(STATS_TABLE);


        //String key = reference.push().getKey();
       // reference.child(key).setValue("some value");

    }

    public static void  saveUserScore(UserScore s)
    {
        DatabaseReference reference = GDXFirebase.FirebaseDatabase().getReference(STATS_TABLE);
        reference.child(s.getId()).setValue(s);
    }
}
