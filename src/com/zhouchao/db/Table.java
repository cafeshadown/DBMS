package com.zhouchao.db;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/*
 * 数据库表
 * 
 * 字段：表名(String)，
 * 		 属性名和属性类型（Map<String, List>）
 * 
 * 方法：void insert() 数据库记录插入
 * 		 void delete() 数据库记录删除
 * 		 void update() 数据库记录修改
 */
public class Table implements Serializable {

	private static final long serialVersionUID = -530965201790985804L;

	private String tableName;// 关系表名
	private File file;// 关系表数据文件
	private File configFile;// 关系表配置文件
	private List<Field> attributes;// 属性集合

	public Table(String tableName, File file, File configFile, List<Field> attributes) {
		super();
		this.tableName = tableName;
		this.file = file;
		this.attributes = attributes;
		this.configFile = configFile;
	}

	@Override
	public String toString() {
		return "Table [tableName=" + tableName + ", file=" + file + ", configFile=" + configFile + ", attributes="
				+ attributes + "]";
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Field> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Field> attributes) {
		this.attributes = attributes;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

}
