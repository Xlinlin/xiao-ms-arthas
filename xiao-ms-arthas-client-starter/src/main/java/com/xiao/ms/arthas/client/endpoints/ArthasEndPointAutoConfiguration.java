package com.xiao.ms.arthas.client.endpoints;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author llxiao 2021-09-03
 */
@Configuration
public class ArthasEndPointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public ArthasEndPoint arthasEndPoint() {
        return new ArthasEndPoint();
    }
}
