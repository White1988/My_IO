package com.internetwarz.basketballrush;


import com.internetwarz.basketballrush.model.PlayerTurn;

import java.util.ArrayList;
import java.util.List;

public abstract class TurnBasedService {


    public TurnBasedCallBacks coreGameplayCallBacks = null;
    public abstract void onQuickMatchClicked();
    public abstract void onStartMatchClicked();


    // Cancel the game. Should possibly wait until the game is canceled before
    // giving up on the view.
    public abstract void onCancelClicked();

    public abstract void onLeaveClicked();
    public abstract void onFinishClicked();


    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    /**
     * @param selectedNumber - number that player selects durinf turn
     */
    public abstract void onDoneClicked(int selectedNumber);




    /*
     * Callbacks for interacting with core libgdx from Android or desctop module
     */
    public static class TurnBasedCallBacks {

         private   List<VoidAction> onMatchStartedCallbacks = new ArrayList<>();
         private  List<EnemyTurnAction> onEnemyTurnCallbacks = new ArrayList<>();

            public void addMatchStartedCallback(VoidAction callback)
            {
                onMatchStartedCallbacks.add(callback);
            }

         public void addEnemyTurnFinishedCallback(EnemyTurnAction callback)
         {
             onEnemyTurnCallbacks.add(callback);
         }

        public void fireMatchStartedEvent()
        {
            for (VoidAction a : onMatchStartedCallbacks) a.Action();
        }

        void fireEnemyTurnFinishedEvent(PlayerTurn param)
        {
            for (EnemyTurnAction a : onEnemyTurnCallbacks) a.Action(param);
        }

    }

    interface VoidAction{

         void Action();
    }

    interface EnemyTurnAction{

         void Action(PlayerTurn param);
    }
}
