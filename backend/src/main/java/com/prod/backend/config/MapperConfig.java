package com.prod.backend.config;

import com.prod.backend.mapper.TicketMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    @ConditionalOnMissingBean(TicketMapper.class)
    public TicketMapper ticketMapper() {
        return Mappers.getMapper(TicketMapper.class);
    }
}
