package capstone.restapi.controllers;


import capstone.restapi.domain.Lwdata;
import capstone.restapi.services.LwService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
/*
    This class is a REST API Controller class.
    It handles all API requests made for the WifiMApp, by calling the appropriate service supplied by the lwService.
 */
@RestController //Indicates to the Spring Framework that this class is a REST API controller class
@RequestMapping(LwdataController.BASE_URL)//Indicates to the Spring Framework that all requests will have the mapping of BASE_URL
public class LwdataController {

    static final String BASE_URL = "/api/v2/lwdata"; //The base URL for the API

    private final LwService lwService; //The LwService object which will provide services to the controller

    public LwdataController(LwService lwService){ //Initialising the controller, parsing it a LwService object
        this.lwService = lwService;
    }

    /*
    This method handles a GET request from a device, using the Base_URL defined above
    It returns a list of the objects listed by the lwService
    */
    @GetMapping
    public List<Lwdata> getAllLwdata(){
        return lwService.findAllLwdata();
    }

    /*
    This method handles a GET request from a device, using the Base_URL defined above.
    The url also contains an id value, shown in the GetMapping annotation.
    The id value is parsed to the lwService method to retrieve and return a value with the given id
     */
    @GetMapping("/{id}")
    public Lwdata getLwdataById(@PathVariable Long id){
        return lwService.findLwdataById(id);
    }

    /*
    This method handles a GET request from a device, using the Base_URL/age.
    The url also contains the age type (hours or days), and the age value (1,2,3 etc.)
    The values are parsed to the lwService method to retrieve an Lwdata list containing the requested values
    */
    @GetMapping("/age/{type}/{age}")
    public List<Lwdata> getLwdataByAge(@PathVariable int age, @PathVariable String type){

        return lwService.findLwdataByAge(age, type);

    }

    /*
    This method handles a POST request from a device, using the base url defined above.
    The call must also contain a JSON object in the correct Lwdata format.
    If the object is stored correctly, it will return a Created HTTP message.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lwdata saveLwdata(@RequestBody Lwdata lwdata){

        lwdata.setTimestamp(Instant.now().toEpochMilli()); //sets the timestamp of the posted Lwdata object
        return lwService.saveLwdata(lwdata);

    }

}
