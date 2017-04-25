package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.test.ActivityUnitTestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.CardExpansion;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ClassesInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("se.chalmers.eda397.team9.cardsagainsthumanity", appContext.getPackageName());
    }
    //Test1
    IndexActivity indexActivity;
    Player player;

    //Test2
    Table table;
    Player playerTable;

    //Test3
    IndexActivity indexActivityTest3;
    Context contextTest3;


    //testTableNameUnique
    Table tableUnique1;


    @Rule
    public ActivityTestRule<IndexActivity> rule1  = new ActivityTestRule<>(IndexActivity.class);

    @Before
    public void setUp(){
        player = new Player("");
        indexActivity = rule1.getActivity();;
    }

    @Test
    public void checkUsernameFile(){

        if(indexActivity.fileExists()){
            assertNotNull(player.getUsername());
        }
        else{
            indexActivity.createUsernameFile("Sven");

            assertNotNull(player.getUsername());
            assertEquals(player.getUsername(),"Sven");
        }
    }

    @Rule
    public ActivityTestRule<CreateTableActivity> rule2  = new ActivityTestRule<>(CreateTableActivity.class);

    @Test
    public void testGetExpansions() throws Exception{

        CardHandler cardHandler = new CardHandler();
        CreateTableActivity createRuleActivity = rule2.getActivity();

        ArrayList<CardExpansion> cardExpansions = cardHandler.getExpansions(createRuleActivity);

        assertEquals(cardExpansions.isEmpty(), false);
        assertEquals(cardExpansions.size() > 0, true);

    }

    @Rule
    public ActivityTestRule<CreateTableActivity> ruleTable  = new ActivityTestRule<>(CreateTableActivity.class);

    @Before
    public void setUpTable(){
        playerTable = new Player("Klasse");
        Context con = InstrumentationRegistry.getTargetContext();
        player = new Player("Nils");
        table = new Table("Table1", con);


        //table = new Table("Table1", ruleTable.getActivity().getBaseContext());
        table.newPlayer(playerTable);
    }

    @Test
    public void testTable() throws Exception{

        assertNotNull(table.getName());
        assertNotEquals(table.getName(),"");
        assertEquals(table.getName(),"Table1");
        assertEquals(table.getSize(), 1);
        table.newPlayer(player);
        assertEquals(table.getSize() > 1, true);
        assertNotNull(table.getHost());
    }

//    @Before
//    public void setUpTableNameUnique(){
//        playerTable = new Player("Klas");
//        Context con = InstrumentationRegistry.getTargetContext();
//        player = new Player("Nils");
//        table = new Table("Table1", con);
//
//        //table = new Table("Table1", ruleTable.getActivity().getBaseContext());
//        table.newPlayer(playerTable);
//    }
//
//    @Test
//    public void testTableNameUnique(){
//
//
//    }

//    @Rule
//    public ActivityTestRule<IndexActivity> ruleOptionSelection  = new ActivityTestRule<>(IndexActivity.class);
//
//    @Before
//    public void setUp3(){
//        indexActivityTest3 = ruleOptionSelection.getActivity();
//        contextTest3 = indexActivityTest3.getBaseContext();
//        String sss = "";
//    }
//
//
//    @Test
//    public void testOptionsSelection() throws Exception{
//       assertThat(contextTest3, is());
//       indexActivityTest3.onOptionsItemSelected()
//    }



}
