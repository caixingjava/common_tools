package com.common.tool.solr.service.impl;

import com.common.tool.solr.bean.FieldBean;
import com.common.tool.solr.service.SolrCoreService;
import com.common.tool.solr.util.ConnectSolrUtil;
import com.common.tool.solr.util.CreateConfigFileUtil;
import com.common.tool.solr.util.SolrCoreUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author caixing<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年05月28日 <br>
 */
@Service
public class SolrCoreServiceImpl implements SolrCoreService {

    @Value("${spring.data.solr.zk-host}")
    private String zkHosts;

    @Override
    public boolean createSolrCore(String coreName, List<FieldBean> fieldBeans) throws DocumentException {
        boolean flag = false;
        try {
            String url1 = SolrCoreServiceImpl.class.getResource("/SolrConfTemplate").getPath() + File.separator;
            url1 = url1.replaceAll("%20", " ");
            String configPath = "E:\\zkConfigs\\"+coreName+"\\";
            CreateConfigFileUtil.process(fieldBeans,configPath,url1,coreName,zkHosts);
            flag = SolrCoreUtil.create(getZkHosts(),coreName,1,4);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public void deleteSolrCore(String coreName) {
        CloudSolrClient cloudSolrClient = ConnectSolrUtil.getCloudInstance(getZkHosts(),coreName);
        SolrCoreUtil.deleteExistCore(coreName,cloudSolrClient);
    }

    private List<String> getZkHosts(){
        return Arrays.asList(zkHosts.split(","));
    }
}
