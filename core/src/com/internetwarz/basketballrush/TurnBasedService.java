package com.internetwarz.basketballrush;


public abstract class TurnBasedService {


    public TurnBasedCallBacks turnBasedCallBacks = null;
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





    public interface TurnBasedCallBacks {
        void onMatchStartedCallback();
    }
}
