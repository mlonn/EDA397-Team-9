package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by SAMSUNG on 2017-04-21.
 */

public class FindTableSwipeRefreshLayout extends SwipeRefreshLayout implements PropertyChangeListener{

    public FindTableSwipeRefreshLayout(Context context) {
        super(context);
    }

    public FindTableSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("START_REFRESHING")){
            if(!isRefreshing())
                setRefreshing(true);
        }
        if(propertyChangeEvent.getPropertyName().equals("STOP_REFRESHING")){
            if(isRefreshing()){
                setRefreshing(false);
            }
        }
    }
}
