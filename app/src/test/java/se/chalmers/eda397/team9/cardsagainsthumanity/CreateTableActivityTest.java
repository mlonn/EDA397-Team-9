package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.Intent;
import android.test.mock.MockContext;

import org.junit.Test;

/**
 * Created by SAMSUNG on 2017-04-05.
 */
public class CreateTableActivityTest {

    Context mockContext;

    @Test
    public void testMulticast(){
        mockContext = new MockContext();
        mockContext.startActivity(new Intent(mockContext, LobbyActivity.class));
    }
}