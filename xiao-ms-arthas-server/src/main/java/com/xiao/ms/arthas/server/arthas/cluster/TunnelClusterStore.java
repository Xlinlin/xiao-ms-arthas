package com.xiao.ms.arthas.server.arthas.cluster;

import com.xiao.ms.arthas.server.arthas.AgentClusterInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 保存agentId连接到哪个具体的 tunnel server，集群部署时使用
 *
 * @author hengyunabc 2020-10-27
 */
public interface TunnelClusterStore {
    void addAgent(String agentId, AgentClusterInfo info, long expire, TimeUnit timeUnit);

    AgentClusterInfo findAgent(String agentId);

    void removeAgent(String agentId);

    Collection<String> allAgentIds();

    Map<String, AgentClusterInfo> agentInfo(String appName);
}
