package com.zhouchao.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

public class Alter implements Operate {
	private Table table = null;
	private String tableName;// 待操作的表名
	private boolean isAdd;// true表示add操作，false表示drop操作
	private List<Field> fields = null;// 待操作的属性列表

	private String account = null;

	public Alter(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		ParseAccount.parseAlter(this);// 解析alter语句（操作所涉及的表名和操作类型）
		this.table = OperUtil.loadTable(this.tableName);// 加载操作所涉及的关系表
		ParseAccount.parseAlterAddAndDrop(this);// 解析alter-add&alter-drop操作
		if (this.isAdd) {
			this.alterAdd();// 执行alter-add操作
			System.out.println("增加属性成功！");
		} else {
			this.alterDrop();// 执行alter-drop操作
			System.out.println("删除属性成功！");
		}
		OperUtil.perpetuateTable(this.table, this.table.getConfigFile());// 将修改后的表结构持久化
	}

	/**
	 * alter-add!!!!
	 * 
	 * @param table
	 * @param alter
	 * @throws Exception
	 */
	private void alterAdd() throws Exception {
		Table table = this.getTable();

		if (Check.isRepeated(table.getAttributes(), this.getFields())) {
			throw new RuntimeException("添加属性与已有属性名重复！");
		}
		BufferedReader br = null;
		PrintWriter pw = null;
		String parentPath = table.getFile().getParent();
		File temp = new File(parentPath + File.separator + "temp");
		if (temp.exists()) {
			temp.delete();
		}
		table.getFile().renameTo(temp);
		table.setFile(new File(parentPath + File.separator + table.getTableName()));

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "GBK"));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(table.getFile()), "GBK"));
			String line = null;
			String add = "";
			for (int i = 0; i < this.getFields().size(); i++) {
				add += ",null";
			}
			while ((line = br.readLine()) != null) {
				line += add;
				pw.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				if (br != null) {
					br.close();
					temp.delete();
				}
				if (pw != null) {
					pw.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		table.getAttributes().addAll(this.getFields());
	}

	/**
	 * alter-drop!!!
	 * 
	 * @param table
	 * @param alter
	 */
	private void alterDrop() {
		Table table = this.getTable();

		BufferedReader br = null;
		PrintWriter pw = null;
		String parentPath = table.getFile().getParent();
		File temp = new File(parentPath + File.separator + "temp");
		if (temp.exists()) {
			temp.delete();
		}
		table.getFile().renameTo(temp);
		table.setFile(new File(parentPath + File.separator + table.getTableName()));
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "GBK"));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(table.getFile()), "GBK"), true);

			String line = null;
			String[] fields = null;

			while ((line = br.readLine()) != null) {
				String newLine = "";
				fields = line.split(",");

				for (int i = 0; i < fields.length; i++) {// 遍历一条记录中的每个字段
					boolean flag = true;
					for (Field field : this.getFields()) {// 遍历所有要删除的属性
						if (field.getColumn() == i) {// 删除待删除属性对应的字段
							flag = false;
							break;
						}
					}
					if (flag == true) {
						newLine = newLine + fields[i] + ",";
					}
				}
				if (newLine.length() != 0) {
					if (newLine.endsWith(",")) {
						newLine = newLine.substring(0, newLine.length() - 1);
					}
					pw.println(newLine);
				}
			}
			for (Field field : this.getFields()) {
				table.getAttributes().remove(field);
			}
			int column = 0;
			for (Field field : table.getAttributes()) {
				field.setColumn(column);
				column += 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				if (br != null) {
					br.close();
					temp.delete();
				}
				if (pw != null) {
					pw.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "Alter [tableName=" + tableName + ", isAdd=" + isAdd + ", fields=" + fields + "]";
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
