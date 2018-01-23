package com.zhouchao.db;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BTree {
	public static IndexNode root;

	/**
	 * 构造B+树
	 * 
	 * @param
	 * @return
	 */
	public static IndexNode buildBT(List<DataEntry> indexList) {
		BTree.root = null;
		for (DataEntry de : indexList) {
			IndexLeafNode node = BTree.find(BTree.root, de.getKey());
			BTree.insert(node, de);
		}
		return BTree.root;
	}

	/**
	 * 向B+树插入元素
	 * 
	 * @param root
	 * @param node
	 * @param de
	 */
	private static void insert(IndexLeafNode node, DataEntry de) {
		if (node == null) {// 如果待插入节点为空，表明树为空，则新建叶子节点作为根节点
			List<DataEntry> data = new LinkedList<DataEntry>();
			data.add(de);
			//没有父节点，没孩子节点，是叶节点，数据，不是右叶子节点
			IndexLeafNode leaf = new IndexLeafNode(true, null, null, data, null);
			BTree.root = leaf;
			return;
		}
		boolean isExist = false;
		for (DataEntry e : node.getData()) {// 在叶节点中查找是否有相同索引，如果有则添加到该索引的值集合中
			if (e.getKey() == de.getKey()) {
				e.getIndex().addAll(de.getIndex());
				isExist = true;
				break;
			}
		}
		if (!isExist) {// 叶节点中无该索引
			node.getData().add(de);// 添加关键字至关键字集合
			Collections.sort(node.getData());// 将关键字排序
			if (node.getData().get(node.getData().size() - 1).equals(de) && node.getRightNode() == null) {// 如果插入的关键字大于所有已插入的关键字，则将父亲的最大关键字设置为插入的关键字值
				IndexNode temp = node.getParent();
				while (temp != null) {
					temp.getChilds().get(temp.getChilds().size() - 1).setKey(de.getKey());
					temp = temp.getParent();
				}
			}
			if (node.getData().size() > IndexNode.PNUMBER) {// 关键字数目大于指定阶数
				BTree.split(node);// 分裂
			}
		}
	}

	/**
	 * 分裂
	 * 
	 * @param node
	 */
	private static void split(IndexNode node) {
		long leftkey;
		if (node.isLeaf()) {// 叶子节点
			IndexLeafNode tn = (IndexLeafNode) node;// 强制类型转换为叶节点
			int n = tn.getData().size() / 2;// 分裂位置
			leftkey = tn.getData().get(n).getKey();//分裂处的关键字
			IndexLeafNode newLeaf = new IndexLeafNode(true, null, null, new LinkedList<DataEntry>(), tn.getRightNode());
			while (n-- > 0) {
				newLeaf.getData().add(tn.getData().remove(IndexNode.PNUMBER / 2 + 1));
			}
			tn.setRightNode(newLeaf);
			Collections.sort(newLeaf.getData());
			long rightKey = newLeaf.getData().get(newLeaf.getData().size() - 1).getKey();
			if (node.getParent() == null) {// 是根节点
				IndexNode newParent = new IndexNode(false, null, new LinkedList<NodeEntry>());
				NodeEntry ln = new NodeEntry(leftkey, node);
				NodeEntry rn = new NodeEntry(rightKey, newLeaf);
				newParent.getChilds().add(ln);
				newParent.getChilds().add(rn);
				node.setParent(newParent);
				newLeaf.setParent(newParent);
				BTree.root = newParent;
			} else {// 不是根结点
				IndexNode parent = node.getParent();// 获取父亲结点
				for (NodeEntry ne : parent.getChilds()) {// 遍历父亲结点
					if (ne.getKey() == rightKey) {
						ne.setKey(leftkey);
						break;
					}
				}
				parent.getChilds().add(new NodeEntry(rightKey, newLeaf));
				newLeaf.setParent(parent);
				Collections.sort(parent.getChilds());
				if (parent.getChilds().size() > IndexNode.PNUMBER) {// 如果分裂后的父亲结点孩子数大于指定阶数则继续分裂
					BTree.split(parent);
				}
			}
		} else {// 内结点
			int n = node.getChilds().size() / 2;// 分裂位置
			leftkey = node.getChilds().get(n).getKey();// 分裂处的关键字
			IndexNode newNode = new IndexNode(false, node.getParent(), new LinkedList<NodeEntry>());// 创建新结点
			while (n-- > 0) {
				newNode.getChilds().add(node.getChilds().remove(IndexNode.PNUMBER / 2 + 1));// 将分裂的结点添加到新街点
				newNode.getChilds().get(newNode.getChilds().size() - 1).getNode().setParent(newNode);// 更新新结点中的孩子结点的父节点
			}
			long rightKey = newNode.getChilds().get(newNode.getChilds().size() - 1).getKey();
			if (node.getParent() == null) {// 是根节点
				IndexNode newParent = new IndexNode(false, null, new LinkedList<NodeEntry>());
				NodeEntry ln = new NodeEntry(leftkey, node);
				NodeEntry rn = new NodeEntry(rightKey, newNode);
				newParent.getChilds().add(ln);
				newParent.getChilds().add(rn);
				node.setParent(newParent);
				newNode.setParent(newParent);
				BTree.root = newParent;
			} else {// 不是根节点
				IndexNode parent = node.getParent();// 获取父亲结点
				for (NodeEntry ne : parent.getChilds()) {// 遍历父亲结点
					if (ne.getKey() == rightKey) {
						ne.setKey(leftkey);
						break;
					}
				}
				parent.getChilds().add(new NodeEntry(rightKey, newNode));
				newNode.setParent(parent);
				Collections.sort(parent.getChilds());
				if (parent.getChilds().size() > IndexNode.PNUMBER) {// 如果分裂后的父亲结点孩子数大于指定阶数则继续分裂
					BTree.split(parent);
				}
			}
		}

	}

	/**
	 * 查找所给关键字插入位置,如果所给关键字为空，则返回叶子节点的第一个结点（即最小关键字所在结点）
	 * 
	 * @param node
	 * @param key
	 * @return
	 */
	public static IndexLeafNode find(IndexNode node, Long key) {
		if (node == null) {// 如果没有根节点，返回空
			return null;
		}

		if (node.isLeaf()) {// 如果是叶结点则表明此叶结点就是待插入位置
			return (IndexLeafNode) node;
		}

		for (NodeEntry e : node.getChilds()) {// 如果是内节点遍历孩子结点
			if (key <= e.getKey()) {// 如果待插入关键字<=某孩子结点关键字，把此孩子作为根结点递归查找
				return BTree.find(e.getNode(), key);
			}
		}

		return BTree.find(node.getChilds().get(node.getChilds().size() - 1).getNode(), key);// 如果此结点中没有比待插入关键字小的结点，则把孩子节点中关键字最大的结点作为根节点递归查找

	}

	/**
	 * 显示B+树
	 */
	public static void display(IndexNode node) {
		IndexLeafNode dis = BTree.find(node, Long.MIN_VALUE);
		while (dis != null) {
			System.out.println(dis.getData());
			dis = dis.getRightNode();
		}
	}
}
