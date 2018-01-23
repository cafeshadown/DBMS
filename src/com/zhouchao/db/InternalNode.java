package com.zhouchao.db;

/**
 * 内部节点
 */
import java.io.File;
import java.util.List;
import java.util.Set;

public class InternalNode extends Node {

	private int numberTable; // 该内结点下面的关系表数量
	private List<String> tableName = null; // 该内结点下面的所有关系表名

	private boolean IsJoinOperation = true; // true表示是连接操作
	private ConditionalExpression joinCondition; // 连接条件

	private Node leftNode = null;//左孩子
	private Node rightNode = null;//右孩子

	public InternalNode() {
		super();
	}

	public InternalNode(boolean isLeafNode, File file, boolean hasFather, Node father, int numberField,
			Set<DisplayField> saveFields, int numberTable, List<String> tableName, boolean isJoinOperation,
			ConditionalExpression joinCondition, Node leftNode, Node rightNode) {
		super(isLeafNode, file, hasFather, father, numberField, saveFields);
		this.numberTable = numberTable;
		this.tableName = tableName;
		IsJoinOperation = isJoinOperation;
		this.joinCondition = joinCondition;
		this.leftNode = leftNode;
		this.rightNode = rightNode;
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
	public Node getFather() {
		return super.getFather();
	}

	@Override
	public void setFather(Node father) {
		super.setFather(father);
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
	public int getAccountNumbers() {
		return super.getAccountNumbers();
	}

	@Override
	public void setAccountNumbers(int accountNumbers) {
		super.setAccountNumbers(accountNumbers);
	}

	public int getNumberTable() {
		return numberTable;
	}

	public void setNumberTable(int numberTable) {
		this.numberTable = numberTable;
	}

	public List<String> getTableName() {
		return tableName;
	}

	public void setTableName(List<String> tableName) {
		this.tableName = tableName;
	}

	public boolean isIsJoinOperation() {
		return IsJoinOperation;
	}

	public void setIsJoinOperation(boolean isJoinOperation) {
		IsJoinOperation = isJoinOperation;
	}

	public ConditionalExpression getJoinCondition() {
		return joinCondition;
	}

	public void setJoinCondition(ConditionalExpression joinCondition) {
		this.joinCondition = joinCondition;
	}

	public Node getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Node leftNode) {
		this.leftNode = leftNode;
	}

	public Node getRightNode() {
		return rightNode;
	}

	public void setRightNode(Node rightNode) {
		this.rightNode = rightNode;
	}

}
