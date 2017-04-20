package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import java.io.Serializable;

import se.chalmers.eda397.team9.cardsagainsthumanity.Classes.Table;

/**
 * Created by SAMSUNG on 2017-04-07.
 */

public class TableInfo implements Serializable{

    private String name;
    private String host;
    private int size;

    public TableInfo(String table){
        name = table;
    }

    public TableInfo(String table, String host){
        this(table);
        this.host = host;
    }

    public TableInfo(String table, String host, int size){
        this(table, host);
        this.size = size;
    }

    public String getName(){
        return name;
    }
    public String getHost(){
        return host;
    }
    public int getSize(){
        return size;
    }

    @Override
    public String toString(){
        return getHost() + " - " + getName() + " - " + getSize();
    }

}
