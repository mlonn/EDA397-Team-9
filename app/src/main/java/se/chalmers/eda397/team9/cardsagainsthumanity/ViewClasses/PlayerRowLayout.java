package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.chalmers.eda397.team9.cardsagainsthumanity.R;

/**
 * Created by SAMSUNG on 2017-04-24.
 */

public class PlayerRowLayout extends LinearLayout {

    TextView textView;
    ImageView imageView;
    int screenWidth;

    //Players' rows
    private void init(){
        View.inflate(getContext(), R.layout.player_row, this);
        textView = (TextView) findViewById(R.id.player_name);
        imageView = (ImageView) findViewById(R.id.player_image);

        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.setMargins(10,10,10,10); //Margins for the boxes that contain the players' names (except king)
        param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        setLayoutParams(param);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels; // Witdth in pixels
        //int dpMin = convertPixelsToDp(screenWidth/2-5,this); Converted dp to pixel

        //int test = ((Layout) this.getParent()).getWidth();
        this.setMinimumWidth(screenWidth/2-50); // Pixel (screenWidth/2-50) !!!!!!! Change with dpMin

        setBackgroundResource(R.drawable.player_row_item_background);
    }

    //Method that convert Pixels to DP
    public static int convertPixelsToDp(float px, PlayerRowLayout context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return (int)dp;
    }

    public PlayerRowLayout(Context context) {
        super(context);
        init();
    }

    public PlayerRowLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerRowLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PlayerRowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    //King's row
    public void setAsHost(){

        //ImageView imgKing = (ImageView) findViewById(R.id.kingIcon);

        setMinimumWidth(screenWidth);

        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.setMargins(10,10,10,10); //Margins for the boxe that contain the king's name
        param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2);
        setLayoutParams(param);

        setBackgroundResource(R.drawable.host_row_item_background);

    }

    public void setName(String name){
        textView.setText(name);
    }

    public void setImageColor(String color){
        imageView.setBackgroundColor(Color.parseColor(color));
    }

}
