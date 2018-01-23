package com.zhouchao.db;

import java.util.List;

public class Oper {
	private String tableName;// 待操作记录的关系表
	private boolean existWhereCondition = true;// 是否存在where表达式
	private List<List<ConditionalExpression>> conditions = null;// 条件表达式的集合

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
