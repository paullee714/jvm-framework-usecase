package com.baul.springbootboardsample.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {


    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("baul"); // TODO: 임의의 데이터 말고 실제 로그인한 사용자의 정보를 가져와야 함
    }
}
