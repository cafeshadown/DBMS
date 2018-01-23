package com.zhouchao.db;

public class ConditionalExpression {
	private boolean isLeftConstant = true;// 条件表达式的左部是否为常量
	private String left = null;// 条件表达式的左部
	private String leftType;// 条件表达式左部的类型
	private String leftTableName;// 当条件表达式左部是属性时，所涉及的表名
	private String leftAttribute;// 左部属性名
	private int leftcolumn;// 如果是属性，所在列
	private boolean leftIsIndex = false;// 是否为索引列
	private IndexNode leftRoot = null;// 左属性索引树树根

	private boolean isRightConstant = true;// 条件表达式右部是否为常量
	private String right = null;// 条件表达式的右部
	private String rightType;// 条件表达式右部的类型
	private String rightTableName;// 当条件表达式右部是属性时，所涉及的表名
	private String rightAttribute;// 右部属性名
	private int rightcolumn;// 如果是属性，所在列
	private boolean rightIsIndex = false;// 是否为索引列
	private IndexNode rightRoot = null;// 右属性索引树树根

	private String oper;// 条件表达式的比较符
						// GT(">"),LT("<"),EQUAL("="),GTOREQUAL(">="),LTOREQUAL("<=")

	public ConditionalExpression() {
		super();
	}

	public ConditionalExpression(ConditionalExpression c) {
		super();
		this.isLeftConstant = c.isLeftConstant;
		this.left = c.left;
		this.leftType = c.leftType;
		this.leftTableName = c.leftTableName;
		this.leftAttribute = c.leftAttribute;
		this.leftcolumn = c.leftcolumn;
		this.leftIsIndex = c.leftIsIndex;
		this.leftRoot = c.leftRoot;
		this.isRightConstant = c.isRightConstant;
		this.right = c.right;
		this.rightType = c.rightType;
		this.rightTableName = c.rightTableName;
		this.rightAttribute = c.rightAttribute;
		this.rightcolumn = c.rightcolumn;
		this.rightIsIndex = c.rightIsIndex;
		this.rightRoot = c.rightRoot;
		this.oper = c.oper;
	}

	public ConditionalExpression(boolean isLeftConstant, String left, String leftType, String leftTableName,
			String leftAttribute, int leftcolumn, boolean leftIsIndex, IndexNode leftRoot, boolean isRightConstant,
			String right, String rightType, String rightTableName, String rightAttribute, int rightcolumn,
			boolean rightIsIndex, IndexNode rightRoot, String oper) {
		super();
		this.isLeftConstant = isLeftConstant;
		this.left = left;
		this.leftType = leftType;
		this.leftTableName = leftTableName;
		this.leftAttribute = leftAttribute;
		this.leftcolumn = leftcolumn;
		this.leftIsIndex = leftIsIndex;
		this.leftRoot = leftRoot;
		this.isRightConstant = isRightConstant;
		this.right = right;
		this.rightType = rightType;
		this.rightTableName = rightTableName;
		this.rightAttribute = rightAttribute;
		this.rightcolumn = rightcolumn;
		this.rightIsIndex = rightIsIndex;
		this.rightRoot = rightRoot;
		this.oper = oper;
	}

	@Override
	public String toString() {
		return "ConditionalExpression [isLeftConstant=" + isLeftConstant + ", left=" + left + ", leftType=" + leftType
				+ ", leftTableName=" + leftTableName + ", leftAttribute=" + leftAttribute + ", leftcolumn=" + leftcolumn
				+ ", leftIsIndex=" + leftIsIndex + ", leftRoot=" + leftRoot + ", isRightConstant=" + isRightConstant
				+ ", right=" + right + ", rightType=" + rightType + ", rightTableName=" + rightTableName
				+ ", rightAttribute=" + rightAttribute + ", rightcolumn=" + rightcolumn + ", rightIsIndex="
				+ rightIsIndex + ", rightRoot=" + rightRoot + ", oper=" + oper + "]";
	}

	public IndexNode getLeftRoot() {
		return leftRoot;
	}

	public void setLeftRoot(IndexNode leftRoot) {
		this.leftRoot = leftRoot;
	}

	public IndexNode getRightRoot() {
		return rightRoot;
	}

	public void setRightRoot(IndexNode rightRoot) {
		this.rightRoot = rightRoot;
	}

	public boolean isLeftIsIndex() {
		return leftIsIndex;
	}

	public void setLeftIsIndex(boolean leftIsIndex) {
		this.leftIsIndex = leftIsIndex;
	}

	public boolean isRightIsIndex() {
		return rightIsIndex;
	}

	public void setRightIsIndex(boolean rightIsIndex) {
		this.rightIsIndex = rightIsIndex;
	}

	public int getLeftcolumn() {
		return leftcolumn;
	}

	public void setLeftcolumn(int leftcolumn) {
		this.leftcolumn = leftcolumn;
	}

	public int getRightcolumn() {
		return rightcolumn;
	}

	public void setRightcolumn(int rightcolumn) {
		this.rightcolumn = rightcolumn;
	}

	public boolean isLeftConstant() {
		return isLeftConstant;
	}

	public void setLeftConstant(boolean isLeftConstant) {
		this.isLeftConstant = isLeftConstant;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getLeftType() {
		return leftType;
	}

	public void setLeftType(String leftType) {
		this.leftType = leftType;
	}

	public String getLeftTableName() {
		return leftTableName;
	}

	public void setLeftTableName(String leftTableName) {
		this.leftTableName = leftTableName;
	}

	public String getLeftAttribute() {
		return leftAttribute;
	}

	public void setLeftAttribute(String leftAttribute) {
		this.leftAttribute = leftAttribute;
	}

	public boolean isRightConstant() {
		return isRightConstant;
	}

	public void setRightConstant(boolean isRightConstant) {
		this.isRightConstant = isRightConstant;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public String getRightType() {
		return rightType;
	}

	public void setRightType(String rightType) {
		this.rightType = rightType;
	}

	public String getRightTableName() {
		return rightTableName;
	}

	public void setRightTableName(String rightTableName) {
		this.rightTableName = rightTableName;
	}

	public String getRightAttribute() {
		return rightAttribute;
	}

	public void setRightAttribute(String rightAttribute) {
		this.rightAttribute = rightAttribute;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

}
