package com.internetwarz.basketballrush.desktop;

import com.internetwarz.basketballrush.TurnBasedService;


public class TurnbasedServiceStub implements TurnBasedService {
    @Override
    public void onQuickMatchClicked() {
        System.out.println("onQuickMatchClicked");
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
    public void onFinishClicked() {
        System.out.println("onFinishClicked");
    }

    @Override
    public void onDoneClicked() {
        System.out.println("onDoneClicked");
    }
}
