package com.xiao.ms.arthas.client.endpoints;

import com.taobao.arthas.agent.attach.ArthasAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * arthas actuator 开放端点
 *
 * @author llxiao 2021-09-03
 */
@Endpoint(id = "arthas")
public class ArthasEndPoint {

    @Autowired(required = false)
    private ArthasAgent arthasAgent;

    @Autowired(required = false)
    private HashMap<String, String> arthasConfigMap;

    @ReadOperation
    public Map<String, Object> invoke() {
        Map<String, Object> result = new HashMap<String, Object>();

        if (arthasConfigMap != null) {
            result.put("arthasConfigMap", arthasConfigMap);
        }
        if (null != arthasAgent) {
            String errorMessage = arthasAgent.getErrorMessage();
            if (errorMessage != null) {
                result.put("errorMessage", errorMessage);
            }
        }
        return result;
    }

}
