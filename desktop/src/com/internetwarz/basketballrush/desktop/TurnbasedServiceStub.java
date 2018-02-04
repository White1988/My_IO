package com.internetwarz.basketballrush.desktop;

import com.internetwarz.basketballrush.TurnBasedService;
import com.internetwarz.basketballrush.model.PlayerTurn;


public class TurnbasedServiceStub extends TurnBasedService {

    PlayerTurn playerTurn = new PlayerTurn();

    @Override
    public void onQuickMatchClicked() {

        System.out.println("onQuickMatchClicked");

        coreGameplayCallBacks.fireMatchStartedEvent(null);
    }

    @Override
    public void onStartMatchClicked() {
        System.out.println("onStartMatchClicked");

        coreGameplayCallBacks.fireMatchStartedEvent(null);
    }

    @Override
    public void onCancelClicked() {
        System.out.println("onCancelClicked");
    }

    @Override
    public void onLeaveClicked() {

        System.out.println("onLeaveClicked");
    }

    @Override
    public void onFinishClicked()
    {
        System.out.println("onFinishClicked");
    }

    @Override
    public void showMatchMakingLobby() {

    }

    @Override
    public void onDoneClicked(int selectedNumber) {

        System.out.println("onDoneClicked selectedNumber: " + selectedNumber);
    }

}
