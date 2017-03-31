package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mccol on 31/03/2017.
 */

public class Table {

    public String tableName = "";
    private List<Player> playerList = new ArrayList<>();
    public BlackCard blackCard;
    private Player host = null;

    public Table(){

    }

    public void createTable(Player player, String tableName){
        this.host = player;
        this.tableName = tableName;
        newPlayer(player);
    }

    public void newPlayer(Player player){
        playerList.add(player);
    }
}
