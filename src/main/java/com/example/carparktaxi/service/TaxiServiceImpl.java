package com.example.carparktaxi.service;

import com.example.carparktaxi.configuration.DataConfig;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.geojson.GeoJsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class TaxiServiceImpl implements TaxiService {

    private WebClient client;

    @Autowired
    private DataConfig dataConfig;

    @PostConstruct
    private void initWebClient() {

        client = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer
                            .defaultCodecs()
                            .maxInMemorySize(1 * 1024 * 1024))
                    .build())
                .baseUrl(dataConfig.getUrl())
                .build();
    }

    @Override
    public Mono<GeoJsonObject> getCurrentTaxiLocations() {
        Mono<GeoJsonObject> taxi_locations = client.get()
                .uri(uriBuilder -> uriBuilder
                    .path("transport/taxi-availability")
                .queryParam("date_time", "2020-01-01T00:00:00").build())
                .retrieve()
                .bodyToMono(GeoJsonObject.class)
                .onErrorMap((Throwable error) -> error);

        return taxi_locations;
    }

    @Override
    public Mono<ObjectNode> getCurrentCarparkAvailabilty() {

        Mono<ObjectNode> carpark_availability = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("transport/carpark-availability").build())
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .onErrorMap((Throwable error) -> error);

        return carpark_availability;
    }
}
