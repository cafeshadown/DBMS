package com.zhouchao.db;

import java.util.ArrayList;
import java.util.List;
/**
 * ��ϣ��Ľ��
 *
 */
public class HashNode {
	private boolean isEmpty;//�Ƿ�Ϊ��
	private long key;// �ؼ���
	private List<Integer> indexs;// ֵ��
	private int count;// ̽�����

	public HashNode() {
		super();
		isEmpty = true;
		key = 0;
		indexs = new ArrayList<Integer>();
		count = 0;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public List<Integer> getIndexs() {
		return indexs;
	}

	public void setIndexs(List<Integer> indexs) {
		this.indexs = indexs;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
