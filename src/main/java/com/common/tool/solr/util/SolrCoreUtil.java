package com.common.tool.solr.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.CoreStatus;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.codehaus.jackson.map.util.JSONPObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class SolrCoreUtil {

    public static boolean create(List<String> zkHosts, String coreName, Integer shardNum, Integer replicateum) throws IOException, SolrServerException {
        // 得到一个client实例
        CloudSolrClient client = ConnectSolrUtil.getCloudInstance(zkHosts, coreName);

        // 删除已存在的核
        deleteExistCore(coreName, client);

        // 建立新核
        CollectionAdminRequest.Create req = CollectionAdminRequest.createCollection(coreName, shardNum, replicateum);
        CollectionAdminResponse rsp = req.process(client);

        if (!rsp.isSuccess()) {
            log.error("create core is fail !!! cause by:{}", rsp.getResponse().toString());
            throw new RuntimeException("creating index failed, error: " + rsp.getResponse().toString());
        }
        return true;
    }

    public static void deleteExistCore(String coreName, CloudSolrClient client) {
        try {
            if (solrcoreIsExist(coreName, client)) {
                client.deleteByQuery("*:*");
                CollectionAdminRequest.Delete delete = CollectionAdminRequest.deleteCollection(coreName);
                delete.process(client);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean solrcoreIsExist(String coreName, CloudSolrClient client) {
        CoreAdminRequest req = new CoreAdminRequest();
        boolean existFlag = false;
        try {
            CoreAdminRequest.getCoreStatus(coreName, client);
            req.setAction(CoreAdminParams.CoreAdminAction.STATUS);
            req.setIndexInfoNeeded(true);
            CoreAdminResponse response = req.process(client);
            SimpleOrderedMap map = (SimpleOrderedMap) response.getResponse().get("status");
            for(int i = 0;i<map.size();i++){
                if(map.getName(i).startsWith(coreName)){
                    existFlag = true;
                    break;
                }
            }

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existFlag;
    }

    public static void main(String[] args) {
        List<String> zkHosts = new ArrayList<String>();
        zkHosts.add("172.16.9.33:2181");
        zkHosts.add("172.16.9.33:2182");
        zkHosts.add("172.16.9.33:2183");
        try {
            create(zkHosts, "faceset5", 1, 4);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

}
