package com.zhouchao.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

public class Delete extends Oper implements Operate{
	private Table table = null;
	private String account = null;
	
	public Delete(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		ParseAccount.parseDelete(this);
		this.table = OperUtil.loadTable(this.getTableName());
		if (this.delete()) {
			System.out.println("删除记录成功！");
		}
		
	}

	/**
	 * 删除记录!!!
	 */
	private boolean delete() {
		Table table = this.getTable();
		
		BufferedReader br = null;
		PrintWriter pw = null;
		String parentPath = table.getFile().getParent();
		File temp = new File(parentPath + File.separator + "temp");
		if (temp.exists()) {
			temp.delete();
		}
		if (!table.getFile().renameTo(temp)) {
			return false;
		} 
		
		table.setFile(new File(parentPath + File.separator + table.getTableName()));
		
		try {
			table.getFile().createNewFile();
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "GBK"));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(table.getFile())));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				if (!Check.isSatisfiedOper(line, this)) {//保留不满足删除条件的记录
					pw.println(line);
				} 
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (pw != null) {
					pw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			temp.delete();
		}
		
		return false;
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public String getTableName() {
		return super.getTableName();
	}

	@Override
	public void setTableName(String tableName) {
		super.setTableName(tableName);
	}

	@Override
	public boolean isExistWhereCondition() {
		return super.isExistWhereCondition();
	}

	@Override
	public void setExistWhereCondition(boolean existWhereCondition) {
		super.setExistWhereCondition(existWhereCondition);
	}

	@Override
	public List<List<ConditionalExpression>> getConditions() {
		return super.getConditions();
	}

	@Override
	public void setConditions(List<List<ConditionalExpression>> conditions) {
		super.setConditions(conditions);
	}
}
