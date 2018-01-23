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
 * 查询树相关的静态方法
 *
 */
public class Tree {
	public static Map<String, LeafNode> leaves;//查询树叶节点
	public static List<InternalNode> inners;//查询树内节点
	/**
	 * 建树
	 * @return
	 */
	public static List<Node> buildTree(Select select) {
		List<Node> roots = new ArrayList<Node>();

		if (select.isExistWhereCondition()) {
			for (int i = 0; i < select.getConditions().size(); i++) {
				buildLeafNode(select, i);//创建叶子节点
				setColumn();
				buildInternalNode(select, i);//创建内部节点
				roots.add(buildDKNode());//
			}
		}
		if (!select.isExistWhereCondition()) {
			buildLeafNode(select, 1);//创建叶子节点
			setColumn();
			buildInternalNode(select, 1);//创建内部节点
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
	 * 设置叶节点保留字段所在列
	 */
	public static void setColumn() {
		List<Field> fields = null;
		Set<DisplayField> saveFields = null;
		for (Entry<String, LeafNode> e : Tree.leaves.entrySet()) {
			int col = 0;
			fields = DBMS.loadedTables.get(e.getKey()).getAttributes();//该表的字段集合
			saveFields = e.getValue().getSaveFields();//该表的保留字段集合
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
	 * 建叶节点
	 */
	public static void buildLeafNode(Select select, int n) {

		Tree.leaves = new HashMap<String, LeafNode>();
		LeafNode leaf = null;

		for (String tempTable : select.getTableName()) {// 给所有的表建立一个叶结点
			File file = new File(DBMS.currentPath + File.separator + tempTable + "_" + n + "$");
			Set<DisplayField> saveFields = new HashSet<DisplayField>();
			DisplayField df = null;
			int num = 0;
			if (!select.isSeeAll()) {
				for (DisplayField f : select.getAttributes()) {// 遍历select要查询的字段
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
			leaf = new LeafNode(true, file, false, null, num, saveFields, tempTable, false, conditions);// 新的叶结点
			Tree.leaves.put(tempTable, leaf);
		}

		if (!select.isExistWhereCondition()) {
			return;
		}

		/*
		 * 叶节点添加保留字段和条件表达式
		 */
		String name = null;
		String table = null;
		String attr = null;
		DisplayField df = null;
		List<ConditionalExpression> conditions = select.getConditions().get(n);//第n组合取式
		for (ConditionalExpression condition : conditions) {// 遍历条件表达式
			if (condition.isLeftConstant() ^ condition.isRightConstant()) {// 是选择条件表达式

				name = condition.isLeftConstant() ? condition.getRight() : condition.getLeft();// 选择条件表达式中属性全名
				table = condition.isLeftConstant() ? condition.getRightTableName() : condition.getLeftTableName();// 选择条件表达式中属性所涉及的表名
				attr = condition.isLeftConstant() ? condition.getRightAttribute() : condition.getLeftAttribute();// 选择条件表达式中属性名

				if ((leaf = Tree.leaves.get(table)) != null) {// 如果已经存在此叶节点，则在此结点上添加
					leaf.setExistCondition(true);
					leaf.getConditions().add(condition);// 添加条件
					List<Field> list = null;
					try {
						list = DBMS.loadedTables.get(table).getAttributes();// 此表的字段集合
					} catch (Exception e) {
						throw new RuntimeException("没有" + table + "这个表！");
					}
					boolean flag = false;
					for (Field field : list) {// 遍历字段集合
						if (field.getFieldName().equals(attr)) {// 找到同名字段
							df = new DisplayField(field, name, table);// 利用同名字段构造新的保留字段
							if (leaf.getSaveFields().add(df)) {// 如果保留字段集合中无此字段则保留字段数加1
								int i = leaf.getNumberField();
								leaf.setNumberField(i + 1);
							}
							flag = true;
						}
					}
					if (flag == false) {
						throw new RuntimeException("没有" + attr + "这个属性！");
					}
				}
			} else {// 是连接条件表达式

				String leftTable = condition.getLeftTableName();

				if ((leaf = Tree.leaves.get(leftTable)) != null) {// 如果已经存在此叶节点，则在此结点上添加

					List<Field> list = null;
					try {
						list = DBMS.loadedTables.get(leftTable).getAttributes();// 此表的字段集合
					} catch (Exception e) {
						throw new RuntimeException("没有" + table + "这个表！");
					}
					boolean flag = false;
					for (Field field : list) {// 遍历字段集合
						if (field.getFieldName().equals(condition.getLeftAttribute())) {// 找到同名字段
							df = new DisplayField(field, condition.getLeft(), leftTable);// 利用同名字段构造新的保留字段
							if (leaf.getSaveFields().add(df)) {// 如果保留字段集合中无此字段则保留字段数加1
								int i = leaf.getNumberField();
								leaf.setNumberField(i + 1);
							}
							flag = true;
						}
					}
					if (flag == false) {
						throw new RuntimeException("没有" + condition.getLeft() + "这个属性！");
					}
				}

				String rightTable = condition.getRightTableName();

				if ((leaf = Tree.leaves.get(rightTable)) != null) {// 如果已经存在此叶节点，则在此结点上添加

					List<Field> list = null;
					try {
						list = DBMS.loadedTables.get(rightTable).getAttributes();// 此表的字段集合
					} catch (Exception e) {
						throw new RuntimeException("没有" + rightTable + "这个表！");
					}
					boolean flag = false;
					for (Field field : list) {// 遍历字段集合
						if (field.getFieldName().equals(condition.getRightAttribute())) {// 找到同名字段
							df = new DisplayField(field, condition.getRight(), rightTable);// 利用同名字段构造新的保留字段
							if (leaf.getSaveFields().add(df)) {// 如果保留字段集合中无此字段则保留字段数加1
								int i = leaf.getNumberField();
								leaf.setNumberField(i + 1);
							}
							flag = true;
						}
					}
					if (flag == false) {
						throw new RuntimeException("没有" + condition.getRight() + "这个属性！");
					}
				}
			}
		}
	}

	/**
	 * 创建内部节点
	 */
	public static void buildInternalNode(Select select, int n) {
		String leftTable = null;
		String rightTable = null;
		Node left = null;// 左结点
		Node right = null;// 右结点
		ConditionalExpression joinCondition = null;// 连接条件

		Tree.inners = new ArrayList<InternalNode>();// 内部结点集合
		if (!select.isExistWhereCondition()) {
			return;
		}
		List<ConditionalExpression> conditions = select.getConditions().get(n);//第n组合取式
		for (ConditionalExpression condition : conditions) {
			if (!condition.isLeftConstant() && !condition.isRightConstant()) {// 是连接条件
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
	 * 返回根节点，并且如果需要做笛卡尔积操作则构建哈夫曼树
	 */
	private static Node buildDKNode() {
		List<Node> list = new ArrayList<Node>();
		for (Node node : Tree.leaves.values()) {//寻找没有父亲的也结点
			if (!node.isHasFather()) {
				list.add(node);
			}
		}
		if (Tree.inners.size() != 0) {
			for (Node node : Tree.inners) {//寻找没有父亲的内部节点
				if (!node.isHasFather()) {
					list.add(node);
				}
			}
		}
		while (list.size() > 1) {
			java.util.Collections.sort(list, new Comparator<Node>() {//自定义比较方式做排序操作,按每个结点的记录数排序
				@Override
				public int compare(Node n1, Node n2) {
					return n1.getAccountNumbers() - n2.getAccountNumbers();
				}
			});
			
			Node left = list.remove(0);
			Node right = list.remove(0);
			Node inner = buildInternal(left, right, null);//记录数最少的两个节点做笛卡尔积
			list.add(inner);
		}
		return list.get(0);
	}
	/**
	 * 创建内部节点
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
		Set<DisplayField> saveFields = new HashSet<DisplayField>();//保留字段集合

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
			 * 如果左孩子是叶结点，则置step为连接条件左部所涉及的表含有的字段数
			 * 如果做孩子是内部结点,则遍历左孩子结点下的所有关系表,计算所含字段个数，赋值给step
			 */
			tdf.setColumn(tdf.getColumn() + step);// 将右半部分的所有保留字段所在列加step
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
	 * 遍历查询树
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
	 * 将关系表中满足条件的记录写入叶子节点临时文件
	 * 
	 * @throws Exception
	 */
	public static void creatLeafFile(LeafNode leaf) throws Exception {
		File sor = DBMS.loadedTables.get(leaf.getTableName()).getFile();
		if (!sor.exists()) {
			throw new RuntimeException(sor + "不存在！");
		}
		List<String> accounts = IOUtil.readFile(sor);//将文件中的数据读入内存
//		System.out.println(accounts.size());
		Set<Integer> retainIndex = new HashSet<Integer>();//用于存储满足条件的记录所在行,初始化所有行都满足条件
		
		List<ConditionalExpression> conditions = leaf.getConditions();
		
		boolean existIndex = false;
		int i;
		for (i = 0; i < conditions.size(); i++) {//找出表达式集合中第一个带有索引的获取索引集合给retainIndex赋值
			Set<Integer> indexs = OperUtil.getIndexOfBT(conditions.get(i));
			if (indexs != null) {
				existIndex = true;
				retainIndex = indexs;
				break;
			}
		}
		while (++i < conditions.size()) {//继续向后遍历，获取索引集合取交集，得到最终满足条件的索引集合
			Set<Integer> indexs = OperUtil.getIndexOfBT(conditions.get(i));
			if (indexs != null) {
				existIndex = true;
				retainIndex.retainAll(indexs);
			}
			if (retainIndex.size() == 0) {
				break;
			}
		}
		
		if (!existIndex) {//如果没有索引则全部行都符合初步判定
			for (int j = 0; j < accounts.size(); j++) {
				retainIndex.add(j);
			}
		}

		//获取要保留的字段所在列
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
	 * 将叶子节点临时文件中满足条件的记录写入内部节点的中间文件
	 * 采用哈希连接
	 */
	public static void creatInnerFile(InternalNode node) {
		Node leftChild = node.getLeftNode();
		Node rightChild = node.getRightNode();

		ConditionalExpression condition = node.getJoinCondition();//连接条件

		File leftFile = leftChild.getFile();
		File rightFile = rightChild.getFile();
		File tempFile = node.getFile();

		BufferedReader leftReader = null;
		BufferedReader rightReader = null;
		PrintWriter pw = null;
		
		List<String> lines = new ArrayList<String>();//保存记录
		Hash hashTable = null;//哈希表
		try {
			leftReader = new BufferedReader(new InputStreamReader(new FileInputStream(leftFile), "GBK"));
			rightReader = new BufferedReader(new InputStreamReader(new FileInputStream(rightFile), "GBK"));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "GBK"));
			
			if (node.isIsJoinOperation()) {//如果是自然连接
				if (leftChild.getAccountNumbers() <= rightChild.getAccountNumbers()) {//左边孩子结点的记录条数少
					String type = condition.getLeftType();//连接表达式左属性类型
					int column = condition.getLeftcolumn();//连接表达式左属性所在列
					String line = null;
					int index = 0;//行数
					hashTable = new Hash(leftChild.getAccountNumbers());//以左孩子节点的记录条数作为哈希表的长度实例化哈希表
					long hashCode = 0;
					Set<Integer> indexs = null;//行集合
					while ((line = leftReader.readLine()) != null) {
						lines.add(line);//将记录添加至记录集合
						//将字段值转化为哈希码作为哈希表的关键字
						hashCode = "Integer".equalsIgnoreCase(type) ? Long.parseLong(line.split(",")[column]) : line.split(",")[column].hashCode();
						indexs = new HashSet<Integer>();
						indexs.add(index);
						hashTable.insert(new DataEntry(hashCode, indexs));//向哈希表中插入元素
						index += 1;
					}
				} else {//右边孩子结点的记录条数少
					String type = condition.getRightType();//连接表达式右属性类型
					int column = condition.getRightcolumn();//连接表达式右属性所在列
					String line = null;
					int index = 0;//行数
					hashTable = new Hash(rightChild.getAccountNumbers());//以右孩子节点的记录条数作为哈希表的长度实例化哈希表
					long hashCode = 0;
					Set<Integer> indexs = null;//行集合
					while ((line = rightReader.readLine()) != null) {
						lines.add(line);//将记录添加至记录集合
						//将字段值转化为哈希码作为哈希表的关键字
						hashCode = "Integer".equalsIgnoreCase(type) ? Long.parseLong(line.split(",")[column]) : line.split(",")[column].hashCode();
						indexs = new HashSet<Integer>();
						indexs.add(index);
						hashTable.insert(new DataEntry(hashCode, indexs));//向哈希表中插入元素
						index += 1;
					}
				}
				int accountNumbers = 0;
				if (leftChild.getAccountNumbers() <= rightChild.getAccountNumbers()) {//左边孩子结点的记录条数少
					String type = condition.getRightType();//连接表达式右属性类型
					int column = condition.getRightcolumn();//连接表达式右属性所在列
					long hashCode = 0;
					String line = null;
					int pos;
					while ((line = rightReader.readLine()) != null) {
						hashCode = "Integer".equalsIgnoreCase(type) ? Long.parseLong(line.split(",")[column]) : line.split(",")[column].hashCode();
						pos = hashTable.search(hashCode);//查询哈希表
						if (pos != -1) {//找到对应值所在位置
							for (int i : hashTable.getHashTable()[pos].getIndexs()) {
								pw.println(lines.get(i) + "," + line);
								accountNumbers += 1;
							}
						}
					}
				} else {//右边孩子结点的记录条数少
					String type = condition.getLeftType();//连接表达式左属性类型
					int column = condition.getLeftcolumn();//连接表达式左属性所在列
					long hashCode = 0;
					String line = null;
					int pos;
					while ((line = leftReader.readLine()) != null) {
						hashCode = "Integer".equalsIgnoreCase(type) ? Long.parseLong(line.split(",")[column]) : line.split(",")[column].hashCode();
						pos = hashTable.search(hashCode);//查询哈希表
						if (pos != -1) {//找到对应值所在位置
							for (int i : hashTable.getHashTable()[pos].getIndexs()) {
								pw.println(line + "," + lines.get(i));
								accountNumbers += 1;
							}
						}
					}
				}
				node.setAccountNumbers(accountNumbers);//此内节点的记录数
			}
			if (!node.isIsJoinOperation()) {//笛卡尔积操作
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
				node.setAccountNumbers(accountNumbers);//此内节点的记录数
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
					System.out.println(leftFile.getName() + " 文件正在被占用，无法删除！");
				}
				if (!rightFile.delete()) {
					System.out.println(rightFile.getName() + " 文件正在被占用，无法删除！");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 显示select查询结果，投影操作
	 * 
	 * @param rootNode
	 */
	public static void display(Select select, Set<String> displayAccounts, Set<DisplayField> fields) {
		
		if (select.isSeeAll()) {// 如果是select*操作，则将文件中的内容全部显示
			for (String line : displayAccounts) {
				System.out.println(line);
			}
			System.out.println("Total lines:" + displayAccounts.size());
		} else {
			String[] attrs = null;
			for (DisplayField df : select.getAttributes()) {
				System.out.printf("%10s%10s\t", df.getType(), df.getFieldName());
			}
			System.out.println();// 换行
			for (String line : displayAccounts) {
				attrs = line.split(",");
				for (DisplayField df1 : select.getAttributes()) {// 要显示的字段集合
					for (DisplayField df2 : fields) {// rootNode中的字段集合
						if (df1.getName().equals(df2.getName())) {// 获取要显示的字段在文件中的列
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
