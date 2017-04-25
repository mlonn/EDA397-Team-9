package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.test.ActivityInstrumentationTestCase2;
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
public class IndexActivityInstrumentTest extends ActivityInstrumentationTestCase2<IndexActivity> {
    public IndexActivityInstrumentTest() {
        super(IndexActivity.class);
    }

    //Test1
    IndexActivity mIndexActivity;
    Player player;

    @Rule
    public ActivityTestRule<IndexActivity> rule1  = new ActivityTestRule<>(IndexActivity.class);

    @Before
    public void setUp(){
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        player = new Player("");
        mIndexActivity = rule1.getActivity();;
    }

    @Test
    public void checkUsernameFile(){

        if(mIndexActivity.fileExists()){
            assertNotNull(player.getUsername());
        }
        else{
            mIndexActivity.createUsernameFile("Sven");
            assertNotNull(player.getUsername());
            assertEquals(player.getUsername(),"Sven");
        }
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
