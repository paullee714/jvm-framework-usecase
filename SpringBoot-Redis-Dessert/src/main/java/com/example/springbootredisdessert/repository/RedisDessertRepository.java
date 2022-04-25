package com.example.springbootredisdessert.repository;

import com.example.springbootredisdessert.config.ObjectMapperUtils;
import com.example.springbootredisdessert.domain.Dessert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.springbootredisdessert.config.ObjectMapperUtils.DESSERT_KEY;

@Repository
@RequiredArgsConstructor
public class RedisDessertRepository implements DessertRepository {

    private final ReactiveRedisComponent reactiveRedisComponent;

    @Override
    public Mono<Dessert> save(Dessert dessert) {
        return reactiveRedisComponent.set(DESSERT_KEY, dessert.getId(), dessert).map(d -> dessert);
    }

    @Override
    public Mono<Dessert> get(String key) {
        return reactiveRedisComponent.get(DESSERT_KEY, key).flatMap(d -> Mono.just(ObjectMapperUtils.objectMapper(d, Dessert.class)));
    }

    @Override
    public Flux<Dessert> getAll() {
        return reactiveRedisComponent.get(DESSERT_KEY).map(d -> ObjectMapperUtils.objectMapper(d, Dessert.class))
                .collectList().flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Long> delete(String id) {
        return reactiveRedisComponent.remove(DESSERT_KEY, id);
    }
}
