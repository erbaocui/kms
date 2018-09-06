package com.thinkgem.jeesite.common.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by home on 2017/11/2.
 */
public class EsClientBuilder {
    private String clusterName;
    private String nodeIpInfo;
    private TransportClient client;

    public Client init()throws Exception{
        try {
            Settings settings = Settings.builder().put("cluster.name",  clusterName).build();
            TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(nodeIpInfo), 9200));
        } catch (UnknownHostException e) {
                e.printStackTrace();
            }


        return client;
    }

    public void close(){
        client.close();
    }

    /**
     * 解析节点IP信息,多个节点用逗号隔开,IP和端口用冒号隔开
     *
     * @return
     */
    private Map<String, Integer> parseNodeIpInfo(){
        String[] nodeIpInfoArr = nodeIpInfo.split(",");
        Map<String, Integer> map = new HashMap<String, Integer>(nodeIpInfoArr.length);
        for (String ipInfo : nodeIpInfoArr){
            String[] ipInfoArr = ipInfo.split(":");
            map.put(ipInfoArr[0], Integer.parseInt(ipInfoArr[1]));
        }
        return map;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNodeIpInfo() {
        return nodeIpInfo;
    }

    public void setNodeIpInfo(String nodeIpInfo) {
        this.nodeIpInfo = nodeIpInfo;
    }

}
