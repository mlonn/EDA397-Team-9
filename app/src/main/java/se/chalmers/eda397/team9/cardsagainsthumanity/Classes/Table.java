package se.chalmers.eda397.team9.cardsagainsthumanity.Classes;

import java.util.ArrayList;
import java.util.List;


public class Table {

    public String tableName = "";
    private List<Player> playerList = new ArrayList<>();
    public BlackCard blackCard;
    private Player host = new Player("TestPlayer");

    /**
     * constructor
     *
     * @param tableName: name given by the host
     *                   REQUIRE notNull
     */
    public Table(String tableName) {
        this.tableName = tableName;
    }

    /**
     * adds the Player that want to join the table
     *
     * @param player: player added to the list of players
     */

    public String getHost(){
        //Get username requires context????
        return host.getUsername();
    }

    public String getName(){
        return tableName;
    }

    public int getSize(){
        return playerList.size();
    }

    public void newPlayer(Player player) {

        playerList.add(player);
    }

    /**
     * it gives a blackCard for the next round
     */
    public void getBlackCard() {
        //retrieve from DB the blackCard
    }

    @Override
    public String toString(){
        return getHost() + "_" + getName() + "_" + getSize();
    }
}
