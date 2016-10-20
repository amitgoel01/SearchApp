package device.droidtv.org.vht;

/**
 * Created by amit.goyal on 8/29/2016.
 */
public class Item {
    private String message;
    private String country;
    private String name;
    private String abbr;
    private String area;
    private String largest_city;
    private String capital;

    Item(/*String message, */String country, String name, String abbr, String area, String largest_city, String capital) {
//        this.message = message;
        this.country = country;
        this.name = name;
        this.abbr = abbr;
        this.area = area;
        this.largest_city = largest_city;
        this.capital = capital;
    }

    public String getMessage() {
        return message;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getAbbr() {
        return abbr;
    }

    public String getArea() {
        return area;
    }

    public String getLargest_city() {
        return largest_city;
    }

    public String getCapital() {
        return capital;
    }
}
