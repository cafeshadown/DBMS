package com.zhouchao.db;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import static com.zhouchao.db.DBMS.l;

public class Use implements Operate{
	private String account = null;
	private String databaseName = null;
	public static List<String> databases;
	int type =2;
	int type1=2;
	public Use(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		this.databaseName = ParseAccount.parseUse(this.account);
		this.use();
		
	}
	
	/**
	 * ִ��use����!!!
	 * @param
	 */
	private void use() throws Exception {
		OperUtil.persistence();
		DBMS.loadedTables = new HashMap<String, Table>();
       List<String> ds = DBMS.databases;

		InputStreamReader reader = new InputStreamReader(new FileInputStream("admin.txt"));
		BufferedReader bufferedReader = new BufferedReader(reader);
		String b = bufferedReader.readLine();
		FileInputStream fin = new FileInputStream("database.config");
		ObjectInputStream in = new ObjectInputStream(fin);
		l=(Loginpass) in.readObject();
		List<Login> logins1= l.getLogins();
		for (int i = 0; i <logins1.size() ; i++) {
			if (logins1.get(i).loginname.equals(b)){
				type=logins1.get(i).type;
				databases = logins1.get(i).database;
			}
		}
		if(type==1){
			System.out.println("��Ȩ��");
		}else if(type==0){
			for (int i = 0; i <databases.size() ; i++) {
				if(databases.get(i).equals(this.databaseName)){
					type1=1;
				}
			}
			if (type1==1){
				System.out.println("��Ȩ��");
			}else{
				System.out.println("��Ȩ��");
				return;
			}
		}else {
			System.out.println("��Ȩ��");
			return;
		}
		File databaseDir = new File(DBMS.ROOTPATH, this.databaseName);
		if (!databaseDir.exists()) {
			System.out.println("�����ݿⲻ���ڣ����ȴ������ݿ⣡");
			return;
		}
		DBMS.currentPath = DBMS.ROOTPATH + File.separator + this.databaseName;
		
		File file = new File(DBMS.ROOTPATH + File.separator + this.databaseName + ".config");
		ObjectInputStream ois = null;//���ö�����������ȡ�����ļ�
		ObjectOutputStream oos = null;
		try {
			DBMS.dataDictionary = null;
			if (file.exists()) {//������ݿ�Ľṹ�����ļ����ڣ�������ڴ�
				ois = new ObjectInputStream(new FileInputStream(file));
				DBMS.dataDictionary = (DataDictionary) ois.readObject();
				System.out.println("�������ݿ�ṹ�����ļ��ɹ���");
			} else {//����������򴴽�һ�����ݿ�ṹ�����ļ�
				file.createNewFile();//�������ݿ�ṹ�����ļ�
				DBMS.dataDictionary = new DataDictionary(this.databaseName);//�������ݿ�ṹ����
				System.out.println("�������ݿ�ṹ�����ļ��ɹ���");
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
