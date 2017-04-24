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

import static android.support.test.espresso.core.deps.guava.base.CharMatcher.isNot;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
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
    IndexActivity indexActivity;
    Player player;

    Table table;
    Player playerTable;


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
            assertThat(player.getUsername(),is(notNullValue()));
        }
        else{
            indexActivity.createUsernameFile("Sven");

            assertThat(player.getUsername(), is(notNullValue()));
            assertThat(player.getUsername(),is("Sven"));
        }
    }

    @Rule
    public ActivityTestRule<CreateRuleActivity> rule2  = new ActivityTestRule<>(CreateRuleActivity.class);

    @Test
    public void testGetExpansions(){

        CardHandler cardHandler = new CardHandler();
        CreateRuleActivity createRuleActivity = rule2.getActivity();

        ArrayList<CardExpansion> cardExpansions = cardHandler.getExpansions(createRuleActivity);

        assertThat(cardExpansions.isEmpty(), is(false));
        assertThat(cardExpansions.size() > 0, is(true));
    }

    @Rule
    public ActivityTestRule<CreateTableActivity> ruleTable  = new ActivityTestRule<>(CreateTableActivity.class);

    @Before
    public void setUpTable(){
        playerTable = new Player("Klas");
        table = new Table("Table1", ruleTable.getActivity().getBaseContext());
        table.newPlayer(playerTable);
    }

    @Test
    public void testTable(){

        assertThat(table.tableName, is("Table1"));

        assertThat(table.getSize() == 1, is(true));

        table.newPlayer(player);
        assertThat(table.getSize() > 1, is(true));

        assertThat(table.getHost(), is(notNullValue()));
    }


}
