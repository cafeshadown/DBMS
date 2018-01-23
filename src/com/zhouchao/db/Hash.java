package com.zhouchao.db;

/**
 * 哈希表
 *
 *
 */
public class Hash {
	private int refinedModel;// 最合理的模
	private HashNode[] hashTable;// 哈希表
	private int length;//哈希表长
	/**
	 * 初始化哈希表
	 * 
	 * @param m
	 * @return
	 */
	public Hash(int m) {
		this.length = m * 4 / 3;// 设定元素个数为表长的75%
		this.refinedModel = Prime.getRefinedModel(m);// 获得最合理的模并赋值
		this.hashTable = new HashNode[this.length];// 初始化哈希表
		for (int i = 0; i < this.length; i++) {
			this.hashTable[i] = new HashNode();
		}
	}

	/**
	 * 向哈希表中插入关键字
	 * 直接借用了B+树索引叶节点的数据项类作为参数（为了效率可以另写参数）
	 * @param de
	 */
	public void insert(DataEntry de) {
		int pos = (int) (de.getKey() % this.refinedModel);

		int count = 0;// 探查次数
		do {
			if (this.hashTable[pos].isEmpty()) {// 如果此位置无元素，则插入
				this.hashTable[pos].setEmpty(false);
				this.hashTable[pos].setKey(de.getKey());
				this.hashTable[pos].getIndexs().addAll(de.getIndex());
				this.hashTable[pos].setCount(count);
				break;
			}
			if (this.hashTable[pos].getKey() == de.getKey()) {// 如果此位置不为空并且关键字相同，则将此关键字的行添加到集合中
				this.hashTable[pos].getIndexs().addAll(de.getIndex());
				break;
			}
			// 如果关键字不同，则产生碰撞，利用线性探查法解决碰撞
			pos = (pos + 1) % this.refinedModel;
			count += 1;
		} while (true);
	}
	/**
	 * 查找指定关键字的位置
	 * @param de
	 * @return
	 */
	public int search(long key) {
		int pos = (int) (key % this.refinedModel);
		int count = 0;
		while (this.hashTable[pos].getKey() != key || this.hashTable[pos].isEmpty()) {//如果找到相同关键字并且该元素不为空，则停止循环
			pos = (pos + 1) % this.refinedModel;
			count += 1;
			if (count >= this.length) {//如果找了一圈没找到，则跳出循环
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
