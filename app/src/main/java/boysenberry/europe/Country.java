package boysenberry.europe;

import java.util.ArrayList;

/**
 * Created by Hassan on 24/11/2015.
 */
public class Country {
    private String ID;
    private String name;
    private String capital;
    private ArrayList<String> percentageFemale;

    public Country (String ID, String name, String capital){
        this.ID = ID;
        this.name = name;
        this.capital = capital;
        percentageFemale = new ArrayList<String>();
    }

    public String getID(){
        return ID;
    }

    public String getName(){
        return name;
    }

    public String getCapital(){
        return capital;
    }

    public String toString(){
        return ID +"," +name+"," +capital;
    }

    public void addFemalePercentage(String s){
        percentageFemale.add(s);
    }

    public ArrayList<String> getPercentageFemale(){
        return percentageFemale;
    }
}
