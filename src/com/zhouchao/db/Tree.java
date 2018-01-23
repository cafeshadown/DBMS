package com.zhouchao.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
/**
 * ��ѯ����صľ�̬����
 *
 */
public class Tree {
	public static Map<String, LeafNode> leaves;//��ѯ��Ҷ�ڵ�
	public static List<InternalNode> inners;//��ѯ���ڽڵ�
	/**
	 * ����
	 * @return
	 */
	public static List<Node> buildTree(Select select) {
		List<Node> roots = new ArrayList<Node>();

		if (select.isExistWhereCondition()) {
			for (int i = 0; i < select.getConditions().size(); i++) {
				buildLeafNode(select, i);//����Ҷ�ӽڵ�
				setColumn();
				buildInternalNode(select, i);//�����ڲ��ڵ�
				roots.add(buildDKNode());//
			}
		}
		if (!select.isExistWhereCondition()) {
			buildLeafNode(select, 1);//����Ҷ�ӽڵ�
			setColumn();
			buildInternalNode(select, 1);//�����ڲ��ڵ�
			roots.add(buildDKNode());//
		}
		return roots;
	}

	public static Set<String> getDisplayAccounts(List<Node> roots) {
		Set<String> set = new HashSet<String>();
		BufferedReader br = null;
		
		for (Node node : roots) {
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(node.getFile())));
				String line = null;
				while ((line = br.readLine()) != null) {
					set.add(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return set;
	}
	/**
	 * ����Ҷ�ڵ㱣���ֶ�������
	 */
	public static void setColumn() {
		List<Field> fields = null;
		Set<DisplayField> saveFields = null;
		for (Entry<String, LeafNode> e : Tree.leaves.entrySet()) {
			int col = 0;
			fields = DBMS.loadedTables.get(e.getKey()).getAttributes();//�ñ���ֶμ���
			saveFields = e.getValue().getSaveFields();//�ñ�ı����ֶμ���
			for (Field field : fields) {
				for (DisplayField df1 : saveFields) {
					if (df1.getFieldName().equalsIgnoreCase(field.getFieldName())) {
						df1.setColumn(col);
						col += 1;
					}
				}
			}
		}
	}
	/**
	 * ��Ҷ�ڵ�
	 */
	public static void buildLeafNode(Select select, int n) {

		Tree.leaves = new HashMap<String, LeafNode>();
		LeafNode leaf = null;

		for (String tempTable : select.getTableName()) {// �����еı���һ��Ҷ���
			File file = new File(DBMS.currentPath + File.separator + tempTable + "_" + n + "$");
			Set<DisplayField> saveFields = new HashSet<DisplayField>();
			DisplayField df = null;
			int num = 0;
			if (!select.isSeeAll()) {
				for (DisplayField f : select.getAttributes()) {// ����selectҪ��ѯ���ֶ�
					if (f.getTable().equals(tempTable)) {
						df = new DisplayField(f);
						if (saveFields.add(df)) {
							num += 1;
						}
					}
				}
			} else if (select.isSeeAll()) {
				for (Field field : DBMS.loadedTables.get(tempTable).getAttributes()) {
					df = new DisplayField(field, tempTable + "." + field.getFieldName(), tempTable);
					if (saveFields.add(df)) {
//						df.setColumn(num);
						num += 1;

					}
				} 
			}
			List<ConditionalExpression> conditions = new ArrayList<ConditionalExpression>();
			leaf = new LeafNode(true, file, false, null, num, saveFields, tempTable, false, conditions);// �µ�Ҷ���
			Tree.leaves.put(tempTable, leaf);
		}

		if (!select.isExistWhereCondition()) {
			return;
		}

		/*
		 * Ҷ�ڵ���ӱ����ֶκ��������ʽ
		 */
		String name = null;
		String table = null;
		String attr = null;
		DisplayField df = null;
		List<ConditionalExpression> conditions = select.getConditions().get(n);//��n���ȡʽ
		for (ConditionalExpression condition : conditions) {// �����������ʽ
			if (condition.isLeftConstant() ^ condition.isRightConstant()) {// ��ѡ���������ʽ

				name = condition.isLeftConstant() ? condition.getRight() : condition.getLeft();// ѡ���������ʽ������ȫ��
				table = condition.isLeftConstant() ? condition.getRightTableName() : condition.getLeftTableName();// ѡ���������ʽ���������漰�ı���
				attr = condition.isLeftConstant() ? condition.getRightAttribute() : condition.getLeftAttribute();// ѡ���������ʽ��������

				if ((leaf = Tree.leaves.get(table)) != null) {// ����Ѿ����ڴ�Ҷ�ڵ㣬���ڴ˽�������
					leaf.setExistCondition(true);
					leaf.getConditions().add(condition);// �������
					List<Field> list = null;
					try {
						list = DBMS.loadedTables.get(table).getAttributes();// �˱���ֶμ���
					} catch (Exception e) {
						throw new RuntimeException("û��" + table + "�����");
					}
					boolean flag = false;
					for (Field field : list) {// �����ֶμ���
						if (field.getFieldName().equals(attr)) {// �ҵ�ͬ���ֶ�
							df = new DisplayField(field, name, table);// ����ͬ���ֶι����µı����ֶ�
							if (leaf.getSaveFields().add(df)) {// ��������ֶμ������޴��ֶ������ֶ�����1
								int i = leaf.getNumberField();
								leaf.setNumberField(i + 1);
							}
							flag = true;
						}
					}
					if (flag == false) {
						throw new RuntimeException("û��" + attr + "������ԣ�");
					}
				}
			} else {// �������������ʽ

				String leftTable = condition.getLeftTableName();

				if ((leaf = Tree.leaves.get(leftTable)) != null) {// ����Ѿ����ڴ�Ҷ�ڵ㣬���ڴ˽�������

					List<Field> list = null;
					try {
						list = DBMS.loadedTables.get(leftTable).getAttributes();// �˱���ֶμ���
					} catch (Exception e) {
						throw new RuntimeException("û��" + table + "�����");
					}
					boolean flag = false;
					for (Field field : list) {// �����ֶμ���
						if (field.getFieldName().equals(condition.getLeftAttribute())) {// �ҵ�ͬ���ֶ�
							df = new DisplayField(field, condition.getLeft(), leftTable);// ����ͬ���ֶι����µı����ֶ�
							if (leaf.getSaveFields().add(df)) {// ��������ֶμ������޴��ֶ������ֶ�����1
								int i = leaf.getNumberField();
								leaf.setNumberField(i + 1);
							}
							flag = true;
						}
					}
					if (flag == false) {
						throw new RuntimeException("û��" + condition.getLeft() + "������ԣ�");
					}
				}

				String rightTable = condition.getRightTableName();

				if ((leaf = Tree.leaves.get(rightTable)) != null) {// ����Ѿ����ڴ�Ҷ�ڵ㣬���ڴ˽�������

					List<Field> list = null;
					try {
						list = DBMS.loadedTables.get(rightTable).getAttributes();// �˱���ֶμ���
					} catch (Exception e) {
						throw new RuntimeException("û��" + rightTable + "�����");
					}
					boolean flag = false;
					for (Field field : list) {// �����ֶμ���
						if (field.getFieldName().equals(condition.getRightAttribute())) {// �ҵ�ͬ���ֶ�
							df = new DisplayField(field, condition.getRight(), rightTable);// ����ͬ���ֶι����µı����ֶ�
							if (leaf.getSaveFields().add(df)) {// ��������ֶμ������޴��ֶ������ֶ�����1
								int i = leaf.getNumberField();
								leaf.setNumberField(i + 1);
							}
							flag = true;
						}
					}
					if (flag == false) {
						throw new RuntimeException("û��" + condition.getRight() + "������ԣ�");
					}
				}
			}
		}
	}

	/**
	 * �����ڲ��ڵ�
	 */
	public static void buildInternalNode(Select select, int n) {
		String leftTable = null;
		String rightTable = null;
		Node left = null;// ����
		Node right = null;// �ҽ��
		ConditionalExpression joinCondition = null;// ��������

		Tree.inners = new ArrayList<InternalNode>();// �ڲ���㼯��
		if (!select.isExistWhereCondition()) {
			return;
		}
		List<ConditionalExpression> conditions = select.getConditions().get(n);//��n���ȡʽ
		for (ConditionalExpression condition : conditions) {
			if (!condition.isLeftConstant() && !condition.isRightConstant()) {// ����������
				joinCondition = new ConditionalExpression(condition);

				leftTable = condition.getLeftTableName();
				left = Tree.leaves.get(leftTable);

				rightTable = condition.getRightTableName();
				right = Tree.leaves.get(rightTable);

				while (left.hasFather) {
					left = left.getFather();
				}
				while (right.hasFather) {
					right = right.getFather();
				}

				buildInternal(left, right, joinCondition);
			}

		}
	}
	/**
	 * ���ظ��ڵ㣬���������Ҫ���ѿ����������򹹽���������
	 */
	private static Node buildDKNode() {
		List<Node> list = new ArrayList<Node>();
		for (Node node : Tree.leaves.values()) {//Ѱ��û�и��׵�Ҳ���
			if (!node.isHasFather()) {
				list.add(node);
			}
		}
		if (Tree.inners.size() != 0) {
			for (Node node : Tree.inners) {//Ѱ��û�и��׵��ڲ��ڵ�
				if (!node.isHasFather()) {
					list.add(node);
				}
			}
		}
		while (list.size() > 1) {
			java.util.Collections.sort(list, new Comparator<Node>() {//�Զ���ȽϷ�ʽ���������,��ÿ�����ļ�¼������
				@Override
				public int compare(Node n1, Node n2) {
					return n1.getAccountNumbers() - n2.getAccountNumbers();
				}
			});
			
			Node left = list.remove(0);
			Node right = list.remove(0);
			Node inner = buildInternal(left, right, null);//��¼�����ٵ������ڵ����ѿ�����
			list.add(inner);
		}
		return list.get(0);
	}
	/**
	 * �����ڲ��ڵ�
	 * @param left
	 * @param right
	 * @param joinCondition
	 * @return
	 */
	private static Node buildInternal(Node left, Node right, ConditionalExpression joinCondition) {
		InternalNode inner = null;
		int numberField = 0;
		int numberTable = 0;
		List<String> tableName = new ArrayList<String>();
		Set<DisplayField> saveFields = new HashSet<DisplayField>();//�����ֶμ���

		if (left.isLeafNode) {
			LeafNode leftNode = (LeafNode) left;
			numberTable += 1;
			tableName.add(leftNode.getTableName());
		} else {
			InternalNode leftNode = (InternalNode) left;
			numberTable += leftNode.getNumberTable();
			tableName.addAll(leftNode.getTableName());
		}

		if (right.isLeafNode) {
			LeafNode rightNode = (LeafNode) right;
			numberTable += 1;
			tableName.add(rightNode.getTableName());
		} else {
			InternalNode rightNode = (InternalNode) right;
			numberTable += rightNode.getNumberTable();
			tableName.addAll(rightNode.getTableName());
		}

		for (DisplayField df : left.getSaveFields()) {
			if (joinCondition != null) {
				if (df.getFieldName().equals(joinCondition.getLeftAttribute())) {
					joinCondition.setLeftcolumn(df.getColumn());
				}
			}
			saveFields.add(new DisplayField(df));
		}

		DisplayField tdf = null;
		int step = left.getNumberField();
		for (DisplayField df : right.getSaveFields()) {
			tdf = new DisplayField(df);
			/*
			 * ���������Ҷ��㣬����stepΪ�������������漰�ı��е��ֶ���
			 * ������������ڲ����,��������ӽ���µ����й�ϵ��,���������ֶθ�������ֵ��step
			 */
			tdf.setColumn(tdf.getColumn() + step);// ���Ұ벿�ֵ����б����ֶ������м�step
			saveFields.add(tdf);
			if (joinCondition != null) {
				if (df.getFieldName().equals(joinCondition.getRightAttribute())) {
					joinCondition.setRightcolumn(df.getColumn());
				}
			}
		}
		numberField = left.getNumberField() + right.getNumberField();
		File file = new File(
				DBMS.currentPath + File.separator + left.getFile().getName() + "&" + right.getFile().getName());
		inner = new InternalNode(false, file, false, null, numberField, saveFields, numberTable, tableName, true,
				joinCondition, left, right);
		if (joinCondition == null) {
			inner.setIsJoinOperation(false);
		}
		left.setFather(inner);
		left.setHasFather(true);
		right.setFather(inner);
		right.setHasFather(true);
		Tree.inners.add(inner);
		return inner;
	}

	/**
	 * ������ѯ��
	 * 
	 * @param node
	 * @throws Exception
	 */
	public static void traverseTree(Node node) throws Exception {
		if (!node.isLeafNode) {
			traverseTree(((InternalNode) node).getLeftNode());
			traverseTree(((InternalNode) node).getRightNode());
			creatInnerFile((InternalNode) node);
		} else {
			creatLeafFile((LeafNode) node);
		}
	}

	/**
	 * ����ϵ�������������ļ�¼д��Ҷ�ӽڵ���ʱ�ļ�
	 * 
	 * @throws Exception
	 */
	public static void creatLeafFile(LeafNode leaf) throws Exception {
		File sor = DBMS.loadedTables.get(leaf.getTableName()).getFile();
		if (!sor.exists()) {
			throw new RuntimeException(sor + "�����ڣ�");
		}
		List<String> accounts = IOUtil.readFile(sor);//���ļ��е����ݶ����ڴ�
//		System.out.println(accounts.size());
		Set<Integer> retainIndex = new HashSet<Integer>();//���ڴ洢���������ļ�¼������,��ʼ�������ж���������
		
		List<ConditionalExpression> conditions = leaf.getConditions();
		
		boolean existIndex = false;
		int i;
		for (i = 0; i < conditions.size(); i++) {//�ҳ����ʽ�����е�һ�����������Ļ�ȡ�������ϸ�retainIndex��ֵ
			Set<Integer> indexs = OperUtil.getIndexOfBT(conditions.get(i));
			if (indexs != null) {
				existIndex = true;
				retainIndex = indexs;
				break;
			}
		}
		while (++i < conditions.size()) {//��������������ȡ��������ȡ�������õ�����������������������
			Set<Integer> indexs = OperUtil.getIndexOfBT(conditions.get(i));
			if (indexs != null) {
				existIndex = true;
				retainIndex.retainAll(indexs);
			}
			if (retainIndex.size() == 0) {
				break;
			}
		}
		
		if (!existIndex) {//���û��������ȫ���ж����ϳ����ж�
			for (int j = 0; j < accounts.size(); j++) {
				retainIndex.add(j);
			}
		}

		//��ȡҪ�������ֶ�������
		List<Integer> saveCol = new ArrayList<Integer>();
		for (Field field : DBMS.loadedTables.get(leaf.getTableName()).getAttributes()) {
			for (DisplayField df : leaf.getSaveFields()) {
				if (df.getFieldName().equalsIgnoreCase(field.getFieldName())) {
					saveCol.add(field.getColumn());
				}
			}
		}
		
		PrintWriter pw = null;
		try {
			File temp = leaf.getFile();
			if (!temp.exists()) {
				temp.createNewFile();
			}
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), "GBK"));
			int accountNumbers = 0;
			if (retainIndex.size() != 0) {
				String subLine = null;
				for (Integer n : retainIndex) {
					if ((subLine = Check.isSatisfiedLeaf(accounts.get(n), leaf)) != null) {
						String[] arr = subLine.split(",");
						subLine = "";
						for (int col : saveCol) {
							subLine += arr[col] + ",";
						}
						pw.println(subLine.substring(0, subLine.lastIndexOf(",")));
						accountNumbers += 1;
					}
				}
			}
			leaf.setAccountNumbers(accountNumbers);
//			System.out.println(accountNumbers);
		} catch (Exception e) {
			throw e;
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * ��Ҷ�ӽڵ���ʱ�ļ������������ļ�¼д���ڲ��ڵ���м��ļ�
	 * ���ù�ϣ����
	 */
	public static void creatInnerFile(InternalNode node) {
		Node leftChild = node.getLeftNode();
		Node rightChild = node.getRightNode();

		ConditionalExpression condition = node.getJoinCondition();//��������

		File leftFile = leftChild.getFile();
		File rightFile = rightChild.getFile();
		File tempFile = node.getFile();

		BufferedReader leftReader = null;
		BufferedReader rightReader = null;
		PrintWriter pw = null;
		
		List<String> lines = new ArrayList<String>();//�����¼
		Hash hashTable = null;//��ϣ��
		try {
			leftReader = new BufferedReader(new InputStreamReader(new FileInputStream(leftFile), "GBK"));
			rightReader = new BufferedReader(new InputStreamReader(new FileInputStream(rightFile), "GBK"));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "GBK"));
			
			if (node.isIsJoinOperation()) {//�������Ȼ����
				if (leftChild.getAccountNumbers() <= rightChild.getAccountNumbers()) {//��ߺ��ӽ��ļ�¼������
					String type = condition.getLeftType();//���ӱ��ʽ����������
					int column = condition.getLeftcolumn();//���ӱ��ʽ������������
					String line = null;
					int index = 0;//����
					hashTable = new Hash(leftChild.getAccountNumbers());//�����ӽڵ�ļ�¼������Ϊ��ϣ��ĳ���ʵ������ϣ��
					long hashCode = 0;
					Set<Integer> indexs = null;//�м���
					while ((line = leftReader.readLine()) != null) {
						lines.add(line);//����¼�������¼����
						//���ֶ�ֵת��Ϊ��ϣ����Ϊ��ϣ��Ĺؼ���
						hashCode = "Integer".equalsIgnoreCase(type) ? Long.parseLong(line.split(",")[column]) : line.split(",")[column].hashCode();
						indexs = new HashSet<Integer>();
						indexs.add(index);
						hashTable.insert(new DataEntry(hashCode, indexs));//���ϣ���в���Ԫ��
						index += 1;
					}
				} else {//�ұߺ��ӽ��ļ�¼������
					String type = condition.getRightType();//���ӱ��ʽ����������
					int column = condition.getRightcolumn();//���ӱ��ʽ������������
					String line = null;
					int index = 0;//����
					hashTable = new Hash(rightChild.getAccountNumbers());//���Һ��ӽڵ�ļ�¼������Ϊ��ϣ��ĳ���ʵ������ϣ��
					long hashCode = 0;
					Set<Integer> indexs = null;//�м���
					while ((line = rightReader.readLine()) != null) {
						lines.add(line);//����¼�������¼����
						//���ֶ�ֵת��Ϊ��ϣ����Ϊ��ϣ��Ĺؼ���
						hashCode = "Integer".equalsIgnoreCase(type) ? Long.parseLong(line.split(",")[column]) : line.split(",")[column].hashCode();
						indexs = new HashSet<Integer>();
						indexs.add(index);
						hashTable.insert(new DataEntry(hashCode, indexs));//���ϣ���в���Ԫ��
						index += 1;
					}
				}
				int accountNumbers = 0;
				if (leftChild.getAccountNumbers() <= rightChild.getAccountNumbers()) {//��ߺ��ӽ��ļ�¼������
					String type = condition.getRightType();//���ӱ��ʽ����������
					int column = condition.getRightcolumn();//���ӱ��ʽ������������
					long hashCode = 0;
					String line = null;
					int pos;
					while ((line = rightReader.readLine()) != null) {
						hashCode = "Integer".equalsIgnoreCase(type) ? Long.parseLong(line.split(",")[column]) : line.split(",")[column].hashCode();
						pos = hashTable.search(hashCode);//��ѯ��ϣ��
						if (pos != -1) {//�ҵ���Ӧֵ����λ��
							for (int i : hashTable.getHashTable()[pos].getIndexs()) {
								pw.println(lines.get(i) + "," + line);
								accountNumbers += 1;
							}
						}
					}
				} else {//�ұߺ��ӽ��ļ�¼������
					String type = condition.getLeftType();//���ӱ��ʽ����������
					int column = condition.getLeftcolumn();//���ӱ��ʽ������������
					long hashCode = 0;
					String line = null;
					int pos;
					while ((line = leftReader.readLine()) != null) {
						hashCode = "Integer".equalsIgnoreCase(type) ? Long.parseLong(line.split(",")[column]) : line.split(",")[column].hashCode();
						pos = hashTable.search(hashCode);//��ѯ��ϣ��
						if (pos != -1) {//�ҵ���Ӧֵ����λ��
							for (int i : hashTable.getHashTable()[pos].getIndexs()) {
								pw.println(line + "," + lines.get(i));
								accountNumbers += 1;
							}
						}
					}
				}
				node.setAccountNumbers(accountNumbers);//���ڽڵ�ļ�¼��
			}
			if (!node.isIsJoinOperation()) {//�ѿ���������
				int accountNumbers = 0;
				String leftLine = null;
				String rightLine = null;
				while ((leftLine = leftReader.readLine()) != null) {
					while ((rightLine = rightReader.readLine()) != null) {
							pw.println(leftLine + "," + rightLine);
							accountNumbers += 1;
					}
					rightReader.close();
					rightReader = new BufferedReader(new InputStreamReader(new FileInputStream(rightFile), "GBK"));
				}
				node.setAccountNumbers(accountNumbers);//���ڽڵ�ļ�¼��
//				System.out.println(accountNumbers);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (leftReader != null) {
					leftReader.close();
				}
				if (rightReader != null) {
					rightReader.close();
				}
				if (pw != null) {
					pw.close();
				}
				if (!leftFile.delete()) {
					System.out.println(leftFile.getName() + " �ļ����ڱ�ռ�ã��޷�ɾ����");
				}
				if (!rightFile.delete()) {
					System.out.println(rightFile.getName() + " �ļ����ڱ�ռ�ã��޷�ɾ����");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ʾselect��ѯ�����ͶӰ����
	 * 
	 * @param rootNode
	 */
	public static void display(Select select, Set<String> displayAccounts, Set<DisplayField> fields) {
		
		if (select.isSeeAll()) {// �����select*���������ļ��е�����ȫ����ʾ
			for (String line : displayAccounts) {
				System.out.println(line);
			}
			System.out.println("Total lines:" + displayAccounts.size());
		} else {
			String[] attrs = null;
			for (DisplayField df : select.getAttributes()) {
				System.out.printf("%10s%10s\t", df.getType(), df.getFieldName());
			}
			System.out.println();// ����
			for (String line : displayAccounts) {
				attrs = line.split(",");
				for (DisplayField df1 : select.getAttributes()) {// Ҫ��ʾ���ֶμ���
					for (DisplayField df2 : fields) {// rootNode�е��ֶμ���
						if (df1.getName().equals(df2.getName())) {// ��ȡҪ��ʾ���ֶ����ļ��е���
							System.out.printf("%20s\t", attrs[df2.getColumn()]);
						}
					}
				}
				System.out.println();
			}
			System.out.println("Total lines: " + displayAccounts.size());
		}

	}
}
