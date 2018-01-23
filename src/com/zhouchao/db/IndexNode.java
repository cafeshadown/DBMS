package com.zhouchao.db;

import java.util.List;
/**
 * B+树内节点
 */
public class IndexNode {
	public static final int PNUMBER = 6;//每个结点的最大孩子数
	private boolean isLeaf;//是否是叶结点
	private IndexNode parent;//父亲结点
	private List<NodeEntry> childs;//孩子结点

	public IndexNode(boolean isLeaf, IndexNode parent, List<NodeEntry> childs2) {
		super();
		this.isLeaf = isLeaf;
		this.parent = parent;
		this.childs = childs2;
	}

	public IndexNode() {
		super();
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public IndexNode getParent() {
		return parent;
	}

	public void setParent(IndexNode parent) {
		this.parent = parent;
	}

	public List<NodeEntry> getChilds() {
		return childs;
	}

	public void setChilds(List<NodeEntry> childs) {
		this.childs = childs;
	}

}
