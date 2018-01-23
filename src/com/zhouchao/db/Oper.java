package com.zhouchao.db;

import java.util.List;

public class Oper {
	private String tableName;// ��������¼�Ĺ�ϵ��
	private boolean existWhereCondition = true;// �Ƿ����where���ʽ
	private List<List<ConditionalExpression>> conditions = null;// �������ʽ�ļ���

	public Oper(String tableName, boolean existWhereCondition, List<List<ConditionalExpression>> conditions) {
		super();
		this.tableName = tableName;
		this.existWhereCondition = existWhereCondition;
		this.conditions = conditions;
	}

	public Oper() {
		super();
	}

	@Override
	public String toString() {
		return "Oper [tableName=" + tableName + ", existWhereCondition=" + existWhereCondition + ", conditions="
				+ conditions + "]";
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isExistWhereCondition() {
		return existWhereCondition;
	}

	public void setExistWhereCondition(boolean existWhereCondition) {
		this.existWhereCondition = existWhereCondition;
	}

	public List<List<ConditionalExpression>> getConditions() {
		return conditions;
	}

	public void setConditions(List<List<ConditionalExpression>> conditions) {
		this.conditions = conditions;
	}

}
