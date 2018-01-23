package com.zhouchao.db;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BTree {
	public static IndexNode root;

	/**
	 * ����B+��
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
	 * ��B+������Ԫ��
	 * 
	 * @param root
	 * @param node
	 * @param de
	 */
	private static void insert(IndexLeafNode node, DataEntry de) {
		if (node == null) {// ���������ڵ�Ϊ�գ�������Ϊ�գ����½�Ҷ�ӽڵ���Ϊ���ڵ�
			List<DataEntry> data = new LinkedList<DataEntry>();
			data.add(de);
			//û�и��ڵ㣬û���ӽڵ㣬��Ҷ�ڵ㣬���ݣ�������Ҷ�ӽڵ�
			IndexLeafNode leaf = new IndexLeafNode(true, null, null, data, null);
			BTree.root = leaf;
			return;
		}
		boolean isExist = false;
		for (DataEntry e : node.getData()) {// ��Ҷ�ڵ��в����Ƿ�����ͬ���������������ӵ���������ֵ������
			if (e.getKey() == de.getKey()) {
				e.getIndex().addAll(de.getIndex());
				isExist = true;
				break;
			}
		}
		if (!isExist) {// Ҷ�ڵ����޸�����
			node.getData().add(de);// ��ӹؼ������ؼ��ּ���
			Collections.sort(node.getData());// ���ؼ�������
			if (node.getData().get(node.getData().size() - 1).equals(de) && node.getRightNode() == null) {// �������Ĺؼ��ִ��������Ѳ���Ĺؼ��֣��򽫸��׵����ؼ�������Ϊ����Ĺؼ���ֵ
				IndexNode temp = node.getParent();
				while (temp != null) {
					temp.getChilds().get(temp.getChilds().size() - 1).setKey(de.getKey());
					temp = temp.getParent();
				}
			}
			if (node.getData().size() > IndexNode.PNUMBER) {// �ؼ�����Ŀ����ָ������
				BTree.split(node);// ����
			}
		}
	}

	/**
	 * ����
	 * 
	 * @param node
	 */
	private static void split(IndexNode node) {
		long leftkey;
		if (node.isLeaf()) {// Ҷ�ӽڵ�
			IndexLeafNode tn = (IndexLeafNode) node;// ǿ������ת��ΪҶ�ڵ�
			int n = tn.getData().size() / 2;// ����λ��
			leftkey = tn.getData().get(n).getKey();//���Ѵ��Ĺؼ���
			IndexLeafNode newLeaf = new IndexLeafNode(true, null, null, new LinkedList<DataEntry>(), tn.getRightNode());
			while (n-- > 0) {
				newLeaf.getData().add(tn.getData().remove(IndexNode.PNUMBER / 2 + 1));
			}
			tn.setRightNode(newLeaf);
			Collections.sort(newLeaf.getData());
			long rightKey = newLeaf.getData().get(newLeaf.getData().size() - 1).getKey();
			if (node.getParent() == null) {// �Ǹ��ڵ�
				IndexNode newParent = new IndexNode(false, null, new LinkedList<NodeEntry>());
				NodeEntry ln = new NodeEntry(leftkey, node);
				NodeEntry rn = new NodeEntry(rightKey, newLeaf);
				newParent.getChilds().add(ln);
				newParent.getChilds().add(rn);
				node.setParent(newParent);
				newLeaf.setParent(newParent);
				BTree.root = newParent;
			} else {// ���Ǹ����
				IndexNode parent = node.getParent();// ��ȡ���׽��
				for (NodeEntry ne : parent.getChilds()) {// �������׽��
					if (ne.getKey() == rightKey) {
						ne.setKey(leftkey);
						break;
					}
				}
				parent.getChilds().add(new NodeEntry(rightKey, newLeaf));
				newLeaf.setParent(parent);
				Collections.sort(parent.getChilds());
				if (parent.getChilds().size() > IndexNode.PNUMBER) {// ������Ѻ�ĸ��׽�㺢��������ָ���������������
					BTree.split(parent);
				}
			}
		} else {// �ڽ��
			int n = node.getChilds().size() / 2;// ����λ��
			leftkey = node.getChilds().get(n).getKey();// ���Ѵ��Ĺؼ���
			IndexNode newNode = new IndexNode(false, node.getParent(), new LinkedList<NodeEntry>());// �����½��
			while (n-- > 0) {
				newNode.getChilds().add(node.getChilds().remove(IndexNode.PNUMBER / 2 + 1));// �����ѵĽ����ӵ��½ֵ�
				newNode.getChilds().get(newNode.getChilds().size() - 1).getNode().setParent(newNode);// �����½���еĺ��ӽ��ĸ��ڵ�
			}
			long rightKey = newNode.getChilds().get(newNode.getChilds().size() - 1).getKey();
			if (node.getParent() == null) {// �Ǹ��ڵ�
				IndexNode newParent = new IndexNode(false, null, new LinkedList<NodeEntry>());
				NodeEntry ln = new NodeEntry(leftkey, node);
				NodeEntry rn = new NodeEntry(rightKey, newNode);
				newParent.getChilds().add(ln);
				newParent.getChilds().add(rn);
				node.setParent(newParent);
				newNode.setParent(newParent);
				BTree.root = newParent;
			} else {// ���Ǹ��ڵ�
				IndexNode parent = node.getParent();// ��ȡ���׽��
				for (NodeEntry ne : parent.getChilds()) {// �������׽��
					if (ne.getKey() == rightKey) {
						ne.setKey(leftkey);
						break;
					}
				}
				parent.getChilds().add(new NodeEntry(rightKey, newNode));
				newNode.setParent(parent);
				Collections.sort(parent.getChilds());
				if (parent.getChilds().size() > IndexNode.PNUMBER) {// ������Ѻ�ĸ��׽�㺢��������ָ���������������
					BTree.split(parent);
				}
			}
		}

	}

	/**
	 * ���������ؼ��ֲ���λ��,��������ؼ���Ϊ�գ��򷵻�Ҷ�ӽڵ�ĵ�һ����㣨����С�ؼ������ڽ�㣩
	 * 
	 * @param node
	 * @param key
	 * @return
	 */
	public static IndexLeafNode find(IndexNode node, Long key) {
		if (node == null) {// ���û�и��ڵ㣬���ؿ�
			return null;
		}

		if (node.isLeaf()) {// �����Ҷ����������Ҷ�����Ǵ�����λ��
			return (IndexLeafNode) node;
		}

		for (NodeEntry e : node.getChilds()) {// ������ڽڵ�������ӽ��
			if (key <= e.getKey()) {// ���������ؼ���<=ĳ���ӽ��ؼ��֣��Ѵ˺�����Ϊ�����ݹ����
				return BTree.find(e.getNode(), key);
			}
		}

		return BTree.find(node.getChilds().get(node.getChilds().size() - 1).getNode(), key);// ����˽����û�бȴ�����ؼ���С�Ľ�㣬��Ѻ��ӽڵ��йؼ������Ľ����Ϊ���ڵ�ݹ����

	}

	/**
	 * ��ʾB+��
	 */
	public static void display(IndexNode node) {
		IndexLeafNode dis = BTree.find(node, Long.MIN_VALUE);
		while (dis != null) {
			System.out.println(dis.getData());
			dis = dis.getRightNode();
		}
	}
}
