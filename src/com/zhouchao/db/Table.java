package com.zhouchao.db;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/*
 * ���ݿ��
 * 
 * �ֶΣ�����(String)��
 * 		 ���������������ͣ�Map<String, List>��
 * 
 * ������void insert() ���ݿ��¼����
 * 		 void delete() ���ݿ��¼ɾ��
 * 		 void update() ���ݿ��¼�޸�
 */
public class Table implements Serializable {

	private static final long serialVersionUID = -530965201790985804L;

	private String tableName;// ��ϵ����
	private File file;// ��ϵ�������ļ�
	private File configFile;// ��ϵ�������ļ�
	private List<Field> attributes;// ���Լ���

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
