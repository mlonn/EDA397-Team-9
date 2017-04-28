package se.chalmers.eda397.team9.cardsagainsthumanity.MulticastClasses;

import java.io.Serializable;

/**
 * Created by SAMSUNG on 2017-04-28.
 */

public class MulticastPackage implements Serializable{

    private String packageType;
    private Object object;
    private String target;

    public MulticastPackage(String target, String packageType){
        this.packageType = packageType;
        this.target = target;
    }

    public MulticastPackage(String target, String packageType, Object object){
        this.target = target;
        this.packageType = packageType;
        this.object = object;
    }

    public String getPackageType(){
        return packageType;
    }

    public Object getObject(){
        return object;
    }

    public String getTarget(){
        return target;
    }
}
