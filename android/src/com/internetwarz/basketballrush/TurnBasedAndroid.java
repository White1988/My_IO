package com.internetwarz.basketballrush;/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchUpdateCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.internetwarz.basketballrush.model.PlayerTurn;

import java.util.ArrayList;

import static com.internetwarz.basketballrush.AndroidLauncher.RC_LOOK_AT_MATCHES;
import static com.internetwarz.basketballrush.AndroidLauncher.RC_SELECT_PLAYERS;


/**
 * TBMPSkeleton: A minimalistic "game" that shows turn-based
 * multiplayer features for Play Games Services.  In this game, you
 * can invite a variable number of players and take turns editing a
 * shared state, which consists of single string.  You can also select
 * automatch players; all known players play before automatch slots
 * are filled.
 * <p>
 * INSTRUCTIONS: To run this sample, please set up
 * a project in the Developer Console. Then, place your app ID on
 * res/values/ids.xml. Also, change the package name to the package name you
 * used to create the client ID in Developer Console. Make sure you sign the
 * APK with the certificate whose fingerprint you entered in Developer Console
 * when creating your Client Id.
 *
 * @author Wolff (wolff@google.com), 2013
 */
public class TurnBasedAndroid extends TurnBasedService  {


    public static final String TAG = "TurnBasedAndroid";

    // Client used to interact with the TurnBasedMultiplayer system.
    public TurnBasedMultiplayerClient mTurnBasedMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    public InvitationsClient mInvitationsClient = null;

    // Local convenience pointers
    public TextView mDataView;
    public TextView mTurnTextView;


    public TurnBasedAndroid(Activity contextActivity) {
        this.contextActivity = contextActivity;
    }

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;

    // This is the current match data after being unpersisted.
    // Do not retain references to match data once you have
    // taken an action on the match, such as takeTurn()
    public PlayerTurn mTurnData;
    public String mDisplayName;public String mPlayerId;


    // This is a helper functio that will do all the setup to create a simple failure message.
    // Add it to any task and in the case of an failure, it will report the string in an alert
    // dialog.
    public OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
                System.out.println(string);
                Toast.makeText(contextActivity.getApplication().getApplicationContext(), string +"  exception : " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

   private final Activity contextActivity ;

    public void onStartMatchClicked() {
          mTurnBasedMultiplayerClient.getSelectOpponentsIntent(1, 7, true)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "getSelectOpponentsIntent!", Toast.LENGTH_SHORT).show();

                        contextActivity.  startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                })
                .addOnFailureListener(createFailureListener(
                        "Select opponents!"));
    }

    // Displays your inbox. You will get back onActivityResult where
    // you will need to figure out what you clicked on.
    public void showMatchMakingLobby() {
        Log.d(TAG, "Start ShoMatchMakingLobby");

        mTurnBasedMultiplayerClient.getInboxIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "getInboxIntent!", Toast.LENGTH_SHORT).show();

                        contextActivity.startActivityForResult(intent, RC_LOOK_AT_MATCHES);
                    }
                })
                .addOnFailureListener(createFailureListener("Cannot get inbox intent!"));
        Log.d(TAG, "Finish ShoMatchMakingLobby");
    }

    //todo add in mainMenuScreen
    @Override
    public void onQuickMatchClicked() {

        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        TurnBasedMatchConfig turnBasedMatchConfig = TurnBasedMatchConfig.builder()
                .setAutoMatchCriteria(autoMatchCriteria).build();

        // Start the match
        mTurnBasedMultiplayerClient.createMatch(turnBasedMatchConfig)
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onInitiateMatch!", Toast.LENGTH_SHORT).show();

                        onInitiateMatch(turnBasedMatch);

                        coreGameplayCallBacks.fireMatchStartedEvent();
                        gameStartedLocally = true;
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem creating a match!"));
    }

    // In-game controls


    private void getAllDebugMatchInfo(TurnBasedMatch match)
    {
        Log.d(TAG, "getAvailableAutoMatchSlots =" + match.getAvailableAutoMatchSlots());
        Log.d(TAG, "getParticipants size =" + match.getParticipants().size());
        Log.d(TAG, "getCreatorId =" + match.getCreatorId());
        Log.d(TAG, "getDescription =" + match.getDescription());
        Log.d(TAG, "getDescriptionParticipantId =" + match.getDescriptionParticipantId());
        Log.d(TAG, "getLastUpdaterId =" + match.getLastUpdaterId());
        Log.d(TAG, "getMatchId =" + match.getMatchId());
        Log.d(TAG, "getPendingParticipantId =" + match.getPendingParticipantId());
        Log.d(TAG, "getStatus =" + match.getStatus());
        Log.d(TAG, "getTurnStatus =" + match.getTurnStatus());
        Log.d(TAG, "getVariant =" + match.getVariant());
        Log.d(TAG, "getVersion =" + match.getVersion());
       // Log.d(TAG, "getPendingParticipantId =" + match.getPendingParticipantId());
    }




     //todo add in Duelscreen
     @Override

    public void onCancelClicked() {


        mTurnBasedMultiplayerClient.cancelMatch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String matchId) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onCancelMatch!", Toast.LENGTH_SHORT).show();

                        onCancelMatch(matchId);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem cancelling the match!"));

        isDoingTurn = false;

    }
    @Override
    //todo add in mainMenuScreen
    public void onLeaveClicked() {
     //   Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onLeaveClicked!", Toast.LENGTH_SHORT).show();

        String nextParticipantId = getNextParticipantId();

        mTurnBasedMultiplayerClient.leaveMatchDuringTurn(mMatch.getMatchId(), nextParticipantId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onLeaveMatch!", Toast.LENGTH_SHORT).show();

                        onLeaveMatch();
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem leaving the match!"));


    }

    @Override
    //todo call after onFinish button from DuelScreen
    public void onFinishClicked() {

      //  Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onFinishClicked!", Toast.LENGTH_SHORT).show();

        mTurnBasedMultiplayerClient.finishMatch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onUpdateMatch!", Toast.LENGTH_SHORT).show();

                        onUpdateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem finishing the match!"));

        isDoingTurn = false;

    }




    @Override
    //todo call after move from DuelScreen
    public void onDoneClicked(int selectedNumber) {

     //   Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onDoneClicked!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Start onDoneClicked");
        String nextParticipantId = getNextParticipantId();
        // Create the next turn
        mTurnData = new PlayerTurn();
        mTurnData.turnCounter += 1;
        mTurnData.selectedNumber = selectedNumber;

        if(isPlayer1)
        {
            mTurnData.player1Score += selectedNumber;
        }
        else
            {
            mTurnData.player2Score += selectedNumber;
            }


        mTurnBasedMultiplayerClient.takeTurn(mMatch.getMatchId(),
                mTurnData.persist(), nextParticipantId)
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onUpdateMatch2!", Toast.LENGTH_SHORT).show();
                        onUpdateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));

        mTurnData = null;
        Log.d(TAG, "Finish onDoneClicked");
    }


    private boolean isPlayer1 = true;

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        Log.d(TAG, "StartMatch");
        mTurnData = new PlayerTurn();
        // Some basic turn data
        mTurnData.selectedNumber = -1;

        mMatch = match;

        String myParticipantId = mMatch.getParticipantId(mPlayerId);



        if(mMatch.getData() != null) //todo if its even possible?
        {

            mTurnData.player2Id = myParticipantId;
        }
        else
        {
            mTurnData.player1Id = myParticipantId;
        }


        mTurnBasedMultiplayerClient.takeTurn(match.getMatchId(),
                mTurnData.persist(), myParticipantId)
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onUpdateMatch3!", Toast.LENGTH_SHORT).show();

                        updateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));
        Log.d(TAG, "Finish  StartMatch");
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        mTurnBasedMultiplayerClient.rematch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        Toast.makeText(contextActivity.getApplication().getApplicationContext(), "onInitiateMatch!", Toast.LENGTH_SHORT).show();

                        onInitiateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem starting a rematch!"));
        mMatch = null;
        isDoingTurn = false;
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {

        String myParticipantId = mMatch.getParticipantId(mPlayerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        mMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                 showToast("This game was canceled!!");
                System.out.println( "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                  showToast("This game is expired.  So sad!");
                System.out.println( "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                System.out.println(
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showToast("This game is over; someone finished it, and so did you!  " +
                            "There is nothing to be done.");
                    System.out.println(
                            "This game is over; someone finished it, and so did you!  " +
                                    "There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                System.out.println(
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mTurnData = PlayerTurn.unpersist(mMatch.getData());

                if(!gameStartedLocally)
                {
                    coreGameplayCallBacks.fireMatchStartedEvent();
                }

                coreGameplayCallBacks.fireEnemyTurnFinishedEvent(mTurnData);
                System.out.println("MATCH_TURN_STATUS_MY_TURN");
                showToast("MATCH_TURN_STATUS_MY_TURN");


                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                System.out.println("MATCH_TURN_STATUS_THEIR_TURN");
                showToast("MATCH_TURN_STATUS_THEIR_TURN");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                System.out.println("MATCH_TURN_STATUS_INVITED");
                showToast("MATCH_TURN_STATUS_INVITED");
        }

        mTurnData = null;


    }

    private void showToast(String text)
    {
        Toast.makeText(contextActivity.getApplication().getApplicationContext(), text, Toast.LENGTH_SHORT).show();

    }

    private void onCancelMatch(String matchId) {


        isDoingTurn = false;
        showToast("This match (" + matchId + ") was canceled.  " +
                "All other players will have their game ended.");
        System.out.println( "This match (" + matchId + ") was canceled.  " +
                "All other players will have their game ended.");
    }

    public void onInitiateMatch(TurnBasedMatch match) {


        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }
        coreGameplayCallBacks.fireMatchStartedEvent();
        gameStartedLocally = true;
        startMatch(match);
    }



    private void onLeaveMatch() {


        isDoingTurn = false;
        System.out.println( "You've left this match.");
        showToast("You've left this match.");

       coreGameplayCallBacks.fireMatchLeftEvent();
    }


    public void onUpdateMatch(TurnBasedMatch match) {

        getAllDebugMatchInfo(match);
        if (match.canRematch()) {
          //  askForRematch();
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }

    }

    public InvitationCallback mInvitationCallback = new InvitationCallback() {
        // Handle notification events.
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) {
         showToast("\"onInvitationReceived\", inviter = " + invitation.getInviter().getDisplayName());
            System.out.println("\"onInvitationReceived\"" + invitation);


        }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) {
            showToast("\"onInvitationRemoved\"" );
            System.out.println("\"onInvitationRemoved\"");
        }
    };

             public TurnBasedMatchUpdateCallback mMatchUpdateCallback = new TurnBasedMatchUpdateCallback() {
        @Override
        public void onTurnBasedMatchReceived(@NonNull TurnBasedMatch turnBasedMatch) {
            showToast("\"onTurnBasedMatchReceived\"" );
            System.out.println("\"onTurnBasedMatchReceived\"");
        }

        @Override
        public void onTurnBasedMatchRemoved(@NonNull String matchId) {
            showToast("\"onTurnBasedMatchRemoved\"  " + matchId );
            System.out.println("\"onTurnBasedMatchRemoved.\"");
        }
    };






}
