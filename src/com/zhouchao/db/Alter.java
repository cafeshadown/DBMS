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
	private String tableName;// �������ı���
	private boolean isAdd;// true��ʾadd������false��ʾdrop����
	private List<Field> fields = null;// �������������б�

	private String account = null;

	public Alter(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		ParseAccount.parseAlter(this);// ����alter��䣨�������漰�ı����Ͳ������ͣ�
		this.table = OperUtil.loadTable(this.tableName);// ���ز������漰�Ĺ�ϵ��
		ParseAccount.parseAlterAddAndDrop(this);// ����alter-add&alter-drop����
		if (this.isAdd) {
			this.alterAdd();// ִ��alter-add����
			System.out.println("�������Գɹ���");
		} else {
			this.alterDrop();// ִ��alter-drop����
			System.out.println("ɾ�����Գɹ���");
		}
		OperUtil.perpetuateTable(this.table, this.table.getConfigFile());// ���޸ĺ�ı�ṹ�־û�
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
			throw new RuntimeException("��������������������ظ���");
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

				for (int i = 0; i < fields.length; i++) {// ����һ����¼�е�ÿ���ֶ�
					boolean flag = true;
					for (Field field : this.getFields()) {// ��������Ҫɾ��������
						if (field.getColumn() == i) {// ɾ����ɾ�����Զ�Ӧ���ֶ�
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
