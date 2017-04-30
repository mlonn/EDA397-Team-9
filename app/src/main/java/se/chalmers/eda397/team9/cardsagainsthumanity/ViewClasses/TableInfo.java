package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;

/**
 * Created by SAMSUNG on 2017-04-07.
 */

public class TableInfo implements Serializable{

    private String name;
    private PlayerInfo host;
    private int size;
    private List<PlayerInfo> playerList;

    public TableInfo(String table){
        playerList = new ArrayList<PlayerInfo>();
        name = table;
    }

    public TableInfo(String table, PlayerInfo host){
        this(table);
        this.host = host;
    }

    public TableInfo(String table, PlayerInfo host, int size){
        this(table, host);
        this.size = size;
    }

    public List<PlayerInfo> getPlayerList(){
        return playerList;
    }

    public synchronized void addPlayer(PlayerInfo player){
        playerList.add(player);
    }

    public synchronized void removePlayer(PlayerInfo player){
        playerList.remove(player);
    }

    public String getName(){
        return name;
    }
    public PlayerInfo getHost(){
        return host;
    }
    public int getSize(){
        return size;
    }

    @Override
    public String toString(){
        return getHost().getName() + " - " + getName() + " - " + getSize();
    }

}
