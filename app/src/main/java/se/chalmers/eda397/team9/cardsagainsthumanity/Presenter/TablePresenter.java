package se.chalmers.eda397.team9.cardsagainsthumanity.Presenter;

import android.support.v7.app.AppCompatActivity;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Player;
import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.PlayerInfo;
import se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses.TableInfo;

/**
 * Created by SAMSUNG on 2017-04-08.
 */

public class TablePresenter {
    private AppCompatActivity activity;

    //Temporary, should exist in the game class.
    private Map<String, Table> tables;

    private PropertyChangeSupport pcs;

    public TablePresenter(AppCompatActivity app){
        tables = new HashMap<String, Table>();
        pcs = new PropertyChangeSupport(this);
        activity = app;
    }

    public TablePresenter(AppCompatActivity app, Map<String, Table> tables){
        this(app);
        this.tables = tables;
    }

    public void setPropertyChangeListeners(PropertyChangeListener listener){
        pcs.addPropertyChangeListener(listener);
    }

    private TableInfo convertToTableInfo(Table table){
        return new TableInfo(table.getName(), new PlayerInfo(table.getHost()), table.getSize());
    }

    private Table convertToTable(TableInfo tableInfo){
        PlayerInfo hostInfo = tableInfo.getHost();
        return new Table(tableInfo.getName(), new Player(hostInfo.getName()));
    }

    public TableInfo createTable(String name, PlayerInfo host){
        Table table = new Table(name, activity);
        tables.put(name, table);
        return convertToTableInfo(table);
    }

    public void clearTables() {
        tables.clear();
    }

    public void insertAll(Map<String, TableInfo> newTables) {
        for(Map.Entry<String, TableInfo> current : newTables.entrySet()){
            Table toTable = convertToTable(current.getValue());
            tables.put(current.getValue().getName(), toTable);
        }
    }
}
