package com.internetwarz.basketballrush;

public interface PlayServices
{
    public void signIn();
    public void signOut();
    public void rateGame();
    public void unlockAchievement(int score, String gameType);
    public void gamesPlayedAchievements(String gameType,int score);
    public void submitScore(int highScore, String gameType);
    public void showAchievement();
    public void showScore();
    public boolean isSignedIn();
}