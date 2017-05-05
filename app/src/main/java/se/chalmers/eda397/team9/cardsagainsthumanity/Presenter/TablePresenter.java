package se.chalmers.eda397.team9.cardsagainsthumanity.Presenter;

import android.support.v7.app.AppCompatActivity;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

/**
 * Created by SAMSUNG on 2017-04-08.
 */

public class TablePresenter {
    private AppCompatActivity activity;

    //Temporary, should exist in the game class.
    private Map<String, TableInfo> tables;

    private PropertyChangeSupport pcs;

    public TablePresenter(AppCompatActivity app){
        tables = new HashMap<String, TableInfo>();
        pcs = new PropertyChangeSupport(this);
        activity = app;
    }

    public TablePresenter(AppCompatActivity app, Map<String, TableInfo> tables){
        this(app);
        this.tables = tables;
    }

    public void setPropertyChangeListeners(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }


    public TableInfo createTable(String name, PlayerInfo host){
        TableInfo table = new TableInfo(name, host);
        tables.put(name, table);
        return table;
    }

    public void clearTables() {
        tables.clear();
    }

    public void insertAll(Map<String, TableInfo> newTables) {
        for(Map.Entry<String, TableInfo> current : newTables.entrySet()){
            TableInfo toTable = current.getValue();
            tables.put(current.getValue().getName(), toTable);
        }
    }
}
