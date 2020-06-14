package com.example.carparktaxi.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.geojson.GeoJsonObject;
import reactor.core.publisher.Mono;

public interface TaxiService {
    public abstract Mono<GeoJsonObject> getCurrentTaxiLocations();
    public abstract Mono<ObjectNode> getCurrentCarparkAvailabilty();
}
