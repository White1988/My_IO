package com.internetwarz.basketballrush;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.example.games.basegameutils.GameHelper;

import java.util.ArrayList;

public class AndroidLauncher extends AndroidApplication implements PlayServices {
    private GameHelper gameHelper;
    private final static int requestCode = 1;
    private final static String AD_ID = "pub-8644762955474796";
    //private final static String AD_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final String TAG = "AndroidLauncher";
    TurnBasedAndroid turnBasedAndroid = new TurnBasedAndroid(this); // !!VERY important that we are using TurnBasedAndroid instead TurnBasedService
    private AlertDialog mAlertDialog;
    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        RelativeLayout layout = new RelativeLayout(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View gameView = initializeForView(new Xintuition(this, turnBasedAndroid), config);
        gameView.setId(R.id.auto);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_ID);
        adView.setId(R.id.auto);
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
        //initialize(new Xintuition(this), config);

        Log.d(TAG, "mGoogleSignInClient ");
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Log.d(TAG, "after mGoogleSignInClient ");

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
    protected void onResume() {
        super.onResume();
        signInSilently();
    }

    public void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            onDisconnected();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Gdx.app.log(TAG,"onStop" );
    }

    // For our intents
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;
    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        gameHelper.onActivityResult(requestCode, resultCode, intent);

        Gdx.app.log(TAG,"onActivityResult" );
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                onDisconnected();

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (resultCode != Activity.RESULT_OK) {
                logBadActivityResult(requestCode, resultCode,
                        "User cancelled returning from the 'Select Match' dialog.");
                return;
            }

            TurnBasedMatch match = intent
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                turnBasedAndroid. updateMatch(match);
            }

            Log.d(TAG, "Match = " + match);
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // Returning from 'Select players to Invite' dialog

            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                logBadActivityResult(requestCode, resultCode,
                        "User cancelled returning from 'Select players to Invite' dialog");
                return;
            }

            // get the invitee list
            ArrayList<String> invitees = intent
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria;

            int minAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers,
                        maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
          turnBasedAndroid.  mTurnBasedMultiplayerClient.createMatch(tbmc)
                    .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                        @Override
                        public void onSuccess(TurnBasedMatch turnBasedMatch) {
                            Toast.makeText(getApplication().getApplicationContext(), "onInitiateMatch!", Toast.LENGTH_SHORT).show();

                            turnBasedAndroid.onInitiateMatch(turnBasedMatch);
                        }
                    })
                    .addOnFailureListener(turnBasedAndroid.createFailureListener("There was a problem creating a match!"));

        }
    }

    private void onDisconnected() {
        Gdx.app.log(TAG,"onDisconnected" );
        System.out.println( "onDisconnected()");

        turnBasedAndroid.  mTurnBasedMultiplayerClient = null;
        turnBasedAndroid.    mInvitationsClient = null;

        setViewVisibility();
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Gdx.app.log(TAG,"onConnected" );
        if(googleSignInAccount==null) {
            Log.d(TAG, "GOOGLE ACC IS NULL");
        }
        else{
        turnBasedAndroid.     mTurnBasedMultiplayerClient = Games.getTurnBasedMultiplayerClient(this, googleSignInAccount);
        turnBasedAndroid.     mInvitationsClient = Games.getInvitationsClient(this, googleSignInAccount);

        Games.getPlayersClient(this, googleSignInAccount)
                .getCurrentPlayer()
                .addOnSuccessListener(
                        new OnSuccessListener<Player>() {
                            @Override
                            public void onSuccess(Player player) {
                                turnBasedAndroid.mDisplayName = player.getDisplayName();
                                turnBasedAndroid.mPlayerId = player.getPlayerId();
                                Log.d(TAG, "onConnected(): Connection successful");
                                setViewVisibility();
                            }
                        }
                )
                .addOnFailureListener(turnBasedAndroid.createFailureListener("There was a problem getting the player!"));

       // Log.d(TAG, "onConnected(): Connection successful");

        // Retrieve the TurnBasedMatch from the connectionHint
        GamesClient gamesClient = Games.getGamesClient(this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            TurnBasedMatch match = hint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

                            if (match != null) {
                               turnBasedAndroid. updateMatch(match);
                            }
                        }
                    }
                })
                .addOnFailureListener(turnBasedAndroid.createFailureListener(
                        "There was a problem getting the activation hint!"));

        setViewVisibility();

        // As a demonstration, we are registering this activity as a handler for
        // invitation and match events.

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
         turnBasedAndroid.     mInvitationsClient.registerInvitationCallback(turnBasedAndroid. mInvitationCallback);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        turnBasedAndroid.    mTurnBasedMultiplayerClient.registerTurnBasedMatchUpdateCallback(turnBasedAndroid.mMatchUpdateCallback);
    }}


    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(int statusCode) {
        switch (statusCode) {
            case GamesCallbackStatusCodes.OK:
                return true;

            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(com.google.example.games.basegameutils.R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(com.google.example.games.basegameutils.R.string.match_error_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(com.google.example.games.basegameutils.R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                showErrorMessage(com.google.example.games.basegameutils.R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(com.google.example.games.basegameutils.R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(com.google.example.games.basegameutils.R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(com.google.example.games.basegameutils.R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }


    public void showErrorMessage(int stringId) {
        showWarning("Warning", getResources().getString(stringId));
    }
    /**
     * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
     *
     * @param exception The exception to evaluate.  Will try to display a more descriptive reason for
     *                  the exception.
     * @param details   Will display alongside the exception if you wish to provide more details for
     *                  why the exception happened
     */
    private void handleException(Exception exception, String details) {
        int status = 0;

        if (exception instanceof TurnBasedMultiplayerClient.MatchOutOfDateApiException) {
            TurnBasedMultiplayerClient.MatchOutOfDateApiException matchOutOfDateApiException =
                    (TurnBasedMultiplayerClient.MatchOutOfDateApiException) exception;

            new AlertDialog.Builder(this)
                    .setMessage("Match was out of date, updating with latest match data...")
                    .setNeutralButton(android.R.string.ok, null)
                    .show();

            TurnBasedMatch match = matchOutOfDateApiException.getMatch();
            turnBasedAndroid. updateMatch(match);

            return;
        }

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        if (!checkStatusCode(status)) {
            return;
        }

        String message = getString(com.google.example.games.basegameutils.R.string.status_exception_error, details, status, exception);

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    private void logBadActivityResult(int requestCode, int resultCode, String message) {
        Log.i(TAG, "Bad activity result(" + resultCode + ") for request (" + requestCode + "): "
                + message);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (turnBasedAndroid.  mInvitationsClient != null) {
            turnBasedAndroid.  mInvitationsClient.unregisterInvitationCallback(turnBasedAndroid.  mInvitationCallback);
        }

        if (turnBasedAndroid.  mTurnBasedMultiplayerClient != null) {
            turnBasedAndroid.  mTurnBasedMultiplayerClient.unregisterTurnBasedMatchUpdateCallback(turnBasedAndroid.  mMatchUpdateCallback);
        }
    }





    // Update the visibility based on what state we're in.
    public void setViewVisibility() {
        boolean isSignedIn = turnBasedAndroid. mTurnBasedMultiplayerClient != null;

        if (!isSignedIn) {
            //findViewById(com.google.example.games.basegameutils.R.id.login_layout).setVisibility(View.VISIBLE);
            //  findViewById(com.google.example.games.basegameutils.R.id.sign_in_button).setVisibility(View.VISIBLE);
            //  findViewById(com.google.example.games.basegameutils.R.id.matchup_layout).setVisibility(View.GONE);
            //  findViewById(com.google.example.games.basegameutils.R.id.gameplay_layout).setVisibility(View.GONE);

            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            return;
        }


        //((TextView) findViewById(com.google.example.games.basegameutils.R.id.name_field)).setText(mDisplayName);
        // findViewById(com.google.example.games.basegameutils.R.id.login_layout).setVisibility(View.GONE);

        if (turnBasedAndroid. isDoingTurn) {
            System.out.println("isDoingTurn");

            // findViewById(com.google.example.games.basegameutils.R.id.matchup_layout).setVisibility(View.GONE);
            // findViewById(com.google.example.games.basegameutils.R.id.gameplay_layout).setVisibility(View.VISIBLE);
        } else {

            System.out.println("!isDoingTurn");
            // findViewById(com.google.example.games.basegameutils.R.id.matchup_layout).setVisibility(View.VISIBLE);
            // findViewById(com.google.example.games.basegameutils.R.id.gameplay_layout).setVisibility(View.GONE);
        }
    }

    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
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
                    onDisconnected();
                }
            });
            System.out.println("Signed out!");


        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
        }
    }

    @Override
    public void rateGame() {
        String str = "https://play.google.com/store/apps/details?id=com.di.devs.xintuition";
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
                        getString(R.string.leaderboard_leaderboard), highScore);

            System.out.println("Score is submitted for " + gameHelper.getApiClient());
        }
    }}

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
