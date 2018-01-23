package com.zhouchao.db;

/**
 * select���
 */
import java.util.List;
import java.util.Set;

public class Select implements Operate {
	private boolean seeAll = false;// true����*����
	private int numberFeld = 0;// ��ʾ����������
	private List<DisplayField> attributes = null;// ��ʾ�����Լ���

	private int numberTable = 0;// ��ѯ�Ĺ�ϵ����
	private List<String> tableName = null;// ��ѯ�ı���

	private boolean existWhereCondition = true;// �Ƿ����where����
	private List<List<ConditionalExpression>> conditions = null;// where��������

	private String account = null;// select���

	public Select(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		long start = System.currentTimeMillis();// ��ʼʱ��

		ParseAccount.parseSelect(this, this.account);// ����select
		List<Node> rootNodes = Tree.buildTree(this);// ������ѯ��
		for (Node node : rootNodes) {// �������ڵ㼯��
			Tree.traverseTree(node);// ִ�в�ѯ��
		}
		Set<String> displayAccounts = Tree.getDisplayAccounts(rootNodes);// ��ȡ��ͶӰ�ļ�¼
		Tree.display(this, displayAccounts, rootNodes.get(0).getSaveFields());// ��ʾ��ѯ���
		OperUtil.garbageClear(rootNodes);// �ͷ���Դ��ɾ���м��ļ�

		long end = System.currentTimeMillis();// ����ʱ��
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
