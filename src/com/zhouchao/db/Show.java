package com.zhouchao.db;

public class Show implements Operate{
	private String account = null;

	public Show(String account) {
		this.account = account;
	}
	
	@Override
	public void start() throws Exception {
		switch(ParseAccount.parseShow(this.account)) {
			case 1:
				showDatabase();
				break;
			case 2:
				Check.hadUseDatabase();
				showTables();
				break;
			default:
				System.out.println("Error");
				break;
		}
	}
	/**
	 * ��ʾ�������ݿ�!!!
	 */
	private void showDatabase() {
		
	}
	/**
	 * ��ʾ��ǰ���ݿ��µ����б�!!!
	 */
	private void showTables() {
		System.out.println(DBMS.dataDictionary.getTables());
	}
	
}
