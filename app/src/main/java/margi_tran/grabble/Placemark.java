package margi_tran.grabble;

/**
 *
    Placemark.java
 *
 *  This class is responsible for a storing placemark data (parsed from the KML file)
 *  in a structured manner.
 *
 *  @author Margi Tran */

public class Placemark {
    private String name;
    private String description;
    private double latitude;
    private double longitude;

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {return longitude; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
