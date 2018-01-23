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
	 * 执行use操作!!!
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
			System.out.println("有权限");
		}else if(type==0){
			for (int i = 0; i <databases.size() ; i++) {
				if(databases.get(i).equals(this.databaseName)){
					type1=1;
				}
			}
			if (type1==1){
				System.out.println("有权限");
			}else{
				System.out.println("无权限");
				return;
			}
		}else {
			System.out.println("无权限");
			return;
		}
		File databaseDir = new File(DBMS.ROOTPATH, this.databaseName);
		if (!databaseDir.exists()) {
			System.out.println("此数据库不存在，请先创建数据库！");
			return;
		}
		DBMS.currentPath = DBMS.ROOTPATH + File.separator + this.databaseName;
		
		File file = new File(DBMS.ROOTPATH + File.separator + this.databaseName + ".config");
		ObjectInputStream ois = null;//利用对象输入流读取配置文件
		ObjectOutputStream oos = null;
		try {
			DBMS.dataDictionary = null;
			if (file.exists()) {//如果数据库的结构定义文件存在，则读入内存
				ois = new ObjectInputStream(new FileInputStream(file));
				DBMS.dataDictionary = (DataDictionary) ois.readObject();
				System.out.println("加载数据库结构定义文件成功！");
			} else {//如果不存在则创建一个数据库结构定义文件
				file.createNewFile();//创建数据库结构定义文件
				DBMS.dataDictionary = new DataDictionary(this.databaseName);//创建数据库结构对象
				System.out.println("创建数据库结构定义文件成功！");
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
