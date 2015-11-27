package boysenberry.europe;

import java.util.ArrayList;

/**
 * Created by Hassan on 24/11/2015.
 * This class stores a collection of countries
 */
public class Countries {
    private static ArrayList<Country> list;

    public Countries() {
        list = new ArrayList<>();
    }

    public void add(Country c) {
        list.add(c);
    }

    public ArrayList<Country> getList() {
        return list;
    }

    public String toString() {
        String s = "";
        for (Country c : list) {
            s = s + c.toString() + "\r\n";
        }
        return s;
    }
}
