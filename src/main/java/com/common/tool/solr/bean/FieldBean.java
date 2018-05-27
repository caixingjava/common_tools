package com.common.tool.solr.bean;

import lombok.Data;

/**
 * 每个字段的属性值
 * @author Administrator
 *
 */
@Data
public class FieldBean {
	
	
	/**
	 * 字段名称
	 */
	private String fieldName;
	
	/**
	 * 字段类型
	 */
	private String fieldType;
	
	/**
	 * 是否为可检索
	 */
	private boolean isIndex;
	
	/**
	 * 是否为可存储。可展示
	 */
	private boolean isStored;
	
	/**
	 * 是否主键
	 */
	private boolean isPk;
	
	/**
	 * 是否为多值
	 */
	private boolean isMuti = false;
	
	/**
	 * 当为动态字段时，指定的源字段
	 */
	private String srcField;
	
	/**
	 * 是否为动态字段
	 */
	private boolean isDynamicField = false;
	
	

}
