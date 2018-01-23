package com.zhouchao.db;

import java.util.List;
/**
 * B+树叶子结点
 */
public class IndexLeafNode extends IndexNode {
	private List<DataEntry> data;// 数据
	private IndexLeafNode rightNode;// 右叶子结点

	public IndexLeafNode(boolean isLeaf, IndexNode parent, List<NodeEntry> childs, List<DataEntry> data,
			IndexLeafNode rightNode) {
		super(isLeaf, parent, childs);
		this.data = data;
		this.rightNode = rightNode;
	}

	public IndexLeafNode() {
		super();
	}

	public List<DataEntry> getData() {
		return data;
	}

	public void setData(List<DataEntry> data) {
		this.data = data;
	}

	public IndexLeafNode getRightNode() {
		return rightNode;
	}

	public void setRightNode(IndexLeafNode rightNode) {
		this.rightNode = rightNode;
	}

	@Override
	public boolean isLeaf() {
		return super.isLeaf();
	}

	@Override
	public void setLeaf(boolean isLeaf) {
		super.setLeaf(isLeaf);
	}

	@Override
	public IndexNode getParent() {
		return super.getParent();
	}

	@Override
	public void setParent(IndexNode parent) {
		super.setParent(parent);
	}

	@Override
	public List<NodeEntry> getChilds() {
		return super.getChilds();
	}

	@Override
	public void setChilds(List<NodeEntry> childs) {
		super.setChilds(childs);
	}

}
