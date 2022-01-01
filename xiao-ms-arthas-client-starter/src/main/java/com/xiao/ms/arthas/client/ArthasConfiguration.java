package com.xiao.ms.arthas.client;

import java.util.HashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName: arthas agent加载，需要扫描包，加载jmx的信息，通过jmx的方式启动arthas agent
 * @Description: 线程池初始化
 * @author: xiaolinlin
 * @date: 2020/9/1 13:45
 **/
@EnableConfigurationProperties({ArthasProperties.class})
@ComponentScan("com.xiaogj.arthas.spring")
public class ArthasConfiguration {


    @ConfigurationProperties(prefix = "arthas")
    @ConditionalOnMissingBean
    @Bean
    public HashMap<String, String> arthasConfigMap() {
        return new HashMap<>();
    }
}