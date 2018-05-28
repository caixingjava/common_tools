package com.common.tool.solr.service;

import com.common.tool.solr.bean.FieldBean;
import org.dom4j.DocumentException;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author caixing<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年05月28日 <br>
 */
public interface SolrCoreService {



    boolean createSolrCore(String coreName, List<FieldBean> fieldBeans) throws DocumentException;

    void deleteSolrCore(String coreName);
}
