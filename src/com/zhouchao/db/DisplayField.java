package com.zhouchao.db;

public class DisplayField extends Field {

	private static final long serialVersionUID = 2760633084059905299L;

	private String name;// 全名
	private String table;// 所属关系表名

	public DisplayField() {
		super();
	}

	public DisplayField(Field field, String name, String table) {
		super(field);
		this.name = name;
		this.table = table;
	}

	public DisplayField(DisplayField df) {
		super(df.fieldName, df.type, df.length, df.column, df.isNull, df.isPrimary, df.isIndex, df.indexRoot);
		this.name = df.name;
		this.table = df.table;
	}

	public DisplayField(String fieldName, String type, int length, int column, boolean isNull, boolean isPrimary,
			boolean isIndex, IndexNode indexRoot, String name, String table) {
		super(fieldName, type, length, column, isNull, isPrimary, isIndex, indexRoot);
		this.name = name;
		this.table = table;
	}

	@Override
	public String toString() {
		return "DisplayField [name=" + name + ", table=" + table + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DisplayField other = (DisplayField) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	@Override
	public int getLength() {
		return super.getLength();
	}

	@Override
	public void setLength(int length) {
		super.setLength(length);
	}

	@Override
	public boolean isNull() {
		return super.isNull();
	}

	@Override
	public void setNull(boolean isNull) {
		super.setNull(isNull);
	}

	@Override
	public boolean isPrimary() {
		return super.isPrimary();
	}

	@Override
	public void setPrimary(boolean isPrimary) {
		super.setPrimary(isPrimary);
	}

	@Override
	public boolean isIndex() {
		return super.isIndex();
	}

	@Override
	public void setIndex(boolean isIndex) {
		super.setIndex(isIndex);
	}

	@Override
	public String getType() {
		return super.getType();
	}

	@Override
	public void setType(String type) {
		super.setType(type);
	}

	@Override
	public String getFieldName() {
		return super.getFieldName();
	}

	@Override
	public void setFieldName(String fieldName) {
		super.setFieldName(fieldName);
	}

	@Override
	public int getColumn() {
		return super.getColumn();
	}

	@Override
	public void setColumn(int column) {
		super.setColumn(column);
	}

	@Override
	public IndexNode getIndexRoot() {
		return super.getIndexRoot();
	}

	@Override
	public void setIndexRoot(IndexNode indexRoot) {
		super.setIndexRoot(indexRoot);
	}

}
