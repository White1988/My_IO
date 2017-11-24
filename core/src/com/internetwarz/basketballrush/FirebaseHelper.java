package com.internetwarz.basketballrush;


import com.internetwarz.basketballrush.model.UserScore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.tomgrill.gdxfirebase.core.GDXFirebase;
import de.tomgrill.gdxfirebase.core.database.DataSnapshot;
import de.tomgrill.gdxfirebase.core.database.DatabaseError;
import de.tomgrill.gdxfirebase.core.database.DatabaseReference;
import de.tomgrill.gdxfirebase.core.database.ValueEventListener;

import static com.internetwarz.basketballrush.Constants.EASY_MODE;
import static com.internetwarz.basketballrush.Constants.HARD_MODE;
import static com.internetwarz.basketballrush.Constants.MEDIUM_MODE;
import static com.internetwarz.basketballrush.Constants.STATS_TABLE;

public  class FirebaseHelper
{

    private static String playerId; // google acc id in android case, random email in desctop case
    DatabaseReference reference;
    //public ArrayList<HashMap> list = new ArrayList<HashMap>();
    ArrayList<HashMap> listEasy = new ArrayList<HashMap>();//List of records level = 3, gameCount = 4;
    ArrayList<HashMap> listMedium = new ArrayList<HashMap>();//List of records level = 3, gameCount = 4;
    ArrayList<HashMap> listHard = new ArrayList<HashMap>();//List of records level = 3, gameCount = 4;

    public FirebaseHelper(String mail) {
        reference = GDXFirebase.FirebaseDatabase().getReference(STATS_TABLE);
        mail = mail.replaceAll("@", "_").replace(".", "_");
        playerId = mail;
    }

    public ArrayList<HashMap> getListEasy() {
        return listEasy;
    }

    public ArrayList<HashMap> getListMedium() {
        return listMedium;
    }

    public ArrayList<HashMap> getListHard() {
        return listHard;
    }

    public void dataInit() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    listEasy = (ArrayList) dataSnapshot.child(playerId).child(EASY_MODE).getValue();
                    for (HashMap map: listEasy) {
                        System.out.println(map.entrySet());
                    }
                    System.out.println("Easy size: " + listEasy.size());
                }
                catch (NullPointerException e) {
                    HashMap<String, Integer> start = new HashMap<String, Integer>();
                    start.put("level", 1);
                    start.put("gamesCount", 0);
                    listEasy = new ArrayList<HashMap>();
                    listEasy.add(start);
                    reference.child(playerId).child("Easy").setValue(listEasy);
                }

                try {
                    listMedium = (ArrayList) dataSnapshot.child(playerId).child(MEDIUM_MODE).getValue();
                    for (HashMap map: listMedium) {
                        System.out.println(map.entrySet());
                    }
                    System.out.println("Medium size: " + listMedium.size());
                }
                catch (NullPointerException e) {
                    HashMap<String, Integer> start = new HashMap<String, Integer>();
                    start.put("level", 1);
                    start.put("gamesCount", 0);
                    listMedium = new ArrayList<HashMap>();
                    listMedium.add(start);
                    reference.child(playerId).child("Medium").setValue(listMedium);
                }

                try {
                    listHard = (ArrayList) dataSnapshot.child(playerId).child(HARD_MODE).getValue();
                    for (HashMap map : listHard) {
                        System.out.println(map.entrySet());
                    }
                    System.out.println("Hard size: " + listHard.size());
                }
                catch (NullPointerException e) {
                    HashMap<String, Integer> start = new HashMap<String, Integer>();
                    start.put("level", 1);
                    start.put("gamesCount", 0);
                    listHard = new ArrayList<HashMap>();
                    listHard.add(start);
                    reference.child(playerId).child("Hard").setValue(listHard);
                }
                sortData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
    }

    private void sortData() {
        Comparator<HashMap> comparator = new Comparator<HashMap>() {
            @Override
            public int compare(HashMap hashMap, HashMap t1) {
                return ((Number)t1.get("level")).intValue() - ((Number)hashMap.get("level")).intValue();
            }
        };
        Collections.sort(listEasy, comparator);
        Collections.sort(listMedium, comparator);
        Collections.sort(listHard, comparator);
        System.out.println("sorted");
    }

    /*public ArrayList getData(final String difficulty) {
        list = new ArrayList<HashMap>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    list = (ArrayList) dataSnapshot.child(playerId).child(difficulty).getValue();
                    for (HashMap map: list) {
                        System.out.println(map.entrySet());
                    }
                    System.out.println("Easy size: " + list.size());
                }
                catch (NullPointerException e) {
                    HashMap<String, Integer> start = new HashMap<String, Integer>();
                    start.put("level", 1);
                    start.put("gamesCount", 0);
                    list = new ArrayList<HashMap>();
                    list.add(start);
                    reference.child(playerId).child(difficulty).setValue(list);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("List size: " + list.size());
        return list;
    }*/

    public void updateData(String difficult, int level) {
        //getData(difficult);
        boolean isExist = false;
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        if(difficult == EASY_MODE) {
            System.out.println(listEasy.size());
            for(HashMap<String, Long> map: listEasy) {
                if(map.get("level") == level) {
                    isExist = true;
                    map.put("gamesCount", map.get("gamesCount") + 1);
                    System.out.println(map.get("gamesCount"));
                    reference.child(playerId).child("Hard").setValue(listEasy);
                    break;
                }
            }
        }
        else if(difficult == MEDIUM_MODE) {
            System.out.println(listMedium.size());
            for(HashMap<String, Long> map: listMedium) {
                if(map.get("level") == level) {
                    isExist = true;
                    map.put("gamesCount", map.get("gamesCount") + 1);
                    System.out.println(map.get("gamesCount"));
                    reference.child(playerId).child("Hard").setValue(listMedium);
                    break;
                }
            }
        }
        else if(difficult == HARD_MODE) {
            System.out.println(listHard.size());
            for(HashMap<String, Long> map: listHard) {
                if(map.get("level") == level) {
                    isExist = true;
                    map.put("gamesCount", map.get("gamesCount") + 1);
                    System.out.println(map.get("gamesCount"));
                    reference.child(playerId).child("Hard").setValue(listHard);
                    break;
                }
            }
        }

        if(!isExist) {
            HashMap<String, Integer> start = new HashMap<String, Integer>();
            start.put("level", level);
            start.put("gamesCount", 1);
            if(difficult.equals(EASY_MODE)){
                listEasy.add(start);
                reference.child(playerId).child(EASY_MODE).setValue(listEasy);
            }
            else if(difficult.equals(MEDIUM_MODE)) {
                listMedium.add(start);
                reference.child(playerId).child(MEDIUM_MODE).setValue(listMedium);
            }
            else if(difficult.equals(HARD_MODE)) {
                listHard.add(start);
                reference.child(playerId).child(HARD_MODE).setValue(listHard);
            }
        }
        System.out.println("End");
    }

    public static void setPlayerId(String id)
    {
        id.replaceAll("@", "_").replace(".", "_");
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

    public static void  saveUserScore(UserScore s, String id, String difficulty, ArrayList<UserScore> results)
    {
        id = id.replaceAll("@", "_").replace(".", "_");
        DatabaseReference reference = GDXFirebase.FirebaseDatabase().getReference(STATS_TABLE);
        reference.child(id).child(difficulty).setValue(results);
    }

    public void updateList(String difficulty, ArrayList data) {
        reference.child(playerId).child("Hard").setValue(data);
    }
}
