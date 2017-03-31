package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.util.ArrayList;
import java.util.List;


public class Table {

    public String tableName = "";
    private List<Player> playerList = new ArrayList<>();
    public BlackCard blackCard;
    private Player host = null;

    /**
     * constructor
     * @param player: host of the new table
     * @param tableName: name given by the host
     *                 REQUIRE notNull
     *
     */
    public Table(Player player, String tableName){
        this.host = player;
        this.tableName = tableName;
        newPlayer(player);
    }

    /**
     * adds the Player that want to join the table
     * @param player: player added to the list of players
     */
    public void newPlayer(Player player){
        playerList.add(player);
    }

    /**
     * it gives a blackCard for the next round
     */
    public void getBlackCard(){
        //retrieve from DB the blackCard
    }
}
