package capstone.restapi.domain;

import lombok.Data;
import javax.persistence.*;

@Data //Lombok annotation, creates Get, Set, Copy, ToString for this class using the Lombok Package
@Entity //Indicates to the Spring Framework that this object can be treated as a table (i.e. a table will exist which will have items of this type)
@Table(name = "wifimapptable") // Indicates to the Spring Framework that objects of this type will be found in the wifimapptable
public class Lwdata {

    @Id //Indicates to Spring Framework that the objects unique ID is the next attribute initialised
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Generates a value for ID - uses a stored Identity counter to ensure all Lwdata objects have unique ID attributes
    private long id;
    private Double lat; //the latitude of the Lwdata point in degrees
    private Double lng; //the longitude of the Lwdata point in degrees
    private int rssilvl; //the RSSI level of the Lwdata point

    @Column(name = "timestamp")  //Identifying the column name for the timestamp field
    private long timestamp; //The time that the Lwdata object was saved to the database, in milliseconds from EPOCH

    public Lwdata(Double lat, Double lng, int rssilvl) {

        this.lat = lat;
        this.lng = lng;
        this.rssilvl = rssilvl;

    }

}
