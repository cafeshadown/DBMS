package com.zhouchao.db;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ���ݿ�
 */
public class DataDictionary implements Serializable {

	private static final long serialVersionUID = -4590671050174187697L;

	private File configFile;
	private String path;// ���ݿ�·��
	private String name;// ���ݿ���
	private List<String> tables;// ���ݿ��µı�������

	public DataDictionary(String name) throws Exception {
		this.name = name;
		this.path = DBMS.ROOTPATH + File.separator + this.name;
		this.configFile = new File(DBMS.ROOTPATH + File.separator + this.name + ".config");
		this.tables = new ArrayList<String>();
	}

	public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
