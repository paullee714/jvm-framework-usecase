package com.example.springcloudgateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;


@Component
public class TeamAuthFilter extends AbstractGatewayFilterFactory<TeamAuthFilter.Config> {

    public TeamAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest reactiveRequest = exchange.getRequest();
            ServerHttpResponse reactiveResponse = exchange.getResponse();

            if (!reactiveRequest.getHeaders().containsKey("token")) {
                return handleUnAutorized(exchange);
            }

            List<String> token = reactiveRequest.getHeaders().get("token");
            String myToken = Objects.requireNonNull(token).get(0);

            // 임시
            String tmpAuthToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Indvb2wiLCJpYXQiOjE1MTYyMzkwMjJ9.U7HCQF6Yx6DM29I-w-0uzl6dJTKnJoF_XTao8jYxL4A";


            if (!myToken.equals(tmpAuthToken)) {
                return handleUnAutorized(exchange);
            }

            return chain.filter(exchange);

        });
    }

    private Mono<Void> handleUnAutorized(ServerWebExchange exchange) {
        ServerHttpResponse reactResponse = exchange.getResponse();

        reactResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        return reactResponse.setComplete();
    }

    public static class Config {
    }
}
