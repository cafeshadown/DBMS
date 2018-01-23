package com.zhouchao.db;

import java.io.Serializable;

/**
 * 字段描述
 *
 */
public class Field implements Serializable {

	private static final long serialVersionUID = -1607589038195638512L;

	protected String fieldName;// 字段名
	protected String type;// 字段类型
	protected int length;// 字段长度
	protected int column;// 字段所在列
	protected boolean isNull = true;// 字段是否可为空，默认可为空
	protected boolean isPrimary = false;// 字段是否为主键，默认不是主键
	protected transient boolean isIndex = false;// 是否是索引字段
	protected transient IndexNode indexRoot = null;// 索引树的根节点

	public Field(String fieldName, String type, int length, int column, boolean isNull, boolean isPrimary,
			boolean isIndex, IndexNode indexRoot) {
		super();
		this.fieldName = fieldName;
		this.type = type;
		this.length = length;
		this.column = column;
		this.isNull = isNull;
		this.isPrimary = isPrimary;
		this.isIndex = isIndex;
		this.indexRoot = indexRoot;
	}

	public Field(Field field) {
		super();
		this.fieldName = field.fieldName;
		this.type = field.type;
		this.length = field.length;
		this.column = field.column;
		this.isNull = field.isNull;
		this.isPrimary = field.isPrimary;
		this.isIndex = field.isIndex;
		this.indexRoot = field.indexRoot;
	}

	public Field() {
		super();
	}

	@Override
	public String toString() {
		return "Field [fieldName=" + fieldName + ", type=" + type + ", length=" + length + ", column=" + column
				+ ", isNull=" + isNull + ", isPrimary=" + isPrimary + ", isIndex=" + isIndex + ", indexRoot="
				+ indexRoot + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + (isNull ? 1231 : 1237);
		result = prime * result + (isPrimary ? 1231 : 1237);
		result = prime * result + length;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Field other = (Field) obj;
		if (column != other.column)
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (isNull != other.isNull)
			return false;
		if (isPrimary != other.isPrimary)
			return false;
		if (length != other.length)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isIndex() {
		return isIndex;
	}

	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}

	public IndexNode getIndexRoot() {
		return indexRoot;
	}

	public void setIndexRoot(IndexNode indexRoot) {
		this.indexRoot = indexRoot;
	}

}
