package boysenberry.europe;

import java.util.ArrayList;

/**
 * Created by Hassan on 24/11/2015.
 * Class representing a country with its data
 */
public class Country {
    private String ID;
    private String name;
    private String capital;
    private ArrayList<String> percentageFemale;
    private ArrayList<String> population;
    private ArrayList<String> femalePopulation;
    private ArrayList<String> education;
    private ArrayList<String> labour;


    public Country(String ID, String name, String capital) {
        this.ID = ID;
        this.name = name;
        this.capital = capital;
        percentageFemale = new ArrayList<>();
        population = new ArrayList<>();
        femalePopulation = new ArrayList<>();
        education = new ArrayList<>();
        labour = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getCapital() {
        return capital;
    }

    public void addFemalePercentage(String s) {
        percentageFemale.add(s);
    }

    public ArrayList<String> getPercentageFemale() {
        return percentageFemale;
    }

    public void addPopulation(String s) {
        population.add(s);
    }

    public ArrayList<String> getPopulation() {
        return population;
    }

    public void addFemalePopulation(String s) {
        femalePopulation.add(s);
    }

    public ArrayList<String> getFemalePopulation() {
        return femalePopulation;
    }

    public ArrayList<String> getEducation(){
        return education;
    }

    public void addEducation(String s){
        education.add(s);
    }

    public ArrayList<String> getLabour(){
        return labour;
    }

    public void addlabour(String s){
        labour.add(s);
    }



    /**
     * Represent the country as a string: ID, Name, Capital, Population per year,
     * FemalePopulation per year, and PercentageFemale per year, all written in new line.
     *
     * @return String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ID).append("\r\n").append(name).append("\r\n").append(capital).append("\r\n");

        for (String s : population)
            sb.append(s).append("\r\n");
        for (String s : femalePopulation)
            sb.append(s).append("\r\n");
        for (String s : percentageFemale)
            sb.append(s).append("\r\n");
        for (String s : education)
            sb.append(s).append("\r\n");
        for (String s : labour)
            sb.append(s).append("\r\n");

        return sb.toString();
    }
}
