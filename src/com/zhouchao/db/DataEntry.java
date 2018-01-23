package com.zhouchao.db;

import java.util.Set;

/**
 * 叶节点中的<数值-所在行>原子
 *
 */
public class DataEntry implements Comparable<DataEntry> {
	private long key;
	private Set<Integer> index;

	public DataEntry(long key, Set<Integer> index) {
		super();
		this.key = key;
		this.index = index;
	}

	public DataEntry() {
		super();
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (key ^ (key >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataEntry other = (DataEntry) obj;
		if (key != other.key)
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "DataEntry [key=" + key + ", index=" + index + "]";
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public Set<Integer> getIndex() {
		return index;
	}

	public void setIndex(Set<Integer> index) {
		this.index = index;
	}

	@Override
	public int compareTo(DataEntry o) {
		return this.getKey() - o.getKey() > 0 ? 1 : -1;
	}

}
