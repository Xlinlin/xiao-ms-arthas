package com.xiao.ms.arthas.server.arthas.app.web;

import java.util.Map;
import java.util.Set;

import com.xiao.ms.arthas.server.arthas.AgentInfo;
import com.xiao.ms.arthas.server.arthas.TunnelServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取所有注册到 Arthas 的客户端 <br>
 *
 * @date: 2021/8/17 <br>
 * @author: llxiao <br>
 * @since: 1.0 <br>
 * @version: 1.0 <br>
 */
@RequestMapping("/api/arthas")
@RestController
public class AthasController {

    @Autowired
    private TunnelServer tunnelServer;

    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public Set<String> getClients() {
        Map<String, AgentInfo> agentInfoMap = tunnelServer.getAgentInfoMap();
        return agentInfoMap.keySet();
    }


}
