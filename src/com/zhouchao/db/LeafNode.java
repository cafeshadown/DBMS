package com.zhouchao.db;

/**
 * 叶子结点
 */
import java.io.File;
import java.util.List;
import java.util.Set;

public class LeafNode extends Node {
	private String tableName = null;// 表名
	private boolean existCondition = false;// 是否存在where条件
	private List<ConditionalExpression> conditions = null;// 条件表达式的集合

	public LeafNode(boolean isLeafNode, File file, boolean hasFather, Node father, int numberField,
			Set<DisplayField> saveFields, String tableName, boolean existCondition,
			List<ConditionalExpression> conditions) {
		super(isLeafNode, file, hasFather, father, numberField, saveFields);
		this.tableName = tableName;
		this.existCondition = existCondition;
		this.conditions = conditions;
	}

	public LeafNode() {
		super();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isExistCondition() {
		return existCondition;
	}

	public void setExistCondition(boolean existCondition) {
		this.existCondition = existCondition;
	}

	public List<ConditionalExpression> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionalExpression> conditions) {
		this.conditions = conditions;
	}

	@Override
	public String toString() {
		return "LeafNode [tableName=" + tableName + ", existCondition=" + existCondition + ", conditions=" + conditions
				+ ", toString()=" + super.toString() + "]";
	}

	@Override
	public int getAccountNumbers() {
		return super.getAccountNumbers();
	}

	@Override
	public void setAccountNumbers(int accountNumbers) {
		super.setAccountNumbers(accountNumbers);
	}

	@Override
	public int getNumberField() {
		return super.getNumberField();
	}

	@Override
	public void setNumberField(int numberField) {
		super.setNumberField(numberField);
	}

	@Override
	public Set<DisplayField> getSaveFields() {
		return super.getSaveFields();
	}

	@Override
	public void setSaveFields(Set<DisplayField> saveFields) {
		super.setSaveFields(saveFields);
	}

	@Override
	public boolean isLeafNode() {
		return super.isLeafNode();
	}

	@Override
	public void setLeafNode(boolean isLeafNode) {
		super.setLeafNode(isLeafNode);
	}

	@Override
	public File getFile() {
		return super.getFile();
	}

	@Override
	public void setFile(File file) {
		super.setFile(file);
	}

	@Override
	public boolean isHasFather() {
		return super.isHasFather();
	}

	@Override
	public void setHasFather(boolean hasFather) {
		super.setHasFather(hasFather);
	}

	@Override
	public Node getFather() {
		return super.getFather();
	}

	@Override
	public void setFather(Node father) {
		super.setFather(father);
	}

}
