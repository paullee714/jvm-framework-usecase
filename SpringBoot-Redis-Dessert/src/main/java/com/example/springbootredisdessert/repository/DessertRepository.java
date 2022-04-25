package com.example.springbootredisdessert.repository;


import com.example.springbootredisdessert.domain.Dessert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DessertRepository {

    Mono<Dessert> save(Dessert dessert);

    Mono<Dessert> get(String key);

    Flux<Dessert> getAll();

    Mono<Long> delete(String id);
}
