package com.example.springbootredisdessert.service;

import com.example.springbootredisdessert.domain.Dessert;
import com.example.springbootredisdessert.repository.RedisDessertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class DessertServiceImpl implements DessertService {

    private final RedisDessertRepository redisDessertRepository;

    @Override
    public Mono<Dessert> create(Dessert dessert) {
        return redisDessertRepository.save(dessert);
    }

    @Override
    public Flux<Dessert> getAll() {
        return redisDessertRepository.getAll();
    }

    @Override
    public Mono<Dessert> getOne(String id) {
        return redisDessertRepository.get(id);
    }

    @Override
    public Mono<Long> deleteById(String id) {
        return redisDessertRepository.delete(id);
    }
}
