package boysenberry.europe;

/**
 * Created by Hassan on 24/11/2015.
 */
public class Country {
    private String ID;
    private String name;
    private String capital;

    public Country (String ID, String name, String capital){
        this.ID = ID;
        this.name = name;
        this.capital = capital;
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
}
