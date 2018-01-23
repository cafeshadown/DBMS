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
		switch (ParseAccount.parseDrop(this.account)) {// ����drop��䣬������1����ɾ����ϵ��������2����ɾ��������
		case 1:
			parseDropTable();// ɾ����ϵ��
			break;
		case 2:
			parseDropIndex();// ɾ������
			break;
		default:
			System.out.println("Sentence Error!!");// �������򱨴�
			break;
		}
		parseDropTable();
		
	}
	public void parseDropTable() throws Exception {
		this.name = ParseAccount.parseTableName(this.account);//����drop-table���
		this.dropTable();//ִ��drop-table����
		OperUtil.perpetuateDatabase(DBMS.dataDictionary, DBMS.dataDictionary.getConfigFile());//�־û����ݿ�ṹ
	}
	public void parseDropIndex() {
		this.name = ParseAccount.parseIndexName(this.account);
		this.dropIndex();
	}
	private void dropIndex() {
		if (DBMS.indexEntry.containsKey(this.name)) {
			DBMS.indexEntry.remove(this.name);
			System.out.println("ɾ�������ɹ���");
		} else {
			System.out.println("�����������ڣ�");
		}
	}

	/**
	 * ɾ��������
	 * @param tableName
	 * @throws Exception
	 */
	private void dropTable() throws Exception{
		
		File file = new File(DBMS.currentPath + File.separator + this.name);
		DBMS.dataDictionary.getTables().remove(this.name);//�ӹ�ϵ������ɾ���˱�
		if (file.exists()) {	
			Table table = OperUtil.loadTable(this.name);
			File dataFile = table.getFile();
			File configFile = table.getConfigFile();
			if (dataFile.delete() && configFile.delete()) {//ɾ����ϵ�������ļ��������ֵ��ļ�
				System.out.println(this.name + "ɾ���ɹ���");
				if (DBMS.loadedTables.containsKey(this.name)) {
					DBMS.loadedTables.remove(this.name, table);
				}
				return;
			}
			throw new RuntimeException("������������ռ�ã�ɾ���ļ�ʧ�ܣ�");
			
		} else {
			throw new RuntimeException("�˱����ڣ�");
		}
	}
	
}
