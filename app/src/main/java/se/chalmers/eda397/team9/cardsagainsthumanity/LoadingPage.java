package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;


public class LoadingPage extends FragmentActivity {
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app Next activity
                Intent i = new Intent(LoadingPage.this, IndexActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 2000);
    }
}

