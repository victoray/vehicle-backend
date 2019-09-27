package com.odinson.vehicles.service;

import com.odinson.vehicles.client.maps.MapsClient;
import com.odinson.vehicles.client.prices.PriceClient;
import com.odinson.vehicles.domain.Location;
import com.odinson.vehicles.domain.car.Car;
import com.odinson.vehicles.domain.car.CarRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private MapsClient mapClient;
    private PriceClient priceClient;


    public CarService(CarRepository repository, MapsClient mapClient, PriceClient priceClient) {
        this.repository = repository;
        this.mapClient = mapClient;
        this.priceClient = priceClient;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        List<Car> cars = repository.findAll();
        cars.forEach(x->{
            x.setPrice(priceClient.getPrice(x.getId()));
            x.setLocation(mapClient.getAddress(x.getLocation()));
        });

        return cars;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);

        /**
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
        car.setPrice(priceClient.getPrice(car.getId()));

        /**
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */
        car.setLocation(mapClient.getAddress(car.getLocation()));

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        Car newCar = repository.save(car);
        newCar.setPrice(priceClient.getPrice(car.getId()));
        newCar.setLocation(mapClient.getAddress(car.getLocation()));

        return newCar;
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);

        repository.delete(car);

    }
}
