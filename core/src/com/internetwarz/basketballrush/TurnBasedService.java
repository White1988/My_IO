package com.internetwarz.basketballrush;


public interface TurnBasedService {


    public void onQuickMatchClicked();

    // Cancel the game. Should possibly wait until the game is canceled before
    // giving up on the view.
    public void onCancelClicked();
    public void onLeaveClicked();
    public void onFinishClicked();

    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.

    /**
     * @param selectedNumber - number that player selects durinf turn
     */

    public void onDoneClicked(int selectedNumber);

    public void onStartMatchClicked();

}
