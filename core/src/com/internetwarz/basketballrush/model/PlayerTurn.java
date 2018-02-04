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

    public int selectedNumber;
    public int turnCounter;


    public String player1Id;
    public String player2Id;


    public int player1Score;
    public int player2Score;

    public byte[] persist() {
        try {
            return convertToBytes(this);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PlayerTurn unpersist(byte[] data) {

        try {
            return (PlayerTurn) convertFromBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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
                ", player2Id='" + player2Id + '\'' +
                ", player1Score=" + player1Score +
                ", player2Score=" + player2Score +
                '}';
    }
}
