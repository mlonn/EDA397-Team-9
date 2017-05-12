package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by TOSHIBA on 12/05/2017.
 */
public class ProfileActivityTest {

    @Rule
    public ActivityTestRule<ProfileActivity> mActivityTestRule = new ActivityTestRule<ProfileActivity>(ProfileActivity.class);
    private ProfileActivity mActivity = null;
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testProfileName(){
        TextView textView = (TextView) mActivity.findViewById(R.id.textView11);
        assertNotNull(textView);
        //
        String username = textView.toString();
        assertTrue(username.length()>3);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}