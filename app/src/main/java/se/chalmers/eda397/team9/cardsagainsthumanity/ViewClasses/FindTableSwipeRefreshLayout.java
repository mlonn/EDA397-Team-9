package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FindTableSwipeRefreshLayout extends SwipeRefreshLayout implements PropertyChangeListener{

    public FindTableSwipeRefreshLayout(Context context) {
        super(context);
    }

    public FindTableSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals(Message.Type.START_REFRESHING)){
            if(!isRefreshing())
                setRefreshing(true);
        }
        if(propertyChangeEvent.getPropertyName().equals(Message.Type.STOP_REFRESHING)){
            if(isRefreshing()){
                setRefreshing(false);
            }
        }
    }
}
