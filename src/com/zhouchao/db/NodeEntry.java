package com.zhouchao.db;

/**
 * 内节点中的<数值-结点>原子
 *
 */
public class NodeEntry implements Comparable<NodeEntry> {
	private long key;
	private IndexNode node;

	public NodeEntry(long leftkey, IndexNode node) {
		super();
		this.key = leftkey;
		this.node = node;
	}

	public NodeEntry() {
		super();
	}

	@Override
	public int compareTo(NodeEntry o) {
		return this.getKey() - o.getKey() > 0 ? 1 : -1;
	}

	@Override
	public String toString() {
		return "NodeEntry [key=" + key + ", node=" + node + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (key ^ (key >>> 32));
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
		{return true;}
		if (obj == null)
		{return false;}
		if (getClass() != obj.getClass())
		{	return false;}
		NodeEntry other = (NodeEntry) obj;
		if (key != other.key)
		{return false;}
		if (node == null) {
			if (other.node != null)
			{return false;}
		} else if (!node.equals(other.node))
		{return false;}
		return true;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public IndexNode getNode() {
		return node;
	}

	public void setNode(IndexNode node) {
		this.node = node;
	}

}
