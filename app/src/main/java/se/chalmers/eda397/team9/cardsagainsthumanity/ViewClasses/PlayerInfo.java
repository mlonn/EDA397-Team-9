package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import java.io.Serializable;

/**
 * Created by SAMSUNG on 2017-04-25.
 */

public class PlayerInfo implements Serializable{

    String name;
    String color;
    String deviceAddress;
    int score;
    boolean isKing = false;

    public PlayerInfo(String name){
        this.name = name;
        color = "#000000";
        deviceAddress = "Test_Address";
    }

    public PlayerInfo(String name, String deviceAddress){
        this.name = name;
        color = "#000000";
        this.deviceAddress = deviceAddress;
    }

    public PlayerInfo(String name, String deviceAddress, String color){
        this.name = name;
        this.deviceAddress = deviceAddress;
        this.color = color;
    }

    public synchronized void setColor(String color){
        this.color = color;
    }

    public String getColor(){
        return color;
    }

    public String getName(){
        return name;
    }

    public String getDeviceAddress(){
        return deviceAddress;
    }

    public synchronized void setDeviceAddress(String deviceAddress){
        this.deviceAddress = deviceAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PlayerInfo))
            return false;
        PlayerInfo playerInfo = (PlayerInfo) obj;
        if(!deviceAddress.equals(playerInfo.getDeviceAddress()))
            return false;
        if(!name.equals(playerInfo.getName()))
            return false;
        if(!color.equals(playerInfo.getColor()))
            return false;
        return true;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing() {
        isKing = true;
    }

}
