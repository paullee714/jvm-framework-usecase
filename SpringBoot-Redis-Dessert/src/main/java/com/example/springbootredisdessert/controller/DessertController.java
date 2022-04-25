package com.example.springbootredisdessert.controller;


import com.example.springbootredisdessert.domain.Dessert;
import com.example.springbootredisdessert.service.DessertServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class DessertController {

    private final DessertServiceImpl dessertService;

    @PostMapping("/dessert")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Dessert> addDessert(@RequestBody @Valid Dessert dessert) {
        return dessertService.create(dessert);
    }

    @GetMapping("/dessert")
    public Flux<Dessert> getAllDessert() {
        return dessertService.getAll();
    }

    @GetMapping("/dessert/{id}")
    public Mono<Dessert> getDessert(@PathVariable String id) {
        return dessertService.getOne(id);
    }

    @DeleteMapping("/dessert/{id}")
    public Mono<Long> deleteDessert(@PathVariable String id) {
        return dessertService.deleteById(id);
    }

}
