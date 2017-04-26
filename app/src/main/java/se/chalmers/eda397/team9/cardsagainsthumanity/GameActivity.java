package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

/**
 * Created by emy on 23/04/17.
 */

public class GameActivity extends AppCompatActivity {

    public int numWhiteCards = 10;

    String[] textWhiteCard = new String [numWhiteCards];
    String textBlackCard = "Black card";

    ImageButton favoriteButtons[] = new ImageButton [numWhiteCards];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);

        for (int i = 0; i < numWhiteCards; i++) {

            //Child relative layout that contains white card and favorite's symbol (heart)
            RelativeLayout childLayout = new RelativeLayout(this);

            //Create objects that contain the images
            ImageView imgWhiteCard = new ImageView(this);
            ImageButton imgFavoriteBorder = new ImageButton(this);

            imgWhiteCard.setId(i);
            imgFavoriteBorder.setId(i);

            //Layout settings of the images
            imgWhiteCard.setPadding(2, 2, 2, 2); //.setPadding(left, top, right, bottom)
            RelativeLayout.LayoutParams paramsWhiteCard = new RelativeLayout.LayoutParams(480, 530); //.LayoutParams(width, height) for white cards
            paramsWhiteCard.setMargins(1, 1, 1, 45); //.setMargins(left, top, right, bottom)
            imgWhiteCard.setLayoutParams(paramsWhiteCard);

            imgFavoriteBorder.setPadding(2, 2, 2, 2); //.setPadding(left, top, right, bottom)
            RelativeLayout.LayoutParams paramsFavoriteBorder = new RelativeLayout.LayoutParams(100, 100); //(width,height) for favorite border
            paramsFavoriteBorder.setMargins(350, 10, 1, 1); //.setMargins(left, top, right, bottom)
            imgFavoriteBorder.getBackground().setAlpha(0); //ImageButton background full transparent
            imgFavoriteBorder.setLayoutParams(paramsFavoriteBorder);

            //Insert images in the objects
            imgWhiteCard.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.white_card));
            imgFavoriteBorder.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_favorite_border));

            //imgFavoriteBorder.bringToFront();

            //Add listener with method onClick for favorite buttons
            imgFavoriteBorder.setOnClickListener(favoriteClick);

            //Add objects to the view
            childLayout.addView(imgWhiteCard);
            childLayout.addView(imgFavoriteBorder);
            layout.addView(childLayout);

            //findViewById(R.id.imgFavoriteBorder).setOnClickListener(favoriteClick);

            favoriteButtons[i] = imgFavoriteBorder;

        }

    }

    public void setText() {
        //Set text of white cards (later on also the black card)
        for (int i=0; i<10; i++){
            textWhiteCard[i]="White card " + i;
        }
    }

    //Button listener with method onClick for favorite buttons
    View.OnClickListener favoriteClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            ImageButton favoriteButton = (ImageButton) view; //Cast

            for (int i = 0; i < numWhiteCards; i++) {
                if (favoriteButton == favoriteButtons[i])
                    favoriteButtons[i].setImageResource(R.mipmap.ic_favorite);
                else
                    favoriteButtons[i].setImageResource(R.mipmap.ic_favorite_border);
            }

        }
    };

    //Main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.changeName:
                try{
                    File prefsFile = new File("/data/data/se.chalmers.eda397.team9.cardsagainsthumanity/shared_prefs/usernameFile.xml");
                    prefsFile.delete();
                }
                catch (Exception e){

                }

                Intent intent = new Intent(this, IndexActivity.class);
                startActivity(intent);

                return true;
            case R.id.changeTable:
                //Do something
                return true;
            case R.id.settings:
                //Do something
                return true;
            case R.id.help:
                //Do something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
