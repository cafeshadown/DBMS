package com.zhouchao.db;

/**
 * select语句
 */
import java.util.List;
import java.util.Set;

public class Select implements Operate {
	private boolean seeAll = false;// true代表*操作
	private int numberFeld = 0;// 显示的属性数量
	private List<DisplayField> attributes = null;// 显示的属性集合

	private int numberTable = 0;// 查询的关系数量
	private List<String> tableName = null;// 查询的表集合

	private boolean existWhereCondition = true;// 是否存在where条件
	private List<List<ConditionalExpression>> conditions = null;// where条件集合

	private String account = null;// select语句

	public Select(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		long start = System.currentTimeMillis();// 开始时间

		ParseAccount.parseSelect(this, this.account);// 解析select
		List<Node> rootNodes = Tree.buildTree(this);// 创建查询树
		for (Node node : rootNodes) {// 遍历根节点集合
			Tree.traverseTree(node);// 执行查询树
		}
		Set<String> displayAccounts = Tree.getDisplayAccounts(rootNodes);// 获取待投影的记录
		Tree.display(this, displayAccounts, rootNodes.get(0).getSaveFields());// 显示查询结果
		OperUtil.garbageClear(rootNodes);// 释放资源，删除中间文件

		long end = System.currentTimeMillis();// 结束时间
		System.out.println("Time used: " + (end - start) + "ms");
	}

	@Override
	public String toString() {
		return "Select [seeAll=" + seeAll + ", numberFeld=" + numberFeld + ", attributes=" + attributes
				+ ", numberTable=" + numberTable + ", tableName=" + tableName + ", existWhereCondition="
				+ existWhereCondition + ", conditions=" + conditions + ", account=" + account + "]";
	}

	public int getNumberFeld() {
		return numberFeld;
	}

	public void setNumberFeld(int numberFeld) {
		this.numberFeld = numberFeld;
	}

	public List<DisplayField> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<DisplayField> attributes) {
		this.attributes = attributes;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public boolean isSeeAll() {
		return seeAll;
	}

	public void setSeeAll(boolean seeAll) {
		this.seeAll = seeAll;
	}

}
