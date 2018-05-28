package com.common.tool.solr.util;

import org.apache.solr.client.solrj.impl.CloudSolrClient;

import java.util.List;
import java.util.Optional;

/**
 * <Description> <br>
 *
 * @author caixing<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年05月28日 <br>
 */
public class ConnectSolrUtil {

    private static final int zkClientTimeout = 20000;
    private static final int zkConnectTimeout = 10000;

    public static CloudSolrClient getCloudInstance(List<String> zkHosts,String collectionName) {
        CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(zkHosts).build();
        client.setDefaultCollection(collectionName);
        client.setZkClientTimeout(zkClientTimeout);
        client.setZkConnectTimeout(zkConnectTimeout);
        client.connect();
        return client;
    }
}
