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
    private ArrayList<String> population;
    private ArrayList<String> femalePopulation;

    public Country (String ID, String name, String capital){
        this.ID = ID;
        this.name = name;
        this.capital = capital;
        percentageFemale = new ArrayList<String>();
        population = new ArrayList<String>();
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

    public void addPopulation(String s){
        population.add(s);
    }

    public ArrayList<String> getPopulation(){
        return population;
    }

    public void addFemalePopulation(String s){
        femalePopulation.add(s);
    }

    public ArrayList<String> getFemalePopulation(){
        return femalePopulation;
    }
}
