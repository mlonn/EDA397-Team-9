package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import java.io.Serializable;

/**
 * Created by SAMSUNG on 2017-04-25.
 */

public class PlayerInfo implements Serializable{

    String name;
    String color;
    String deviceAddress;


    public PlayerInfo(String name){
        this.name = name;
    }

    public PlayerInfo(String name, String deviceAddress){
        this.name = name;
        this.deviceAddress = deviceAddress;
    }

    public void setColor(String color){
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
}
