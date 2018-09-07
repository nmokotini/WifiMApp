package capstone.restapi.services;

import capstone.restapi.domain.Lwdata;
import capstone.restapi.repositories.LwRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
An implementation of the LwService interface
 */
@Service //Indicates to the Spring Framework that this class is a service
public class LwServiceImpl implements LwService {

    @Autowired
    private final LwRepository lwRepository; //lwRepository object which will be used to access the repository/database

    public LwServiceImpl(LwRepository lwRepository) {
        this.lwRepository = lwRepository; //initialising the lwRepository object
    }

    /*
        Method which will return the lwdata object with the corresponding id
        The method uses a lwRepository method to find the object
        The method called is an inherited JpaRepository method
        @parameter Long id: The id of the requested Lwdata object
     */
    @Override
    public Lwdata findLwdataById(Long id) {
        if(lwRepository.findById(id).isPresent()) {
            return lwRepository.findById(id).get();
        }
        else return null;
    }

    /*
       Method which will return a list of lwData
       The class uses the lwRepository method to return the list of lwData objects
       The method called is an inherited JpaRepository method
    */
    @Override
    public List<Lwdata> findAllLwdata() {

        return aggregate(lwRepository.findAll(),-33.960900,-33.954600, 18.460952, 18.462969, 300, 100);

    }

    /*
    Method which will return a list of lwData
    The class uses the lwRepository method to return a list of all stored lwData objects
    The method called is an inherited JpaRepository method.
    The method then goes through all of the entries, adding those that are below the required age/timestamp
    and adds them to a list that is returned.
    @parameter int age: The maximum age of entries eg. 1
    @parameter String type: The duration of the age eg. hours or days
    */
    @Override
    public List<Lwdata> findLwdataByAge(int age, String type) {

        List<Lwdata> raw = lwRepository.findAll();
        List<Lwdata> output = new ArrayList<Lwdata>();
        long millis;
        long min;

        if (type.toUpperCase().equals("DAYS")) {
            millis = TimeUnit.DAYS.toMillis(age);
            min = Instant.now().toEpochMilli() - millis;
            for (Lwdata lwdata : raw) {
                if (lwdata.getTimestamp() >= min) {
                    output.add(lwdata);
                }
            }
        }
        else if (type.toUpperCase().equals("HOURS")) {
            millis = TimeUnit.HOURS.toMillis(age);
            min = Instant.now().toEpochMilli() - millis;
            for (Lwdata lwdata : raw) {
                if (lwdata.getTimestamp() >= min) {
                    output.add(lwdata);
                }
            }
        }
        else{
            output = findAllLwdata();
        }

        return aggregate(output, -33.960900,-33.954600, 18.460952, 18.462969, 300, 100);
    }

    /*
        Method which will save an lwData object to the lwRepository/database
        The method called is an inherited JpaRepository method
        This method
        @parameter Lwdata lwdata: The Lwdata object to be saved/inserted into the database
    */
    @Override
    public Lwdata saveLwdata(Lwdata lwdata) {
        return lwRepository.save(lwdata);
    }


    /*
        Method which will aggregate a list of Lwdata object based on a set of parameters.
        It does so by creating a rectangle of 4 points (lat1, lng1), (lat1, lng2), (lat2, lng1), (lat2, lng2).
        Traverses through the blocks, defined by blat and blng.
        This can be logically seen as a 2d array with dimensions [blat][blng].
        Lwdata objects which fall within each block are recorded and aggregated, determining the average RSSIlevel in that block.
        A new aggregated Lwdata object is then created, with its location being the center of that block, and RSSIlevel value being the average for that block.
        @parameter List<Lwdata> data: The Lwdata objects to be aggregated, already filtered based on age
        @parameter Double lat1: The latitude of the first point
        @parameter Double lat2: The latitude of the second point
        @parameter Double lng1: The longitude of the first point
        @parameter Double lng2: The longitude of the second point
        @parameter int blat: The number of blocks there will be across the latitude of the rectangle
        @parameter int blat: The number of blocks there will be across the longitude of the rectangle
    */
    public List<Lwdata> aggregate(List<Lwdata> data, Double lat1, Double lat2, Double lng1, Double lng2, int blat, int blng){

        List<Lwdata> agg = new ArrayList(); //initialising list to store the aggregated Lwdata objects
        List<Lwdata> points = new ArrayList(); //initialising list to store the Lwdata objects in a particular block
        Double wlat1;
        Double wlat2;
        Double wlng1;
        Double wlng2;
        boolean negLat; //keeping track of whether the latitude and longitude are negative
        boolean negLng;

        if(lat1 < 0){//deteriming whether the latitude and longitude are negative
            negLat = true;
        }
        else{
            negLat = false;
        }
        if(lng1 < 0){
            negLng = true;
        }
        else{
            negLng = false;
        }

        lat1 = Math.abs(lat1); //getting the absolute values of the latitude and longitude
        lat2 = Math.abs(lat2);
        lng1 = Math.abs(lng1);
        lng2 = Math.abs(lng2);

        if(lat1 < lat2){ //creating a generic rectangle from any set of inputted latitudes and longitudes
            wlat1 = lat1;
            wlat2 = lat2;
        }
        else{
            wlat1 = lat2;
            wlat2 = lat1;
        }

        if(lng1 < lng2){
            wlng1 = lng1;
            wlng2 = lng2;
        }
        else{
            wlng1 = lng2;
            wlng2 = lng1;
        }


        Double deltalat = (wlat2 - wlat1)/blat; //determining the size, in degrees, of each blocks latitude
        Double deltalng = (wlng2 - wlng1)/blng; //determining the size, in degrees, of each blocks longitude

        Double aggPointLat = 0.0;
        Double aggPointLng = 0.0;
        Double avgWifi = 0.0;

        /*
        the code below traverses the blocks (like a 2d array).
        It traverses through the latitudes first. Each Lwdata point in data which is within that block is added to points.
        The Lwdata's RSSIlevels in points are then averaged and stored, before adding an aggregated Lwdata object to a list (agg).
        The aggregated Lwdata point falls within the center of the block it is created from (eg, see *** below).
        */

        for(double i = wlat1; i < wlat2; i+=deltalat){ //go through each block's latitude

            for(double j = wlng1; j < wlng2; j+=deltalng) {

                avgWifi = 0.0; //resetting the values to store information
                aggPointLat = 0.0;
                aggPointLng = 0.0;
                points.clear(); //clearing the list to store Lwdata points in the block

                for (Lwdata lw : data) {

                    if (Math.abs(lw.getLat()) <= i + deltalat && Math.abs(lw.getLat()) >= i && Math.abs(lw.getLng()) <= j + deltalng && Math.abs(lw.getLng()) >= j) {

                        points.add(lw);

                    }

                }

                if(points.size() != 0) {
                    //determining the average RSSI level
                    for (Lwdata point : points) {

                        avgWifi += point.getRssilvl();

                    }
                        //*** determining the latitude and longitude of the aggregated Lwdata point
                    if (negLat) {
                        aggPointLat = (i + (deltalat / 2)) * -1;
                    } else {
                        aggPointLat = i + (deltalat / 2);
                    }
                    if (negLng) {
                        aggPointLng = (j + (deltalng / 2)) * -1;
                    } else {
                        aggPointLng = j + (deltalng / 2);
                    }

                    agg.add(new Lwdata(aggPointLat, aggPointLng, (int) (avgWifi / points.size())));

                }

            }

        }
            return agg;

        }

    }



