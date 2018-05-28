package com.common.tool.solr.util;

import com.common.tool.solr.bean.FieldBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.solr.common.cloud.ZkConfigManager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * 创建solrcore需要的配置文件
 * 
 * @author Administrator
 *
 */
@Slf4j
public class CreateConfigFileUtil {

	public static boolean process(List<FieldBean> fieldBeans, String confPath, String localConfPath, String coreName,String zkHost) throws DocumentException, IOException {

		mkDir(confPath);
		copyFiles(localConfPath, confPath);
		reWriteSchema(fieldBeans, confPath);
		log.info("the confPath is {}",confPath);
		ZkConfigManager confManager = new ZkConfigManager(new SolrZkClient(zkHost, 10000, 10000));
		confManager.uploadConfigDir(Paths.get(confPath), coreName);

		return true;
	}

	public static void mkDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			log.warn("make dir {} is {}", path, file.mkdirs() == true ? "success" : "fail");
		} else {
			if (!file.delete()) {
				DeleteDirUtil.process(new File(path));
			}
			file.mkdirs();
		}

	}

	private static void copyFiles(String localConfPath, String confPath) {
		try {

			File[] file = (new File(localConfPath)).listFiles();
			for (int i = 0; i < file.length; i++) {
				if (file[i].isFile()) {
					CopyDirUtil.copyFile(file[i], new File(confPath + file[i].getName()));
				}
				if (file[i].isDirectory()) {
					String sorceDir = localConfPath + File.separator + file[i].getName();
					String targetDir = confPath + File.separator + file[i].getName();
					CopyDirUtil.copyDirectiory(sorceDir, targetDir);
				}
			}
			log.info("copy the file is ok !");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("copy file {} is fail,cause by:{}", confPath, e.getMessage());
		}
	}

	private static void reWriteSchema(List<FieldBean> fieldBeans, String confPath) throws DocumentException, IOException {

		SAXReader reader = new SAXReader();
		System.out.println(new File(confPath + "managed-schema").getAbsolutePath());
		Document doc = reader.read(new File(confPath + "managed-schema"));

		Element root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		java.util.List<Element> list = root.content();

		for (FieldBean fieldBean : fieldBeans) {
			String name = fieldBean.getFieldName();
			String fieldType = fieldBean.getFieldType();

			if (fieldBean.isPk()) { // 主键写入uniqueKey
				Element uniqueKey = DocumentHelper.createElement("uniqueKey");
				uniqueKey.setText(name);
				list.add(5, uniqueKey);
			}

			String index = "";
			if (fieldBean.isIndex()) {
				index = "true";
			} else {
				index = "false";
			}
			String store = "";
			if (fieldBean.isStored()) {
				store = "true";
			} else {
				store = "false";
			}

			// 处理copyField
			if (fieldBean.getSrcField() != null && fieldBean.getSrcField().length() > 0) {

				String[] fields = fieldBean.getSrcField().split(",");

				for (String string : fields) {
					String dest = "";
					if (fieldBean.isDynamicField()) {
						dest = name;
					} else {
						dest = name;
					}
					Element elementcopy = DocumentHelper.createElement("copyField").addAttribute("source", string)
							.addAttribute("dest", dest);

					list.add(5, elementcopy);
				}

			}

			String elementType = "field";
			if (fieldBean.isDynamicField()) {
				elementType = "dynamicField";
			}

			// 判断多值
			Element fieldElement = null;
			if (fieldBean.isMuti()) {
				// 写入field
				fieldElement = DocumentHelper.createElement(elementType).addAttribute("name", name)
						.addAttribute("type", fieldType).addAttribute("indexed", index).addAttribute("stored", store)
						.addAttribute("multiValued", "true");

			} else {

				fieldElement = DocumentHelper.createElement(elementType).addAttribute("name", name)
						.addAttribute("type", fieldType).addAttribute("indexed", index).addAttribute("stored", store);
				if (fieldBean.getSrcField() != null && fieldBean.getSrcField().length() > 0) {
					fieldElement.addAttribute("multiValued", "true");
				}
			}
			list.add(5, fieldElement);

		}

		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setEncoding("UTF-8");

		XMLWriter writer = new XMLWriter(new FileWriter(confPath + "managed-schema"), outputFormat);
		writer.write(doc);
		writer.close();
		log.info("write schema is ok !!!");

	}

}
