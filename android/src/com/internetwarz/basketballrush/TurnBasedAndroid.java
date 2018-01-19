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
import android.widget.TextView;

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
            }
        };
    }

   private final Activity contextActivity ;

    public void onStartMatchClicked() {
          mTurnBasedMultiplayerClient.getSelectOpponentsIntent(1, 7, true)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        contextActivity.  startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                })
                .addOnFailureListener(createFailureListener(
                        "Select opponents!"));
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
                        onInitiateMatch(turnBasedMatch);

                        coreGameplayCallBacks.fireMatchStartedEvent();
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem creating a match!"));
    }

    // In-game controls




     //todo add in Duelscreen
     @Override

    public void onCancelClicked() {


        mTurnBasedMultiplayerClient.cancelMatch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String matchId) {
                        onCancelMatch(matchId);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem cancelling the match!"));

        isDoingTurn = false;

    }
    @Override
    //todo add in mainMenuScreen
    public void onLeaveClicked() {

        String nextParticipantId = getNextParticipantId();

        mTurnBasedMultiplayerClient.leaveMatchDuringTurn(mMatch.getMatchId(), nextParticipantId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onLeaveMatch();
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem leaving the match!"));


    }

    @Override
    //todo call after onFinish button from DuelScreen
    public void onFinishClicked() {

        mTurnBasedMultiplayerClient.finishMatch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        onUpdateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem finishing the match!"));

        isDoingTurn = false;

    }




    @Override
    //todo call after move from DuelScreen
    public void onDoneClicked(int selectedNumber) {

        String nextParticipantId = getNextParticipantId();
        // Create the next turn
        mTurnData.turnCounter += 1;
        mTurnData.selectedNumber = selectedNumber;

        mTurnBasedMultiplayerClient.takeTurn(mMatch.getMatchId(),
                mTurnData.persist(), nextParticipantId)
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        onUpdateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));

        mTurnData = null;
    }




    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        mTurnData = new PlayerTurn();
        // Some basic turn data
        mTurnData.selectedNumber = 0;

        mMatch = match;

        String myParticipantId = mMatch.getParticipantId(mPlayerId);



        mTurnBasedMultiplayerClient.takeTurn(match.getMatchId(),
                mTurnData.persist(), myParticipantId)
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        updateMatch(turnBasedMatch);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem taking a turn!"));
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        mTurnBasedMultiplayerClient.rematch(mMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
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
                System.out.println( "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                System.out.println( "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                System.out.println(
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
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

                coreGameplayCallBacks.fireEnemyTurnFinishedEvent(mTurnData);
                System.out.println("MATCH_TURN_STATUS_MY_TURN");
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                System.out.println("MATCH_TURN_STATUS_THEIR_TURN");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                System.out.println("MATCH_TURN_STATUS_INVITED");
        }

        mTurnData = null;


    }

    private void onCancelMatch(String matchId) {


        isDoingTurn = false;

        System.out.println( "This match (" + matchId + ") was canceled.  " +
                "All other players will have their game ended.");
    }

    public void onInitiateMatch(TurnBasedMatch match) {


        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }



    private void onLeaveMatch() {


        isDoingTurn = false;
        System.out.println( "You've left this match.");
    }


    public void onUpdateMatch(TurnBasedMatch match) {


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
            System.out.println("\"onInvitationReceived\"" + invitation);
        }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) {
            System.out.println("\"onInvitationRemoved\"");
        }
    };

             public TurnBasedMatchUpdateCallback mMatchUpdateCallback = new TurnBasedMatchUpdateCallback() {
        @Override
        public void onTurnBasedMatchReceived(@NonNull TurnBasedMatch turnBasedMatch) {
            System.out.println("\"onTurnBasedMatchReceived\"");
        }

        @Override
        public void onTurnBasedMatchRemoved(@NonNull String matchId) {
            System.out.println("\"onTurnBasedMatchRemoved.\"");
        }
    };






}
