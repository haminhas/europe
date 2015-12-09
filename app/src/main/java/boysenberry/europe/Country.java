package boysenberry.europe;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hassan on 24/11/2015.
 * Class representing a country with its data
 */
public class Country {
    private String ID;
    private String name;
    private String capital;
    private ArrayList<String> percentageFemale;// employment to female ratio
    private ArrayList<String> population; // total population
    private ArrayList<String> femalePopulation; //percetage of feaml population
    private ArrayList<String> education; // labor force with education (& of female labor force)
    private ArrayList<String> labour; // labor force, female (%percentage of total labor force)


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


    public void addFemalePercentage(String s) {
        percentageFemale.add(s);
    }

    public void addPopulation(String s) {
        population.add(s);
    }

    public void addFemalePopulation(String s) {
        femalePopulation.add(s);
    }

    public void addlabour(String s) {
        labour.add(s);
    }

    public void addEducation(String s) {
        education.add(s);
    }

    // Returns percentage of labour force which are female
    public String getLabour(String year) {
        return getDataPerYear(year, labour);
    }

    // Returns employment ratio between female and male
    public String getFemalePopulation(String year) {
        return getDataPerYear(year, femalePopulation);
    }

    // Returns labour force with education
    public String getEducation(String year) {
        return getDataPerYear(year, education);
    }

    // returns employment ratios between female and male
    public String getPercentageFemale(String year) {
        return getDataPerYear(year, percentageFemale);
    }

    public String getPopulation(String year) {
        return getDataPerYear(year, population);
    }

    public String getCapital() {
        return capital;
    }


    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    private String getDataPerYear(String year, ArrayList<String> arrayList) {
        for (String data : arrayList) {
            List<String> oneYear = Arrays.asList(data.split(","));
            Log.i("TEST", "yearGiven" + year + "yearInArray" + oneYear.get(1));
            if (oneYear.get(1).contains(year))
                return oneYear.get(0);
        }
        return "NoDataForYear:" + year;
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
