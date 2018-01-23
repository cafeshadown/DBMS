package com.zhouchao.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Check {
	/**
	 * �ж�������¼�Ƿ������Ӧ������
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

		for (ConditionalExpression condition : leaf.getConditions()) {// �����������ϣ�ֻҪ����������һ����������flagΪfalse
			if (condition.isLeftIsIndex() || condition.isRightIsIndex()) {
				continue;
			}
			
			String leftAttr = null;
			String rightAttr = null;
			String leftCons = null;
			String rightCons = null;
			String oper = condition.getOper();

			if (condition.isLeftConstant()) {// �������ʽ����ǳ������ұ�������
				int column = -1;
				leftCons = condition.getLeft();
				rightAttr = condition.getRightAttribute();
				if ("<".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (Integer.parseInt(leftCons) >= Integer.parseInt(fields[column])) {
						flag = false;
					}
				}
				if (">".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (Integer.parseInt(leftCons) <= Integer.parseInt(fields[column])) {
						flag = false;
					}
				}
				if ("<=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (Integer.parseInt(leftCons) > Integer.parseInt(fields[column])) {
						flag = false;
					}
				}
				if (">=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (Integer.parseInt(leftCons) < Integer.parseInt(fields[column])) {
						flag = false;
					}
				}
				if ("=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(rightAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (!leftCons.equals(fields[column])) {
						flag = false;
					}
				}
			} else if (condition.isRightConstant()) {// �������ʽ��������ԣ��ұ��ǳ���
				int column = -1;
				rightCons = condition.getRight();
				leftAttr = condition.getLeftAttribute();
				if ("<".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (Integer.parseInt(fields[column]) >= Integer.parseInt(rightCons)) {
						flag = false;
					}
				}
				if (">".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (Integer.parseInt(fields[column]) <= Integer.parseInt(rightCons)) {
						flag = false;
					}
				}
				if ("<=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (Integer.parseInt(fields[column]) > Integer.parseInt(rightCons)) {
						flag = false;
					}
				}
				if (">=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
						return null;
					}
					if (Integer.parseInt(fields[column]) < Integer.parseInt(rightCons)) {
						flag = false;
					}
				}
				if ("=".equals(oper)) {
					for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
						if (field.getFieldName().equals(leftAttr)) {
							column = field.getColumn();
						}
					}
					if (column == -1) {// �ñ����޴�����
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
	 * �ж��Ƿ���������������֧�ֵѿ���������
	 * 
	 * @param node
	 * @param leftLine
	 * @param rightLine
	 * @return
	 */
	public static boolean isSatisfiedInner(Node leftChild, Node rightChild, ConditionalExpression condition,
			String leftLine, String rightLine) {
		if (condition == null) {//����������ʽΪ�գ����ʾû��������������Ϊ�ѿ���������
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
	 * �ж�������¼�Ƿ������Ӧ������
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
		
		List<List<ConditionalExpression>> lc = operate.getConditions();//���㼯�ϣ����or�ڲ�and
		boolean flag1 = false;
		for (List<ConditionalExpression> land : lc) {//����and������ϼ��ϣ�ֻ����һ���������������������������ʽ
			boolean flag = true;
			for (ConditionalExpression condition : land) {// �����������ϣ�ֻҪ����������һ����������flagΪfalse
				String leftAttr = null;
				String rightAttr = null;
				String leftCons = null;
				String rightCons = null;
				String oper = condition.getOper();
	
				if (condition.isLeftConstant()) {// �������ʽ����ǳ������ұ�������
					int column = -1;
					leftCons = condition.getLeft();
					rightAttr = condition.getRightAttribute();
					if ("<".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (Integer.parseInt(leftCons) >= Integer.parseInt(fields[column])) {
							flag = false;
						}
					}
					if (">".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (Integer.parseInt(leftCons) <= Integer.parseInt(fields[column])) {
							flag = false;
						}
					}
					if ("<=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (Integer.parseInt(leftCons) > Integer.parseInt(fields[column])) {
							flag = false;
						}
					}
					if (">=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (Integer.parseInt(leftCons) < Integer.parseInt(fields[column])) {
							flag = false;
						}
					}
					if ("=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(rightAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (!leftCons.equals(fields[column])) {
							flag = false;
						}
					}
				} else if (condition.isRightConstant()) {// �������ʽ��������ԣ��ұ��ǳ���
					int column = -1;
					rightCons = condition.getRight();
					leftAttr = condition.getLeftAttribute();
					if ("<".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (Integer.parseInt(fields[column]) >= Integer.parseInt(rightCons)) {
							flag = false;
						}
					}
					if (">".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (Integer.parseInt(fields[column]) <= Integer.parseInt(rightCons)) {
							// System.out.println(Integer.parseInt(fields[column])+
							// "<=" +Integer.parseInt(rightCons));
							flag = false;
						}
					}
					if ("<=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (Integer.parseInt(fields[column]) > Integer.parseInt(rightCons)) {
							flag = false;
						}
					}
					if (">=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
						}
						if (Integer.parseInt(fields[column]) < Integer.parseInt(rightCons)) {
							flag = false;
						}
					}
					if ("=".equals(oper)) {
						for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {// �����ñ�����Լ��ϣ���������������
							if (field.getFieldName().equals(leftAttr)) {
								column = field.getColumn();
							}
						}
						if (column == -1) {// �ñ����޴�����
							throw new RuntimeException("���ԷǷ���");
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
	 * �����ԡ�Ψһ��Լ�����
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
			while ((line = br.readLine()) != null) {//���������ļ��ļ�¼
				lines = line.split(",");
				for (int i = 0; i < lines.length; i++) {//����һ����¼�������ֶ�
					if (!list.get(i).isNull) {//������ֶ�Ҫ��ǿ����ж��Ƿ�Ϊnull
						if (newLines[i].equals("null")) {
							return false;
						}
					}
					if (list.get(i).isPrimary) {//������ֶ��������������ж��Ƿ����ظ�
						if (lines[i].equals(newLines[i])) {//����
							return false;
						}
						if (newLines[i].equals("null")) {//�п�
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
	 * ��������������Ƿ�ƥ��
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
			System.out.println("�����ֶ������Բ�ƥ�䣡");
			return false;
		}

		for (int i = 0; i < values.length; i++) {//�������ͺϷ��Լ��
			type = table.getAttributes().get(i).getType();
			if ("Integer".equalsIgnoreCase(type)) {
				if (!values[i].trim().matches("^[0-9]+[0-9]$")) {
					System.out.println("����" + values[i].trim() + "���Ϸ�");
					return false;
				}
			} else if ("Varchar".equalsIgnoreCase(type)){
				if (!values[i].trim().matches("^\"[\\S]+\"$") || values[i].length() > table.getAttributes().get(i).getLength()) {//����Ƿ���varchar�����Ƿ񳬹�����ֽ���
					System.out.println("����" + values[i].trim() + "���Ϸ�");
					return false;
				}
			} else {
				System.out.println("��֧���������ͣ�" + type);
				return false;
			}
		}
		/*
		 * Լ��
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
				System.out.println("Υ��Լ��������");
				return false;
			}
		} else {
			return true;
		}
	}
	/**
	 * ����������Լ������Ƿ����ظ�����
	 * @param attributes
	 * @param fields
	 * @return
	 * 		�����ظ����ݣ�����true�����򷵻�false
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
	 * ����Ƿ��Ѿ�ѡ�����ݿ�
	 */
	public static void hadUseDatabase() {
		if (DBMS.dataDictionary == null) {
			throw new RuntimeException("����ѡ�����ݿ⣡");
		}
	}
}

