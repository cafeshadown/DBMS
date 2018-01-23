package com.zhouchao.db;

/**
 * ��ϣ��
 *
 *
 */
public class Hash {
	private int refinedModel;// ������ģ
	private HashNode[] hashTable;// ��ϣ��
	private int length;//��ϣ��
	/**
	 * ��ʼ����ϣ��
	 * 
	 * @param m
	 * @return
	 */
	public Hash(int m) {
		this.length = m * 4 / 3;// �趨Ԫ�ظ���Ϊ����75%
		this.refinedModel = Prime.getRefinedModel(m);// ���������ģ����ֵ
		this.hashTable = new HashNode[this.length];// ��ʼ����ϣ��
		for (int i = 0; i < this.length; i++) {
			this.hashTable[i] = new HashNode();
		}
	}

	/**
	 * ���ϣ���в���ؼ���
	 * ֱ�ӽ�����B+������Ҷ�ڵ������������Ϊ������Ϊ��Ч�ʿ�����д������
	 * @param de
	 */
	public void insert(DataEntry de) {
		int pos = (int) (de.getKey() % this.refinedModel);

		int count = 0;// ̽�����
		do {
			if (this.hashTable[pos].isEmpty()) {// �����λ����Ԫ�أ������
				this.hashTable[pos].setEmpty(false);
				this.hashTable[pos].setKey(de.getKey());
				this.hashTable[pos].getIndexs().addAll(de.getIndex());
				this.hashTable[pos].setCount(count);
				break;
			}
			if (this.hashTable[pos].getKey() == de.getKey()) {// �����λ�ò�Ϊ�ղ��ҹؼ�����ͬ���򽫴˹ؼ��ֵ�����ӵ�������
				this.hashTable[pos].getIndexs().addAll(de.getIndex());
				break;
			}
			// ����ؼ��ֲ�ͬ���������ײ����������̽�鷨�����ײ
			pos = (pos + 1) % this.refinedModel;
			count += 1;
		} while (true);
	}
	/**
	 * ����ָ���ؼ��ֵ�λ��
	 * @param de
	 * @return
	 */
	public int search(long key) {
		int pos = (int) (key % this.refinedModel);
		int count = 0;
		while (this.hashTable[pos].getKey() != key || this.hashTable[pos].isEmpty()) {//����ҵ���ͬ�ؼ��ֲ��Ҹ�Ԫ�ز�Ϊ�գ���ֹͣѭ��
			pos = (pos + 1) % this.refinedModel;
			count += 1;
			if (count >= this.length) {//�������һȦû�ҵ���������ѭ��
				break;
			}
		}
		if (this.hashTable[pos].getKey() == key && !this.hashTable[pos].isEmpty() && count == this.hashTable[pos].getCount()) {
			return pos;
		}
		return -1;
	}

	public int getRefinedModel() {
		return refinedModel;
	}

	public void setRefinedModel(int refinedModel) {
		this.refinedModel = refinedModel;
	}

	public HashNode[] getHashTable() {
		return hashTable;
	}

	public void setHashTable(HashNode[] hashTable) {
		this.hashTable = hashTable;
	}

}
