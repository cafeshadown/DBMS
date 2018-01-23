package com.zhouchao.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class OperUtil {
	/**
	 * 从键盘接收命令语句
	 * 
	 * @param scan
	 */
	public static String input(Scanner scan) throws Exception {
		System.out.print(">>");
		String account = scan.nextLine();
		while (!account.endsWith(";")) {// 若以";"结尾,则结束输入
			System.out.print(">>");
			account = account + " " + scan.nextLine();// next和nextLine的区别
		}
		if ("exit;".equalsIgnoreCase(account)) {// 退出数据库时持久化数据字典
			OperUtil.persistence();// 持久化数据字典
			scan.close();// 关闭控制台输入流
			throw new RuntimeException("退出数据库成功！");
		}
		return account;
	}

	/**
	 * select语句执行结束后，释放资源，删除中间文件!!!
	 * @param rootNodes 
	 */
	public static void garbageClear(Collection<Node> rootNodes) {
		for (Node node : rootNodes) {
			node.getFile().delete();
		}
		Tree.leaves = null;
		Tree.inners = null;
	}

	/**
	 * 持久化关系表结构定义文件和数据库结构定义文件!!!
	 */
	public static void persistence() {
		ObjectOutputStream oos = null;
		try {
			/*
			 * 持久化数据库结构定义对象
			 */
			if (DBMS.dataDictionary != null) {
				oos = new ObjectOutputStream(new FileOutputStream(DBMS.dataDictionary.getConfigFile()));
				oos.writeObject(DBMS.dataDictionary);
			}
			/*
			 * 持久化关系表结构定义对象
			 */
			if (DBMS.loadedTables != null) {
				for (Table table : DBMS.loadedTables.values()) {
					table.getConfigFile().delete();
					oos = new ObjectOutputStream(new FileOutputStream(table.getConfigFile()));
					oos.writeObject(table);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("关系表持久化失败！");
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 加载关系表!!!
	 */
	public static Table loadTable(String tableName) throws Exception {
		File file = new File(DBMS.currentPath + File.separator + tableName + ".config");
		if (!file.exists()) {
			throw new RuntimeException("语句非法！没有" + tableName + "这个表");
		}
		if (DBMS.loadedTables.containsKey(tableName)) {//如果已加载关系表集合中含有此关系表则返回此关系表
			return DBMS.loadedTables.get(tableName);
		}
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Table table = (Table) ois.readObject();
		if (ois != null) {
			ois.close();
		}
		if (table != null) {
			DBMS.loadedTables.put(tableName, table);// 将加载的关系表添加至已加载关系表map
			return table;
		} else {
			throw new RuntimeException("读取关系表配置信息出错！");
		}
	}

	/**
	 * 获取指定字段索引集合
	 * 
	 * @param file
	 * @param column
	 * @return
	 */
	public static List<DataEntry> getIndex(File file, int column, String type) {
		List<DataEntry> indexList = new ArrayList<DataEntry>();//定义索引数据集合
		DataEntry de = null;

		BufferedReader br = null;
		String line = null;
		int index = 0;// 第一行从0开始
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
			long key;
			if ("Integer".equalsIgnoreCase(type)) {//如果待创建索引列值是数值类型
				while ((line = br.readLine()) != null) {
					key = Long.parseLong(line.split(",")[column]);//将字符串转化为数值作为索引的key
					de = new DataEntry(key, new HashSet<Integer>());
					de.getIndex().add(index++);
					indexList.add(de);
				}
			} else if ("Varchar".equalsIgnoreCase(type)) {//如果待创建索引列值是字符串类型
				while ((line = br.readLine()) != null) {
					key = line.split(",")[column].hashCode();//将字符串的哈希值作为索引的key
					de = new DataEntry(key, new HashSet<Integer>());
					de.getIndex().add(index++);
					indexList.add(de);
				}
			} else {
				throw new RuntimeException("无此类型！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return indexList;
	}
	/**
	 * 获取选择条件的索引值集合
	 * 		null 表示此选择条件涉及的属性不带有索引
	 * 		Set<Integer> 索引值集合
	 */
	public static Set<Integer> getIndexOfBT(ConditionalExpression c) {

		Set<Integer> indexs = null;
		String cons = c.isLeftConstant() ? c.getLeft() : c.getRight();//获取待比较常量
		String type = c.isLeftConstant() ? c.getRightType() : c.getLeftType();
		long constant = 0;
		if ("Integer".equalsIgnoreCase(type)) {
			constant = Integer.parseInt(cons);
		} else if ("Varchar".equalsIgnoreCase(type)) {
			constant = cons.hashCode();
		} else {
			throw new RuntimeException("无此类型！");
		}
		IndexNode root = c.isLeftIsIndex() ? c.getLeftRoot() : c.getRightRoot();//获取索引树的根节点
		IndexLeafNode now = BTree.find(root, constant);//查找要比较的常量所在的叶节点
		
		if ("<".equals(c.getOper()) && c.isLeftIsIndex() || ">".equals(c.getOper()) && c.isRightIsIndex()) {
			indexs = new HashSet<Integer>();
			IndexLeafNode minNode = BTree.find(c.getLeftRoot(), Long.MIN_VALUE);//查找最小关键字所在节点
			while (minNode != null && !minNode.equals(now)) {//获取当前叶结点所存的数据（即记录所在行）
				for (DataEntry de : minNode.getData()) {//遍历当前叶结点下的数据集合
					indexs.addAll(de.getIndex());
				}
				minNode = minNode.getRightNode();
			}
			for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
				if (de.getKey() >= constant) {//当前关键字大于等于待比较常量，则不添加直接跳出循环
					break;
				}
				indexs.addAll(de.getIndex());
			}
		} else if ("<=".equals(c.getOper()) && c.isLeftIsIndex() || ">=".equals(c.getOper()) && c.isRightIsIndex()) {
			indexs = new HashSet<Integer>();
			IndexLeafNode minNode = BTree.find(c.getLeftRoot(), Long.MIN_VALUE);//查找最小关键字所在节点
			while (minNode != null && !minNode.equals(now)) {//获取当前叶结点所存的数据（即记录所在行）
				for (DataEntry de : minNode.getData()) {//遍历当前叶结点下的数据集合
					indexs.addAll(de.getIndex());
				}
				minNode = minNode.getRightNode();
			}
			for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
				if (de.getKey() > constant) {//当前关键字大于待比较常量，则不添加直接跳出循环
					break;
				}
				indexs.addAll(de.getIndex());
			}
		} else if ("=".equals(c.getOper()) && (c.isLeftIsIndex() || c.isRightIsIndex())) {
			indexs = new HashSet<Integer>();
			for (DataEntry de : now.getData()) {
				if (de.getKey() == constant) {
					indexs.addAll(de.getIndex());
				}
			}
		} else if (">".equals(c.getOper())  && c.isLeftIsIndex() || "<".equals(c.getOper()) && c.isRightIsIndex()) {
			indexs = new HashSet<Integer>();
			for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
				if (de.getKey() <= constant) {//当前关键字小于等于待比较常量，则不添加继续下一次循环
					continue;
				}
				indexs.addAll(de.getIndex());
			}
			now = now.getRightNode();
			while (now != null) {//获取当前叶结点所存的数据（即记录所在行）
				for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
					indexs.addAll(de.getIndex());
				}
				now = now.getRightNode();
			}
		} else if (">=".equals(c.getOper())  && c.isLeftIsIndex() || "<=".equals(c.getOper()) && c.isRightIsIndex()) {
			indexs = new HashSet<Integer>();
			for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
				if (de.getKey() < constant) {//当前关键字小于待比较常量，则不添加继续下一次循环
					continue;
				}
				indexs.addAll(de.getIndex());
			}
			now = now.getRightNode();
			while (now != null) {//获取当前叶结点所存的数据（即记录所在行）
				for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
					indexs.addAll(de.getIndex());
				}
				now = now.getRightNode();
			}
		}
		
		return indexs;
	}
	/**
	 * 持久化关系表结构
	 * @param table
	 * @param file
	 * @throws Exception
	 */
	public static void perpetuateTable(Table table, File file) throws Exception {
		/*
		 * 将配置信息写入configFile
		 */
		file.delete();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(table);// 将关系表结构信息持久化
		oos.close();
	}
	/**
	 * 持久化数据库结构
	 * @param dd
	 * @param file
	 * @throws Exception
	 */
	public static void perpetuateDatabase(DataDictionary dd, File file) throws Exception {
		file.delete();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(dd);
		oos.close();
	}
}
