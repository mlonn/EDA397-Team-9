package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public synchronized void addPlayer(PlayerInfo player) {
        boolean found = false;
        for(PlayerInfo current : playerList){
            if(current.getDeviceAddress().equals(player.getDeviceAddress()))
                found = true;
        }

        if (!found) {
            playerList.add(player);
        }
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
        return playerList.size();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TableInfo))
            return false;
        TableInfo table = (TableInfo) obj;
        if(!name.equals(table.getName()))
            return false;
        if(size != table.getSize())
            return false;
        if(!host.equals(table.getHost()))
            return false;
        if(!comparePlayerList(playerList, table.getPlayerList()))
            return false;
        return true;
    }

    private boolean comparePlayerList(List<PlayerInfo> list1, List<PlayerInfo> list2){
        if(list1 == null && list2 == null)
            return true;
        if((list1 != null && list2 == null) || (list2 != null && list1 == null))
            return false;
        if(list1.size() != list2.size())
            return false;
        for(PlayerInfo current : list1){
            if(!list2.contains(current))
                return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return getHost().getName() + " - " + getName() + " - " + getSize();
    }

    public PlayerInfo findPlayer(PlayerInfo player) {
        for (PlayerInfo p : playerList) {
            if (p.equals(player)) {
                return p;
            }
        }
        return null;
    }
}
