package com.xiao.ms.arthas.server.arthas.endpoint;

import java.util.HashMap;
import java.util.Map;

import com.xiao.ms.arthas.server.arthas.TunnelServer;
import com.xiao.ms.arthas.server.arthas.app.configuration.ArthasProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "arthas")
public class ArthasEndpoint {

    @Autowired
    private ArthasProperties arthasProperties;
    @Autowired
    private TunnelServer tunnelServer;

    @ReadOperation
    public Map<String, Object> invoke() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("version", this.getClass().getPackage().getImplementationVersion());
        result.put("properties", arthasProperties);

        result.put("agents", tunnelServer.getAgentInfoMap());
        result.put("clientConnections", tunnelServer.getClientConnectionInfoMap());

        return result;
    }

}
