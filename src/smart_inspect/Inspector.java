/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart_inspect;

/**
 *
 * @author joud
 */
public class Inspector {
    private String name;
    private int ID;
    private Location location;

    public Inspector(String name, int ID) {
        this.name = name;
        this.ID = ID;
    }

    Inspector() {
    
    }

    public String getName() {
        return name;
    }

    public void setName(String usesrname) {
        this.name = usesrname;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(int Longitude, int Latitude) {
        this.location = new Location( Longitude,  Latitude);
    }
    
    
}
