package com.zhouchao.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Check {
	/**
	 * 判断所给记录是否满足对应的条件
	 * 
	 * @param line
	 * @param leaf
	 * @return
	 * 
	 */
	public static String isSatisfiedLeaf(String line, LeafNode leaf) {
		if (!leaf.isExistCondition()) {
			return line;
		}

		boolean flag = true;
		String[] fields = line.split(",");
		String tableName = leaf.getTableName();

		for (ConditionalExpression condition : leaf.getConditions()) {// 遍历条件集合，只要不满足其中一个条件就置flag为false
			if (condition.isLeftIsIndex() || condition.isRightIsIndex()) {
				continue;
			}
			
			String leftAttr = null;
			String rightAttr = null;
			String leftCons = null;
			String rightCons = null;
			String oper = condition.getOper();

			if (condition.isLeftConstant()) {// 条件表达式左边是常量，右边是属性
				int column = -1;
				leftCons = condition.getLeft();
				rightAttr = condition.getRightAttribute();
				if ("<".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (Integer.parseInt(leftCons) >= Integer.parseInt(fields[column])) {
						flag = false;
					}
				}
				if (">".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (Integer.parseInt(leftCons) <= Integer.parseInt(fields[column])) {
						flag = false;
					}
				}
				if ("<=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (Integer.parseInt(leftCons) > Integer.parseInt(fields[column])) {
						flag = false;
					}
				}
				if (">=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (Integer.parseInt(leftCons) < Integer.parseInt(fields[column])) {
						flag = false;
					}
				}
				if ("=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (!leftCons.equals(fields[column])) {
						flag = false;
					}
				}
			} else if (condition.isRightConstant()) {// 条件表达式左边是属性，右边是常量
				int column = -1;
				rightCons = condition.getRight();
				leftAttr = condition.getLeftAttribute();
				if ("<".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (Integer.parseInt(fields[column]) >= Integer.parseInt(rightCons)) {
						flag = false;
					}
				}
				if (">".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (Integer.parseInt(fields[column]) <= Integer.parseInt(rightCons)) {
						flag = false;
					}
				}
				if ("<=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (Integer.parseInt(fields[column]) > Integer.parseInt(rightCons)) {
						flag = false;
					}
				}
				if (">=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (Integer.parseInt(fields[column]) < Integer.parseInt(rightCons)) {
						flag = false;
					}
				}
				if ("=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// 该表中无此属性
						return null;
					}
					if (!fields[column].equals(rightCons)) {
						flag = false;
					}
				}
			}

		}
		if (flag == false) {
			return null;
		}
		return line;
	}

	/**
	 * 判断是否满足连接条件，支持笛卡尔基操作
	 * 
	 * @param node
	 * @param leftLine
	 * @param rightLine
	 * @return
	 */
	public static boolean isSatisfiedInner(Node leftChild, Node rightChild, ConditionalExpression condition,
			String leftLine, String rightLine) {
		if (condition == null) {//如果条件表达式为空，则表示没有连接条件，则为笛卡尔积操作
			return true;
		}

		String[] leftFields = leftLine.split(",");
		String[] rightFields = rightLine.split(",");

		int leftColumn = condition.getLeftcolumn();
		int rightColumn = condition.getRightcolumn();

		if (leftFields[leftColumn].equals(rightFields[rightColumn])) {
			return true;
		}
		return false;
	}

	/**
	 * 判断所给记录是否满足对应的条件
	 * 
	 * @param line
	 * @param leaf
	 * @return
	 * 
	 */
	public static boolean isSatisfiedOper(String line, Oper operate) {
		if (!operate.isExistWhereCondition()) {
			return true;
		}

		String[] fields = line.split(",");
		String tableName = operate.getTableName();
		
		List<List<ConditionalExpression>> lc = operate.getConditions();//两层集合，外层or内层and
		boolean flag1 = false;
		for (List<ConditionalExpression> land : lc) {//遍历and条件组合集合，只有有一组满足条件即满足整个条件表达式
			boolean flag = true;
			for (ConditionalExpression condition : land) {// 遍历条件集合，只要不满足其中一个条件就置flag为false
				String leftAttr = null;
				String rightAttr = null;
				String leftCons = null;
				String rightCons = null;
				String oper = condition.getOper();
	
				if (condition.isLeftConstant()) {// 条件表达式左边是常量，右边是属性
					int column = -1;
					leftCons = condition.getLeft();
					rightAttr = condition.getRightAttribute();
					if ("<".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (Integer.parseInt(leftCons) >= Integer.parseInt(fields[column])) {
							flag = false;
						}
					}
					if (">".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (Integer.parseInt(leftCons) <= Integer.parseInt(fields[column])) {
							flag = false;
						}
					}
					if ("<=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (Integer.parseInt(leftCons) > Integer.parseInt(fields[column])) {
							flag = false;
						}
					}
					if (">=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (Integer.parseInt(leftCons) < Integer.parseInt(fields[column])) {
							flag = false;
						}
					}
					if ("=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (!leftCons.equals(fields[column])) {
							flag = false;
						}
					}
				} else if (condition.isRightConstant()) {// 条件表达式左边是属性，右边是常量
					int column = -1;
					rightCons = condition.getRight();
					leftAttr = condition.getLeftAttribute();
					if ("<".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (Integer.parseInt(fields[column]) >= Integer.parseInt(rightCons)) {
							flag = false;
						}
					}
					if (">".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (Integer.parseInt(fields[column]) <= Integer.parseInt(rightCons)) {
							// System.out.println(Integer.parseInt(fields[column])+
							// "<=" +Integer.parseInt(rightCons));
							flag = false;
						}
					}
					if ("<=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (Integer.parseInt(fields[column]) > Integer.parseInt(rightCons)) {
							flag = false;
						}
					}
					if (">=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (Integer.parseInt(fields[column]) < Integer.parseInt(rightCons)) {
							flag = false;
						}
					}
					if ("=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// 遍历该表的属性集合，查找属性所在列
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// 该表中无此属性
							throw new RuntimeException("属性非法！");
						}
						if (!fields[column].equals(rightCons)) {
							flag = false;
						}
					}
				}
	
			}
			if ((flag1 = flag) == true) {
				break;
			}
		}
		return flag1;
	}

	/**
	 * 完整性、唯一性约束检测
	 * @param newLine
	 * @param list
	 * @param file
	 * @return
	 */
	private static boolean isSatisfiedConstraint(String newLine, List<Field> list, File file) {
		String[] newLines = newLine.split(",");
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
			
			String line = null;
			String[] lines = null;
			while ((line = br.readLine()) != null) {//遍历整个文件的记录
				lines = line.split(",");
				for (int i = 0; i < lines.length; i++) {//遍历一条记录的所有字段
					if (!list.get(i).isNull) {//如果该字段要求非空则判断是否为null
						if (newLines[i].equals("null")) {
							return false;
						}
					}
					if (list.get(i).isPrimary) {//如果该字段设置了主键则判断是否有重复
						if (lines[i].equals(newLines[i])) {//判重
							return false;
						}
						if (newLines[i].equals("null")) {//判空
							return false;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 检查数据与属性是否匹配
	 * @param temp 
	 * @param table
	 * @param value
	 * @param isUpdatePrimary 
	 * @return
	 */
	public static boolean checkValues(File temp, Table table, String value, boolean isUpdatePrimary) {
		String[] values = value.split(",");
		String type = null;

		if (values.length != table.getAttributes().size()) {
			System.out.println("插入字段与属性不匹配！");
			return false;
		}

		for (int i = 0; i < values.length; i++) {//数据类型合法性检查
			type = table.getAttributes().get(i).getType();
			if ("Integer".equalsIgnoreCase(type)) {
				if (!values[i].trim().matches("^[0-9]+[0-9]$")) {
					System.out.println("数据" + values[i].trim() + "不合法");
					return false;
				}
			} else if ("Varchar".equalsIgnoreCase(type)){
				if (!values[i].trim().matches("^\"[\\S]+\"$") || values[i].length() > table.getAttributes().get(i).getLength()) {//检查是否是varchar并且是否超过最大字节数
					System.out.println("数据" + values[i].trim() + "不合法");
					return false;
				}
			} else {
				System.out.println("不支持数据类型：" + type);
				return false;
			}
		}
		/*
		 * 约束
		 */
		boolean flag = false;
		for (Field field : table.getAttributes()) {
			if (!field.isNull || field.isPrimary) {
				flag = true;
				break;
			}
		}
		if (flag && isUpdatePrimary) {
			if (Check.isSatisfiedConstraint(value, table.getAttributes(), temp)) {
				return true;
			} else {
				System.out.println("违反约束条件！");
				return false;
			}
		} else {
			return true;
		}
	}
	/**
	 * 检查两个属性集合中是否有重复数据
	 * @param attributes
	 * @param fields
	 * @return
	 * 		若有重复数据，返回true，否则返回false
	 */
	public static boolean isRepeated(List<Field> attributes, List<Field> fields) {
		for (Field field1 : fields) {
			for (Field filed2 : attributes) {
				if (field1.getFieldName().equalsIgnoreCase(filed2.getFieldName())) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 检查是否已经选择数据库
	 */
	public static void hadUseDatabase() {
		if (DBMS.dataDictionary == null) {
			throw new RuntimeException("请先选择数据库！");
		}
	}
}

