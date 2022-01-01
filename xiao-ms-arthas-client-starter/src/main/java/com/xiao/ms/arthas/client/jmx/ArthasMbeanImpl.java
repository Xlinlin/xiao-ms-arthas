package com.xiao.ms.arthas.client.jmx;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.agent.attach.ArthasAgent;

import java.util.HashMap;
import java.util.Map;

import com.xiao.ms.arthas.client.ArthasProperties;
import com.xiao.ms.arthas.client.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * jmx实现 <br>
 *
 * @date: 2021/8/22 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
@Component
@ManagedResource(objectName = "ArthasMbean:name=ArthasMbean", description = "Arthas远程管理Mbean")
@Slf4j
public class ArthasMbeanImpl {

    private static final String ARTHAS_AGENT_BEAN_NAME = "arthasAgent";

    private static final String ARTHAS_PREFIX = "arthas.";

    @Autowired
    private Map<String, String> arthasConfigMap;

    @Autowired
    private ArthasProperties arthasProperties;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 初始化&启动arthas
     *
     * @return
     */
    private ArthasAgent arthasAgentInit() {
        arthasConfigMap = StringUtils.removeDashKey(arthasConfigMap);
        // 给配置全加上前缀
        Map<String, String> mapWithPrefix = new HashMap<>(arthasConfigMap.size());
        for (Map.Entry<String, String> entry : arthasConfigMap.entrySet()) {
            mapWithPrefix.put(ARTHAS_PREFIX + entry.getKey(), entry.getValue());
        }
        final ArthasAgent arthasAgent = new ArthasAgent(mapWithPrefix, arthasProperties.getHome(),
                arthasProperties.isSlientInit(), null);
        log.info("开始连接Arthas tunnel server，配置信息：{}", JSON.toJSONString(arthasProperties));
        arthasAgent.init();
        String errorMessage = arthasAgent.getErrorMessage();
        log.info("连接Arthas tunnel server，返回结果：{}", errorMessage);
        return arthasAgent;
    }

    @ManagedOperation(description = "获取Tunnel Server地址")
    public String getArthasTunnelServerUrl() {
        return arthasProperties.getTunnelServer();
    }

    @ManagedOperation(description = "重置Tunnel Server地址(重新attach后生效)")
    @ManagedOperationParameter(name = "tunnelServer", description = "example:ws://127.0.0.1:7777/ws")
    public Boolean setArthasTunnelServerUrl(String tunnelServer) {
        if (tunnelServer == null || tunnelServer.trim().equals("") || tunnelServer.indexOf("ws://") < 0) {
            return false;
        }
        arthasProperties.setTunnelServer(tunnelServer);
        return true;
    }

    @ManagedOperation(description = "获取AgentID")
    public String getAgentId() {
        return arthasProperties.getAgentId();
    }

    @ManagedOperation(description = "获取应用名称")
    public String getAppName() {
        return arthasProperties.getAppName();
    }

    @ManagedOperation(description = "获取Arthas配置")
    public HashMap<String, String> getArthasConfigMap() {
        return (HashMap) arthasConfigMap;
    }

    @ManagedOperation(description = "是否启动Arthas")
    public Boolean isArthasAttched() {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext
                .getAutowireCapableBeanFactory();
        if (defaultListableBeanFactory.containsBean(ARTHAS_AGENT_BEAN_NAME)) {
            return true;
        }
        return false;
    }

    @ManagedOperation(description = "启动Arthas")
    public Boolean startArthasAgent() {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext
                .getAutowireCapableBeanFactory();
        if (defaultListableBeanFactory.containsBean(ARTHAS_AGENT_BEAN_NAME)) {
            ((ArthasAgent) defaultListableBeanFactory.getBean(ARTHAS_AGENT_BEAN_NAME)).init();
            return true;
        }
        defaultListableBeanFactory.registerSingleton(ARTHAS_AGENT_BEAN_NAME, arthasAgentInit());
        return true;
    }

    @ManagedOperation(description = "关闭Arthas(暂未实现)")
    public Boolean stopArthasAgent() {
        // TODO 无法获取自定义tmp文件夹加载的classLoader，因此无法获取到com.taobao.arthas.core.server.ArthasBootstrap类并调用destroy方法
        // TODO 现有官方提供的com.taobao.arthas.agent.attach.ArthasAgent 中启动arthas agent的客户端使用的arthasClassLoader和bootstrapClass均为方法内的临时变量，外部无法获取相关句柄实现通过bootstrapClass关闭arthas agent的功能；
        // TODO 临时解决方案为通过JMX启动后，在web console连接使用后，使用stop命令实现目标进程中 arthas agent的关闭
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext
                .getAutowireCapableBeanFactory();
        if (defaultListableBeanFactory.containsBean(ARTHAS_AGENT_BEAN_NAME)) {
            defaultListableBeanFactory.destroySingleton(ARTHAS_AGENT_BEAN_NAME);
            return true;
        } else {
            return false;
        }
    }
}
