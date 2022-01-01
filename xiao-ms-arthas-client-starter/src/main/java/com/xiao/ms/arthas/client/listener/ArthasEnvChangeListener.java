package com.xiao.ms.arthas.client.listener;

import com.taobao.arthas.agent.attach.ArthasAgent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.xiao.ms.arthas.client.ArthasProperties;
import com.xiao.ms.arthas.client.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

/**
 * 当监听 spring.arthas.enabled 为 true 的时候，注册 Arthas <br>
 * TODO 更改为jmx启动
 * @date: 2021/8/17 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
@Slf4j
public class ArthasEnvChangeListener implements ApplicationListener<EnvironmentChangeEvent> {

    @Autowired
    private Environment env;
    @Autowired
    private Map<String, String> arthasConfigMap;
    @Autowired
    private ArthasProperties arthasProperties;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        for (String key : keys) {
            if ("spring.arthas.enabled".equals(key)) {
                if ("true".equals(env.getProperty(key))) {
                    registerArthas();
                }
            }
        }
    }

    private void registerArthas() {
        log.info("开始将当前服务：{} 注册到arthas server", env.getProperty("spring.application.name"));
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext
                .getAutowireCapableBeanFactory();
        String bean = "arthasAgent";
        if (defaultListableBeanFactory.containsBean(bean)) {
            ((ArthasAgent) defaultListableBeanFactory.getBean(bean)).init();
            log.warn("DefaultListableBeanFactory 已经初始化");
            return;
        }
        defaultListableBeanFactory.registerSingleton(bean, arthasAgentInit());
        log.info("当前服务：{} 注册到arthas server 完成", env.getProperty("spring.application.name"));
    }

    private ArthasAgent arthasAgentInit() {
        arthasConfigMap = StringUtils.removeDashKey(arthasConfigMap);
        // 给配置全加上前缀
        Map<String, String> mapWithPrefix = new HashMap<String, String>(arthasConfigMap.size());
        for (Map.Entry<String, String> entry : arthasConfigMap.entrySet()) {
            mapWithPrefix.put("arthas." + entry.getKey(), entry.getValue());
        }
        log.info("初始化Arthas Agent，服务端：{}", arthasProperties.getTunnelServer());
        final ArthasAgent arthasAgent = new ArthasAgent(mapWithPrefix, arthasProperties.getHome(),
                arthasProperties.isSlientInit(), null);
        arthasAgent.init();
        log.info("初始化Arthas Agent，成功。{}", arthasProperties.getIp());
        return arthasAgent;
    }
}
