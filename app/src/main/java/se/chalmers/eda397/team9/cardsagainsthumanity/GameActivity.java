package se.chalmers.eda397.team9.cardsagainsthumanity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Game;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.WhiteCard;
import se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses.MulticastPackage;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.IntentType;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.Message;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.util.BlackCardAdapter;

import static se.chalmers.eda397.team9.cardsagainsthumanity.R.id.profile;


/**
 * Created by emy on 23/04/17.
 */

public class GameActivity extends AppCompatActivity implements PropertyChangeListener {
    public ArrayList<WhiteCard> whiteCards;

    ImageButton favoriteButtons[];
    private Game game;
    private PlayerInfo myPlayerInfo;
    private Boolean[] selectedCards;
    private Timer timer;
    private String tableAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = (Game) getIntent().getExtras().get(IntentType.THIS_GAME);
        tableAddress = (String) getIntent().getStringExtra(IntentType.TABLE_ADDRESS);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(IndexActivity.GAME_SETTINGS_FILE, Context.MODE_PRIVATE);
        myPlayerInfo = game.getPlayerByUUID(prefs.getString(IndexActivity.PLAYER_UUID, null));
        /* Get table info */

        if (myPlayerInfo.isKing()) {
            openCloseTableDialog();
        } else {
            initPlayer();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateGame();
            }

        }, 0, 200);
    }

    private void openCloseTableDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do u wanna be the King for this round?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initKing();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                game.setKing(myPlayerInfo);
                initPlayer();
                }

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateGame() {
        game.update();
    }

    private void initKing() {
        setContentView(R.layout.activity_king);
        TextView blackCardText = (TextView) findViewById(R.id.currentBlackCard);
        blackCardText.setText(Html.fromHtml(game.getBlackCard().getText()));
        ListView blackCardList = (ListView) findViewById(R.id.black_card_list);
        blackCardList.setAdapter(new BlackCardAdapter(this, game.getBlackCard(), myPlayerInfo.getSubmissions(), myPlayerInfo));
        Button endTurnButton = (Button) findViewById(R.id.btn_selectWinner);
        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game.endTurn()) {
                    Intent intent = new Intent(getApplicationContext(), EndTurnActivity.class);
                    intent.putExtra(IntentType.THIS_GAME, game);
                    intent.putExtra(IntentType.WINNING_STRING, updateBlackCardText(game.getWinner().getWhiteCards()));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void initPlayer() {
        setContentView(R.layout.activity_game);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        TextView blackCardTextView = (TextView) findViewById(R.id.textviewBlackCard);
        blackCardTextView.setText(Html.fromHtml(game.getBlackCard().getText()));
        TextView pickTextView = (TextView) findViewById(R.id.pickTextView);
        String t = "Pick: " + game.getBlackCard().getPick();
        pickTextView.setText(t);
        whiteCards = myPlayerInfo.getWhiteCards();
        favoriteButtons = new ImageButton[whiteCards.size()];
        selectedCards = new Boolean[whiteCards.size()];
        for (int i = 0; i < whiteCards.size(); i++) {
            selectedCards[i] = false;
            //Child relative layout that contains white card and favorite's symbol (heart)
            RelativeLayout childLayout = new RelativeLayout(this);

            //Create objects that contain the images
            ImageView imgWhiteCard = new ImageView(this);
            ImageButton imgFavoriteBorder = new ImageButton(this);
            TextView cardText = new TextView(this);

            imgWhiteCard.setId(i);
            imgFavoriteBorder.setId(i);

            //Layout settings of the images
            imgWhiteCard.setPadding(0, 0, 0, 0); //.setPadding(left, top, right, bottom)
            RelativeLayout.LayoutParams paramsWhiteCard = new RelativeLayout.LayoutParams(convertDpToPixels(150,this), convertDpToPixels(450,this)); //.LayoutParams(width, height) for white cards
            paramsWhiteCard.setMargins(convertDpToPixels(10,this), convertDpToPixels(10,this), convertDpToPixels(1,this), convertDpToPixels(7,this)); //.setMargins(left, top, right, bottom)
            //paramsWhiteCard.setMargins(1, 1, 1, 75); //.setMargins(left, top, right, bottom)
            imgWhiteCard.setLayoutParams(paramsWhiteCard);

            imgFavoriteBorder.setPadding(0, 0, 0, 0); //.setPadding(left, top, right, bottom)
            RelativeLayout.LayoutParams paramsFavoriteBorder = new RelativeLayout.LayoutParams(convertDpToPixels(150,this), convertDpToPixels(65,this)); //(width,height) for favorite border
            paramsFavoriteBorder.setMargins(convertDpToPixels(59,this), convertDpToPixels(1,this), 0, 0); //.setMargins(left, top, right, bottom)
            imgFavoriteBorder.getBackground().setAlpha(0); //ImageButton background full transparent
            imgFavoriteBorder.setLayoutParams(paramsFavoriteBorder);

            cardText.setPadding(convertDpToPixels(40,this),0,convertDpToPixels(30,this),0);
            RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(convertDpToPixels(150,this), convertDpToPixels(450,this)); //.LayoutParams(width, height) for text in white card
            cardText.setLayoutParams(paramsText);
            paramsText.setMargins(0,convertDpToPixels(80,this),0,0);
            cardText.setText(Html.fromHtml(whiteCards.get(i).getWord()));
            cardText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            cardText.setTextColor(Color.BLACK);

            //Insert images in the objects
            imgWhiteCard.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.white_card));
            imgFavoriteBorder.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_favorite_border));

            //imgFavoriteBorder.bringToFront();

            //Add listener with method onClick for favorite buttons
            imgFavoriteBorder.setOnClickListener(favoriteClick);

            //Add objects to the view
            childLayout.addView(imgWhiteCard);
            childLayout.addView(imgFavoriteBorder);
            childLayout.addView(cardText);
            layout.addView(childLayout);

            //findViewById(R.id.imgFavoriteBorder).setOnClickListener(favoriteClick);

            favoriteButtons[i] = imgFavoriteBorder;

        }
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(submitCards);
    }

    View.OnClickListener submitCards = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (game.getBlackCard().getPick() == myPlayerInfo.getSelectedCards().size()) {
                myPlayerInfo.submitSelection();
            } else {
                Toast.makeText(getApplicationContext(), "Please select a winner", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //Button listener with method onClick for favorite buttons
    View.OnClickListener favoriteClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            ImageButton favoriteButton = (ImageButton) view; //Cast

            int picked = 0;
            boolean selected = false;
            for (int i = 0; i < whiteCards.size(); i++) {
                if(selectedCards[i]){
                    picked++;
                }
                if (favoriteButton == favoriteButtons[i] && selectedCards[i]){
                    selected = true;
                }
            }
            if (picked < game.getBlackCard().getPick() || selected) {
                for (int i = 0; i < whiteCards.size(); i++) {
                    if (favoriteButton == favoriteButtons[i]) {
                        selectedCards[i] = !selectedCards[i];
                        if (selectedCards[i]) {
                            favoriteButtons[i].setImageResource(R.mipmap.ic_favorite);
                            myPlayerInfo.getSelectedCards().add(whiteCards.get(i));
                        } else {
                            favoriteButtons[i].setImageResource(R.mipmap.ic_favorite_border);
                            myPlayerInfo.getSelectedCards().remove(whiteCards.get(i));
                        }
                    }
                }
                String blackCardText = updateBlackCardText(myPlayerInfo.getSelectedCards());
                if (blackCardText != null) {
                    TextView blackCardTextView = (TextView) findViewById(R.id.textviewBlackCard);
                    blackCardTextView.setText(Html.fromHtml(blackCardText));
                }
            } else {
                Toast.makeText(getApplicationContext(), "You can only select " + game.getBlackCard().getPick() + " cards.", Toast.LENGTH_SHORT).show();
            }

        }
    };

    //Method that convert Pixels to DP
    /*
    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return (int)dp;
    }*/

    //Method that convert DP to Pixels
    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        //float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, resources.getDisplayMetrics());
        return (int)px;
    }

    private void openExitGameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit the game?").setTitle("King of cards");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String updateBlackCardText(List<WhiteCard> whiteCards) {
        String[] blackText = game.getBlackCard().getText().split("_");
        StringBuilder sb = new StringBuilder();
        if (blackText.length>1) {
            for (int j = 0; j < blackText.length; j++) {
                sb.append(blackText[j]);
                if (j < whiteCards.size()) {
                    sb.append(whiteCards.get(j).getWord());
                } else if (j < blackText.length-1) {
                    sb.append("_");
                }
            }
        }
        return sb.toString();
    }

    //Main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu, menu);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(IndexActivity.GAME_SETTINGS_FILE, Context.MODE_PRIVATE);
        String username = prefs.getString(IndexActivity.PLAYER_NAME, null);
        menu.findItem(R.id.profile).setTitle(username);
        return true;
    }

    @Override
    public void onBackPressed() {
        openExitGameDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;


            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi! I'm playing this wonderful game called King of Cards. Please download it you too from Play store so we can play together!";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "King of Cards");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.help:
                //Do something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Message.Type.GAME_STARTED)) {
            android.os.Handler handler = new android.os.Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    new MulticastPackage(tableAddress, Message.Response.GAME_START_CONFIRMED, myPlayerInfo);
                }
            });
        }
    }
}
