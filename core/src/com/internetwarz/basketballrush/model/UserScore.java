package com.internetwarz.basketballrush.model;


public class UserScore
{
    //private String email;
   // private String username;
    //private String difficulty;
    private int  level;
    private int  gamesCount;

    public UserScore(int level, int gamesCount) {
        //this.email = email;
        //this.username = username;
        //this.difficulty = difficulty;
        this.level = level;
        this.gamesCount = gamesCount;
    }

    /*public String getId() {
        return email.replaceAll("@", "_").replace(".", "_");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }*/

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGamesCount() {
        return gamesCount;
    }

    public void setGamesCount(int gamesCount) {
        this.gamesCount = gamesCount;
    }
}
