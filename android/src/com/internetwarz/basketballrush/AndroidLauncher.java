package com.internetwarz.basketballrush;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements PlayServices {
    private GameHelper gameHelper;
    private final static int requestCode = 1;
    private final static String AD_ID = "pub-8644762955474796";
    //private final static String AD_ID = "ca-app-pub-3940256099942544/6300978111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        RelativeLayout layout = new RelativeLayout(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View gameView = initializeForView(new Tsar(this), config);
        gameView.setId(View.generateViewId());

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_ID);
        adView.setId(View.generateViewId());
        adView.setBackgroundColor(Color.BLACK);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        RelativeLayout.LayoutParams gameParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(gameView, gameParams);

        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adParams.addRule(RelativeLayout.BELOW, gameView.getId());;
        layout.addView(adView, adParams);

        setContentView(layout);
        //initialize(new Tsar(this), config);

        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(true);
        GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {
                Gdx.app.log("MainActivity", "Log in failed: " + gameHelper.getSignInError() + "."); ;
            }

            @Override
            public void onSignInSucceeded() {
                System.out.println("signed in!!!!!!!");
                FirebaseHelper.isSignIn = true;
                FirebaseHelper.setPlayerId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));
            }
        };
        gameHelper.setup(gameHelperListener);
        System.out.println("FIRST MESSAGE!");
        //FirebaseHelper.setPlayerId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));
        //MobileAds.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult!!!!!!!");
        System.out.println(gameHelper.getApiClient());
    }


    @Override
    public void signIn() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.beginUserInitiatedSignIn();
                }
            });

        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
        }

    }



    @Override
    public void signOut() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.signOut();
                }
            });
            System.out.println("Signed out!");
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
        }
    }

    @Override
    public void rateGame() {
        String str = "https://play.google.com/store/apps/details?id=com.di.devs.tsar";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }


    @Override
    public void gamesPlayedAchievements(String gameType,int score){
        /*if(isSignedIn()){
            if(gameType.equals("two color mode")){
                Games.Achievements.increment(gameHelper.getApiClient(),
                        getString(R.string.achievement_play_25_games_easy_mode),1);
                Games.Achievements.increment(gameHelper.getApiClient(),
                        getString(R.string.achievement_play_50_games_easy_mode),1);
            }
            else if(gameType.equals("three color mode")){
                Games.Achievements.increment(gameHelper.getApiClient(),
                        getString(R.string.achievement_play_50_games_medium_mode),1);
                Games.Achievements.increment(gameHelper.getApiClient(),
                        getString(R.string.achievement_play_100_games_medium_mode),1);
            }
            else if(gameType.equals("four color mode")){
                Games.Achievements.increment(gameHelper.getApiClient(),
                        getString(R.string.achievement_play_100_games_hard_mode),1);
                Games.Achievements.increment(gameHelper.getApiClient(),
                        getString(R.string.achievement_play_200_games_hard_mode),1);
            }
        }*/
    }

    @Override
    public void unlockAchievement(int score, String gameType) {
       /* if (isSignedIn()) {
            if (gameType.equals("two color mode")) {
                if (score == 25) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_25_in_easy));
                } else if (score == 50) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_50_in_easy));
                } else if (score == 100) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_100_in_easy));
                }
            } else if (gameType.equals("three color mode")) {
                if (score == 25) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_25_in_medium));
                } else if (score == 50) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_50_in_medium));
                } else if (score == 100) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_100_in_medium));
                }
            } else if (gameType.equals("four color mode")) {
                if (score == 25) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_25_in_hard));
                } else if (score == 50) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_50_in_hard));
                } else if (score == 80){
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_80_in_hard));
                } else if (score == 100) {
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_score_100_in_hard));
                }
            }
        }*/
    }

    @Override
    public void submitScore(int highScore, String gameType) {
        Gdx.app.log("MainActivity", "submitScore: " + highScore + "."); ;
        if (isSignedIn()) {
            if (gameType.equals(Constants.EASY_MODE)) {
                Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                        getString(R.string.leaderboard_easy), highScore);
            } else if (gameType.equals(Constants.MEDIUM_MODE)) {
                Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                        getString(R.string.leaderboard_medium), highScore);
            } else if (gameType.equals(Constants.HARD_MODE)) {
                Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                        getString(R.string.leaderboard_hard), highScore);
            }
            System.out.println("Score is submitted for " + gameHelper.getApiClient());
        }
    }

    @Override
    public void showAchievement() {
       /* if (isSignedIn()) { no achievements
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
        } else {
            signIn();
        }*/
    }

    @Override
    public void showScore() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), requestCode);
        } else {
            signIn();
        }
    }

    @Override
    public boolean isSignedIn() {
        return gameHelper.isSignedIn();
    }



}
