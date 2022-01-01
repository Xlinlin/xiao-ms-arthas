package com.xiao.ms.arthas.server.arthas.app.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.nacos.common.utils.MapUtils;
import com.xiao.ms.arthas.server.arthas.AgentClusterInfo;
import com.xiao.ms.arthas.server.arthas.AgentInfo;
import com.xiao.ms.arthas.server.arthas.TunnelServer;
import com.xiao.ms.arthas.server.arthas.app.configuration.ArthasProperties;
import com.xiao.ms.arthas.server.arthas.cluster.TunnelClusterStore;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hengyunabc 2020-11-03
 */
@Controller
public class DetailAPIController {

    private final static Logger logger = LoggerFactory.getLogger(DetailAPIController.class);

    @Autowired
    private ArthasProperties arthasProperties;

    @Autowired(required = false)
    private TunnelClusterStore tunnelClusterStore;

    @Autowired
    private TunnelServer tunnelServer;

    @RequestMapping("/api/tunnelApps")
    @ResponseBody
    public Set<String> tunnelApps(HttpServletRequest request, Model model) {
        if (!arthasProperties.isEnableDetatilPages()) {
            throw new IllegalAccessError("not allow");
        }

        Set<String> result = new HashSet<String>();
        if (tunnelClusterStore != null) {
            Collection<String> agentIds = tunnelClusterStore.allAgentIds();

            for (String id : agentIds) {
                String appName = findAppNameFromAgentId(id);
                if (appName != null) {
                    result.add(appName);
                } else {
                    logger.warn("illegal agentId: " + id);
                }
            }
        }

        // 从AgentInfoMap获取
        Map<String, AgentInfo> agentInfoMap = tunnelServer.getAgentInfoMap();
        if (CollectionUtils.isEmpty(result) && MapUtils.isNotEmpty(agentInfoMap)) {
            result = agentInfoMap.keySet();
        }

        return result;
    }

    @RequestMapping("/api/tunnelAgentInfo")
    @ResponseBody
    public Map<String, AgentClusterInfo> tunnelAgentIds(@RequestParam(value = "app", required = true) String appName,
                                                        HttpServletRequest request, Model model) {
        if (!arthasProperties.isEnableDetatilPages()) {
            throw new IllegalAccessError("not allow");
        }

        Map<String, AgentClusterInfo> agentInfos = new HashMap<>();
        if (tunnelClusterStore != null) {
            agentInfos = tunnelClusterStore.agentInfo(appName);
        }

        // 从agent中获取
        Map<String, AgentInfo> agentInfoMap = tunnelServer.getAgentInfoMap();
        if (MapUtils.isEmpty(agentInfos) && MapUtils.isNotEmpty(agentInfoMap)) {
            AgentInfo agentInfo = agentInfoMap.get(appName);
            if (null != agentInfo) {
                AgentClusterInfo agentClusterInfo = new AgentClusterInfo();
                agentClusterInfo.setHost(agentInfo.getHost());
                agentClusterInfo.setPort(agentInfo.getPort());
                agentClusterInfo.setArthasVersion(agentInfo.getArthasVersion());
                agentInfos.put(appName, agentClusterInfo);
            }
        }
        return agentInfos;
    }

    private static String findAppNameFromAgentId(String id) {
        int index = id.indexOf('_');
        if (index < 0 || index >= id.length()) {
            return null;
        }

        return id.substring(0, index);
    }
}
