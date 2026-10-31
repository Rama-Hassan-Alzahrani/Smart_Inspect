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
public class Location {
    
    private int Longitude;
    private int Latitude;

    public Location(int Longitude, int Latitude) {
        this.Longitude = Longitude;
        this.Latitude = Latitude;
    }

    public int getLongitude() {
        return Longitude;
    }

    public void setLongitude(int Longitude) {
        this.Longitude = Longitude;
    }

    public int getLatitude() {
        return Latitude;
    }

    public void setLatitude(int Latitude) {
        this.Latitude = Latitude;
    }
    
    
    
}
