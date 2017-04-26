package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by axel_ on 2017-04-21.
 */
@RunWith(AndroidJUnit4.class)
public class CreateTableActivityIntrumentTest extends ActivityInstrumentationTestCase2<CreateTableActivity>{

    public CreateTableActivityIntrumentTest() {
        super(CreateTableActivity.class);
    }
    CreateTableActivity mCreateTableActivity;

    //Test2
    Table   table;
    Player  playerTable;
    Player  player;

    //testTableNameUnique
    Table tableUnique1;

    @Before
    public void setUpTestGetExpansions(){
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mCreateTableActivity = getActivity();
    }
    @Test
    public void testGetExpansions() throws Exception{
        CardHandler cardHandler = new CardHandler();
        ArrayList<CardExpansion> cardExpansions = cardHandler.getExpansions(mCreateTableActivity);
        assertEquals(cardExpansions.isEmpty(), false);
        assertEquals(cardExpansions.size() > 0, true);
    }

//    @Before
//    public void setUpTable(){
//        playerTable = new Player("Klasse");
//        player = new Player("Nils");
//        table = new Table("Table1", mCreateTableActivity);
//        table.newPlayer(playerTable);
//    }
//
//    @Test
//    public void testTable() throws Exception{
//        assertNotNull(table.getName());
//        assertNotEquals(table.getName(),"");
//        assertEquals(table.getName(),"Table1");
//        assertEquals(table.getSize(), 1);
//        table.newPlayer(player);
//        assertEquals(table.getSize() > 1, true);
//        String host = table.getHost();
//        assertNotNull(table.getHost());
//    }
}
