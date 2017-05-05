package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Game;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.CardHandler;

/**
 * Created by Mikae on 2017-04-21.
 */
@RunWith(MockitoJUnitRunner.class)
public class GameTest {
    @Mock
    Context mockContext;

    private Game game;
    private ArrayList<PlayerInfo> players = new ArrayList<PlayerInfo>();
    @Before
    public void Setup(){
        String S = "s";
        mockContext = new MockContext();
        players.add(new PlayerInfo("Mike"));
        players.add(new PlayerInfo("Ike"));
        players.add(new PlayerInfo("Carl"));
        game = new Game(players , CardHandler.getExpansions(mockContext));
        String str = "";
    }
    @Test
    public void GameSetupTest() throws Exception {

    }
}
