package boysenberry.europe;

import java.util.ArrayList;

/**
 * Created by Hassan on 24/11/2015.
 */
public class Countries {
    private static ArrayList<Country> list;

    public Countries (){
        list = new ArrayList<Country>();
    }

    public void add(Country c){
        list.add(c);
    }

    public ArrayList<Country> getList(){
        return list;
    }

    public String toString(){
        String s= "";
        for(Country c:list){
            s = s +"\n"+c.getID() + "," + c.getName() +","+ c.getCapital();
        }
        return s;
    }
}
