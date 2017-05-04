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
import android.widget.RelativeLayout;
import android.widget.TextView;

import se.chalmers.eda397.team9.cardsagainsthumanity.R;

public class PlayerRowLayout extends LinearLayout {

    private TextView textView;
    private ImageView imageView;
    private int screenWidth;
    private String color;
    private String deviceAddress;

    private int margin = convertDpToPixels(5, getContext());

    public static final Integer CONNECTING = 0;
    public static final Integer CONNECTED = 1;

    private static final String CONNECTED_COLOR = "#333333";
    private static final String CONNECTING_COLOR = "#bbbbbb";
    private static final String READY_COLOR = "#6fa54f";


    //Players' rows
    private void init(){
        View.inflate(getContext(), R.layout.player_row, this);
        textView = (TextView) findViewById(R.id.player_name);
        imageView = (ImageView) findViewById(R.id.player_image);

        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.setMargins(margin/2, margin, margin/2, margin); //Margins for the boxes that contain the players' names (except king)
        param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        setLayoutParams(param);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels; // Witdth in pixels
        //int dpMin = convertDpToPixels(screenWidth/2-5,this); Converted dp to pixel

        //int test = ((Layout) this.getParent()).getWidth();
        this.setMinimumWidth(screenWidth/2-50); // Pixel (screenWidth/2-50) !!!!!!! Change with dpMin
        param.width = screenWidth/2-2*margin;

        textView.setWidth(screenWidth/2-(convertDpToPixels(90, getContext())));
        setBackgroundResource(R.drawable.player_row_item_background);
    }

    //Method that convert DP to Pixels
    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        //float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, resources.getDisplayMetrics());
        return (int)px;
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

    //Host's row
    public void setAsHost(){

        setMinimumWidth(screenWidth);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.setMargins(margin/2, margin, margin/2, margin); //Margins for the boxe that contain the host's name
        param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2);
        setLayoutParams(param);
        textView.setWidth(screenWidth-(convertDpToPixels(90, getContext())));
        setBackgroundResource(R.drawable.host_row_item_background);

    }

    public void setName(String name){
        textView.setText(name);
    }

    public void setImageColor(String color){
        this.color = color;
        imageView.setBackgroundColor(Color.parseColor(color));
    }

    public String getColor() {
        return color;
    }

    public void setConnectionStatus(int status){
        if(status == CONNECTING){
            textView.setTextColor(Color.parseColor(CONNECTING_COLOR));
        }

        if(status == CONNECTED){
            textView.setTextColor(Color.parseColor(CONNECTED_COLOR));
        }
    }

    public void setKing () {
        ImageView kingIcon = (ImageView) findViewById(R.id.king_icon);
        kingIcon.setVisibility(VISIBLE);

    }

    public void setScore (int score) {
        TextView scoreText = (TextView) findViewById(R.id.player_score);
        scoreText.setText("" + score);

    }

    public String getPlayerId(){
        return deviceAddress;
    }

    public void setPlayerId(String deviceAddress){
        this.deviceAddress = deviceAddress;
    }

    public void setReady(boolean ready) {
        if(ready)
            textView.setTextColor(Color.parseColor(READY_COLOR));
        else
            textView.setTextColor(Color.parseColor(CONNECTED_COLOR));
    }

    public void setColor(String color) {
        this.color = color;
    }
}
