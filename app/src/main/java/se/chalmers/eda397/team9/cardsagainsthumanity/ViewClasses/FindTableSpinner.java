package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAMSUNG on 2017-04-21.
 */

public class FindTableSpinner extends android.support.v7.widget.AppCompatSpinner implements PropertyChangeListener{

    public FindTableSpinner(Context context) {
        super(context);

    }

    public FindTableSpinner(Context context, int mode) {
        super(context, mode);
    }

    public FindTableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FindTableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FindTableSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public FindTableSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }


    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("TABLES_UPDATED")){
            if(propertyChangeEvent.getNewValue() instanceof List) {
                @SuppressWarnings("unchecked")
                List<TableInfo> list = (List<TableInfo>) propertyChangeEvent.getNewValue();

                ArrayAdapter<TableInfo> spinnerAdapter = new ArrayAdapter<TableInfo>(getContext(), android.R.layout.simple_list_item_1, list);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                this.setAdapter(spinnerAdapter);

                int tableSize = ((List) propertyChangeEvent.getNewValue()).size();

                String toastSentence = "";

                if(tableSize == 0)
                    toastSentence = "No tables found";

                if(tableSize == 1) {
                    toastSentence = tableSize + " table found";
                }

                if(tableSize > 1) {
                    toastSentence = tableSize + " tables found";
                }

                Toast.makeText(getContext(), toastSentence, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
