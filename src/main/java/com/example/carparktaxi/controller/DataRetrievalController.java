package com.example.carparktaxi.controller;

import com.example.carparktaxi.service.TaxiService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.geojson.GeoJsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/data")
public class DataRetrievalController {

    @Autowired
    private TaxiService taxiService;

    @GetMapping(value="/taxi")
    private ResponseEntity<Mono<GeoJsonObject>> getTaxiLocations() {
        log.info("REST controller: getTaxiLocations called");
        Mono<GeoJsonObject> locations = taxiService.getCurrentTaxiLocations();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locations);
    }

    @GetMapping(value="/carpark")
    private ResponseEntity<Mono<ObjectNode>> getCarparkAvailabilty() {
        log.info("REST controller: getCarparkAvailabilty called");
        Mono<ObjectNode> carparkAvailability = taxiService.getCurrentCarparkAvailabilty();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(carparkAvailability);
    }
}
