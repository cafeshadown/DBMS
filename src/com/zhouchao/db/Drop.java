package com.zhouchao.db;

import java.io.File;

public class Drop implements Operate{
	private String account = null;
	private String name = null;
	
	public Drop(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		switch (ParseAccount.parseDrop(this.account)) {// 解析drop语句，若返回1则是删除关系表，若返回2则是删除索引。
		case 1:
			parseDropTable();// 删除关系表
			break;
		case 2:
			parseDropIndex();// 删除索引
			break;
		default:
			System.out.println("Sentence Error!!");// 都不是则报错
			break;
		}
		parseDropTable();
		
	}
	public void parseDropTable() throws Exception {
		this.name = ParseAccount.parseTableName(this.account);//解析drop-table语句
		this.dropTable();//执行drop-table操作
		OperUtil.perpetuateDatabase(DBMS.dataDictionary, DBMS.dataDictionary.getConfigFile());//持久化数据库结构
	}
	public void parseDropIndex() {
		this.name = ParseAccount.parseIndexName(this.account);
		this.dropIndex();
	}
	private void dropIndex() {
		if (DBMS.indexEntry.containsKey(this.name)) {
			DBMS.indexEntry.remove(this.name);
			System.out.println("删除索引成功！");
		} else {
			System.out.println("此索引不存在！");
		}
	}

	/**
	 * 删除表！！！
	 * @param tableName
	 * @throws Exception
	 */
	private void dropTable() throws Exception{
		
		File file = new File(DBMS.currentPath + File.separator + this.name);
		DBMS.dataDictionary.getTables().remove(this.name);//从关系表集合中删除此表
		if (file.exists()) {	
			Table table = OperUtil.loadTable(this.name);
			File dataFile = table.getFile();
			File configFile = table.getConfigFile();
			if (dataFile.delete() && configFile.delete()) {//删除关系表数据文件和数据字典文件
				System.out.println(this.name + "删除成功！");
				if (DBMS.loadedTables.containsKey(this.name)) {
					DBMS.loadedTables.remove(this.name, table);
				}
				return;
			}
			throw new RuntimeException("其他程序正在占用，删除文件失败！");
			
		} else {
			throw new RuntimeException("此表不存在！");
		}
	}
	
}
