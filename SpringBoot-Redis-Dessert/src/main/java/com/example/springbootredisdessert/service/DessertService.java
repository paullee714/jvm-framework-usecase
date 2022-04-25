package com.example.springbootredisdessert.service;


import com.example.springbootredisdessert.domain.Dessert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DessertService {

    Mono<Dessert> create(Dessert dessert);

    Flux<Dessert> getAll();

    Mono<Dessert> getOne(String id);

    Mono<Long> deleteById(String id);

}
