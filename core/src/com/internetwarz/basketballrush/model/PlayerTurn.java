package com.internetwarz.basketballrush.model;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PlayerTurn implements Serializable {


    /*CONSTANTS*/
    public final static int MATCH_TURN_STATUS_INVITED = 0;
    public final static int MATCH_TURN_STATUS_MY_TURN = 1;
    public final static int  MATCH_TURN_STATUS_THEIR_TURN = 2;
    public final static int MATCH_TURN_STATUS_COMPLETE = 3;



    /*PERSISTED FIELDS*/
    public int selectedNumber;
    public int turnCounter;
    public String player1Id;
    public int player1Score;
    public int player2Score;


    /*NON-PERSISTED FIELDS*/  //need to fill manually on client
    public int matchStatus;

    public byte[] persist() {
        try {
            return convertToBytes(this);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PlayerTurn unpersist(byte[] data) throws IOException, ClassNotFoundException {


            return (PlayerTurn) convertFromBytes(data);

    }


    private byte[] convertToBytes(Object object) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos) ;
        out.writeObject(object);

       return bos.toByteArray();



    }



    private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = new ObjectInputStream(bis);

        return in.readObject();

    }

    @Override
    public String toString() {
        return "PlayerTurn{" +
                "selectedNumber=" + selectedNumber +
                ", turnCounter=" + turnCounter +
                ", player1Id='" + player1Id + '\'' +

                ", player1Score=" + player1Score +
                ", player2Score=" + player2Score +
                '}';
    }
}
