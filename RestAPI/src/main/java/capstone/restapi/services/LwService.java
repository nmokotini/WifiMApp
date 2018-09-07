package capstone.restapi.services;

import capstone.restapi.domain.Lwdata;

import java.util.List;
/*
    Interface class that outlines an LwService class
 */
public interface LwService {

    Lwdata findLwdataById(Long id); //method which will take in an id and return the lwdata object with the parsed id

    List<Lwdata> findAllLwdata(); //method which will return a list of all lwdata objects in the repository/database

    Lwdata saveLwdata(Lwdata lwdata); //method which will save a lwdata object to the repository/database

    List<Lwdata> findLwdataByAge(int age, String type);//method which will return a list of all lwdata within the age limit


}
