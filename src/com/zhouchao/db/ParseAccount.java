package com.zhouchao.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseAccount {
	/**
	 * 解析条件表达式！！！
	 * @param left
	 * @param right
	 * @param condition
	 * @throws Exception
	 */
	private static void parseCondition(String left, String right, ConditionalExpression condition) throws Exception{
		String[] sp = null;
		String table = null;
		String name = null;
		
		condition.setLeft(left);
		condition.setRight(right);
		
		if (left.contains(".") && !left.matches("^\".+\"$")) {//如果符合xx.xx格式则为属性
			boolean flag = true;
			sp = left.split("\\.");
			table = sp[0];
			name = sp[1];
			if (!DBMS.loadedTables.containsKey(table)) {//没有这个表
				System.out.println("属性非法！" + sp);
				return;
			}
			for (Field field : DBMS.loadedTables.get(table).getAttributes()) {//遍历这个表的属性集合
				if (field.getFieldName().equals(name)) {//这个表含有条件表达式左部的属性，利用表中的属性给条件表达式中的属性赋值
					condition.setLeftType(field.getType());//左属性类型
					condition.setLeftcolumn(field.column);//左属性所在列
					condition.setLeftIsIndex(field.isIndex());//左属性是否为索引列
					condition.setLeftRoot(field.indexRoot);//左属性索引列树根，若不是索引列，则为null
					flag = false;
					break;
				}
			}
			if (flag == false) {
				condition.setLeftConstant(flag);//条件表达式的左部是属性
				condition.setLeftTableName(table);//左部所属表名
				condition.setLeftAttribute(name);//左部属性名
			} else {
				throw new RuntimeException("关系" + table + "没有" + name + "这个属性！");//将异常抛出
			}
			
		}
		if (right.contains(".") && !right.matches("^\".+\"$")) {//如果符合XX.XX格式，则为属性
			boolean flag = true;
			sp = right.split("\\.");
			table = sp[0];
			name = sp[1];
			if (!DBMS.loadedTables.containsKey(table)) {
				System.out.println("语句非法！" + sp);
				return;
			}
			for (Field field : DBMS.loadedTables.get(table).getAttributes()) {//遍历这个表的属性集合
				if (field.getFieldName().equals(name)) {//这个表含有条件表达式右部的属性，利用表中的属性给条件表达式中的属性赋值
					condition.setRightType(field.getType());//右属性类型
					condition.setRightcolumn(field.column);//右属性所在列
					condition.setRightIsIndex(field.isIndex());//右属性是否为索引列
					condition.setRightRoot(field.indexRoot);//右属性索引列树根，若不是索引列，则为null
					flag = false;
					break;
				}
			}
			if (flag == false) {
				condition.setRightConstant(flag);//条件表达式的又部是属性
				condition.setRightTableName(table);//又部所属表名
				condition.setRightAttribute(name);//又部属性名
			} else {
				throw new RuntimeException("关系" + table + "没有" + name + "这个属性！");//将异常抛出
			}	
		}
		if (!left.contains(".") && !right.contains(".")) {
			System.out.println("语句非法！常量比较");
		}
	}
	/**
	 * 解析create-table语句 获取table对象！！！
	 * 
	 * @param account
	 * @param name
	 * @return 返回一个Table实例
	 * @throws Exception
	 */
	public static Table parseTable(String account, String name) throws Exception {
		// 截取最外层括号内的字符串
		String attr = account.substring(account.indexOf('(') + 1, account.lastIndexOf(')')).trim();
		// 分割成字段数组
		String[] attrs = attr.split(",");// 按照 逗号 分割各个字段

		if (attrs.length == 0) {
			throw new RuntimeException("非法语句！");
		}

		List<Field> fields = new ArrayList<Field>();
		Field field = null;
		String[] infos = null;
		for (int i = 0; i < attrs.length; i++) {// 遍历字段数组
			infos = attrs[i].trim().split("\\s");
			field = new Field();
			field.setColumn(i);
			field.setFieldName(infos[0]);

			if (infos[1].matches("(?i)(Integer)")) {// integer类型
				field.setType(infos[1]);
				field.setLength(4);
			} else {// varchar类型
				String[] t = infos[1].split("\\(|\\)");
				field.setType(t[0]);
				field.setLength(Integer.parseInt(t[1]));
			}

			if (infos.length == 4) {// 此属性带有主键或非空
				if (infos[2].equalsIgnoreCase("not")) {// 非空
					field.setNull(false);
				} else {// 主键
					field.setPrimary(true);
				}
			}
			fields.add(field);
		}
		File file = new File(DBMS.currentPath + File.separator + name);// 数据文件
		File configFile = new File(DBMS.currentPath + File.separator + name + ".config");// 配置文件

		return new Table(name, file, configFile, fields);// 表名 数据文件 配置文件 字段集合
	}
	/**
	 * 解析Use语句 获取use操作的数据库名！！！
	 * @param account
	 * @return
	 */
	public static String parseUse(String account) {
		String pattern = "(?i)^(use)\\s(database)\\s\\S+\\s?;$";
		if (!account.matches(pattern)) {
			throw new RuntimeException("匹配失败！语句非法");
		}	
		return account.split("\\s|;")[2];
	}
	/**
	 * 解析create语句！！！
	 * 
	 * @param account：输入的一条命令
	 * @return 1：创建数据库 2：创建表
	 */
	public static int parseCreate(Create create) {
		String tableP = "(?i)^(create)\\s(table)\\s(\\w+)\\s?\\(\\s?((\\S+\\sInteger)|(\\S+\\sVarchar\\(\\w+\\)))((\\sPrimary\\sKey)|(\\sNot\\sNull))?"
				+ "(\\s?,\\s?((\\S+\\sInteger)|(\\S+\\sVarchar\\(\\w+\\)))((\\sPrimary\\sKey)|(\\sNot\\sNull))?)*\\);$";

		String databaseP = "(?i)^(create)\\s(database)\\s\\S+\\s?;$";
		
		String indexP = "(?i)^(create)\\s(index)\\s(\\S+)\\s(on)\\s(\\S+)\\s(\\S+)\\s?;$";
		
		String[] arr = create.getAccount().split("\\s|;|\\(");//将语句分割为单个单词

		if (create.getAccount().matches(tableP)) {// 创建表
			create.setName(arr[2]);// 待创建的表名
			if (DBMS.dataDictionary == null) {
				throw new RuntimeException("请先选择数据库！");
			}
			if (DBMS.dataDictionary.getTables().contains(create.getName())) {// 表已存在
				throw new RuntimeException("此表已存在！");
			} else {// 表不存在，则建表
				return 2;
			}
		} else if (create.getAccount().matches(databaseP)) {// 创建数据库
			create.setName(arr[2]);//待创建的数据库名
			return 1;
		} else if (create.getAccount().matches(indexP)) {
			create.setName(arr[4]);//待创建索引的表名
			return 3;
		} else {
			throw new RuntimeException("非法语句！");
		}
	}

	/**
	 * 解析show操作！！！
	 * 
	 * @param account
	 * @return 1：showdatabase 2：showtables
	 */
	public static int parseShow(String account) {
		String showDatabase = "(?i)(show)\\s(database)\\s?;";
		String shoeTables = "(?i)(show)\\s(tables)\\s?;";

		if (account.matches(shoeTables)) {
			return 2;
		} else if (account.matches(showDatabase)) {
			return 1;
		} else {
			throw new RuntimeException("非法语句！");
		}

	}

	/**
	 * 解析insert语句 获取table对象！！！
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 */
	public static void parseInsert(Insert insert) throws Exception {
		String account = insert.getAccount();
		
		String pattern = "(?i)^(insert)\\s(into)\\s[a-zA-Z]+\\s(values)\\s?"
				+ "\\(\\s?([\\S]+)\\s?(,\\s?[\\S]+\\s?)*\\);$";
		if (!account.matches(pattern)) {
			throw new RuntimeException("匹配失败，语句非法！");
		}
		String tableName = account.split("\\s")[2];
		insert.setTableName(tableName);
		insert.setValues(ParseAccount.subValues(account));


	}
	/**
	 * 解析delete操作!!!
	 * @param account
	 */
	public static void parseDelete(Delete delete) throws Exception{
		String account = delete.getAccount();
		
		String pattern = "(?i)^(delete)\\s(from)\\s[\\S]+(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
		if (!account.matches(pattern)) {
			throw new RuntimeException("匹配失败，语句非法!");
		}
		
		String[] array = account.split("\\s|;");
		String tableName = array[2];
		
		delete.setTableName(tableName);

		
		if (!account.contains("where") && !account.contains("WHERE")) {//没有where
			delete.setExistWhereCondition(false);
		} else {//有where，则处理条件表达式
			List<List<String>> arr = ParseAccount.subOper(account, "where", ";");
			
			List<List<ConditionalExpression>> lc = new ArrayList<List<ConditionalExpression>>();//or表达式
			List<ConditionalExpression> conditions = null;//and表达式
			ConditionalExpression condition = null;//单个表达式
			
			String[] lr = null;
			String left = null;
			String right = null;
			try {
				for (List<String> lor : arr) {
					conditions = new ArrayList<ConditionalExpression>();
					for (String str : lor) {
						condition = new ConditionalExpression();
						if (str.contains("<")) {
							lr = str.split("<");
							left = lr[0];
							right = lr[1];
							condition.setOper("<");
							parseCondition(left, right, condition);
						} else if (str.contains(">")) {
							lr = str.split(">");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">");
						} else if (str.contains("<=")) {
							lr = str.split("<=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("<=");
						} else if (str.contains(">=")) {
							lr = str.split(">=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">=");
						} else {
							lr = str.split("=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("=");
						}
						conditions.add(condition);
					}
					lc.add(conditions);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
			delete.setConditions(lc);
		}

	}
	/**
	 * 解析drop语句，返回表名!!!
	 * @param account
	 * @return
	 */
	public static String parseTableName(String account) {
		return account.split("\\s|;")[2];
	}
	
	/**
	 * 解析alter操作（操作所涉及的表名和操作类型）!!!
	 * @param account
	 * @return 
	 * @throws Exception 
	 */
	public static void parseAlter(Alter alter) throws Exception {
		String account = alter.getAccount();
		
		String patternAdd = "(?i)^(alter)\\s(table)\\s\\S+\\s(add)\\s((\\S+\\sInteger)|(\\S+\\sVarchar\\(\\w+\\)))\\s?;$";
		String patternDrop = "(?i)^(alter)\\s(table)\\s\\w+\\s(drop)(\\s\\w+)(\\s?,\\s?\\w+)*\\s?;$";
	
		if (account.matches(patternAdd)) {//alter-add操作
			alter.setAdd(true);
		} else if (account.matches(patternDrop)) {//alter-drop操作
			alter.setAdd(false);
		} else {
			throw new RuntimeException("匹配失败！语句非法");
		}
		
		String[] arr = account.split("\\s");
		String tableName = arr[2];//获取语句中涉及的表名
		alter.setTableName(tableName);
	}
	/**
	 * 解析alter-add&alter-drop操作
	 * @param alter
	 * @param account
	 * @param table
	 */
	public static void parseAlterAddAndDrop(Alter alter) {
		String account = alter.getAccount();
		Table table = alter.getTable();
		String[] attrs = ParseAccount.subAccount(account, null, ";");
		
		List<Field> fields = new ArrayList<Field>();
		alter.setFields(fields);
		if (alter.isAdd()) {//是alter-add操作，新建属性，添加至待添加关系的属性集合
			
			String name = null;
			String type = null;
			int step = 0;
			
			for (String str : attrs) {//添加属性，新建字段
				int length = 4;//Integer 4字节
				name = str.split("\\s")[0];
				if (str.contains("(")) {//varchar类型
					type = str.split("\\s")[1].split("\\(|\\)")[0];
					length = Integer.parseInt(str.split("\\s")[1].split("\\(|\\)")[1]);//设置最大长度
				} else {//Integer类型
					type = str.split("\\s")[1];
				}
				alter.getFields().add(new Field(name, type, length, table.getAttributes().size() + step, true, false, false, null));
				step += 1;
			}
		}
		if (!alter.isAdd()) {//是alter-drop操作
			for (String name : attrs) {
				for (Field field : table.getAttributes()) {//找到对应的属性，添加至alter
					if (field.getFieldName().equals(name)) {//名字对应
						alter.getFields().add(field);
					}
				}
			}
			
		}
	}
	
	/**
	 * 截取最外层括号中的字符串并删除字符串中的空格
	 * 
	 * @param account
	 * @return
	 */
	public static String subValues(String account) {
		String[] values = account.substring(account.indexOf('(') + 1, account.lastIndexOf(')')).split(",");
		String newLine = "";
		for (String str : values) {
			newLine = newLine + str.trim() + ",";
		}
		return newLine.substring(0, newLine.lastIndexOf(","));
	}
	/**
	 * 截取语句!!!
	 * 
	 * @param account
	 *            语句
	 * @param start
	 *            截取的开始位置
	 * @param end
	 *            截取的末尾
	 * @return 
	 * 			  截取后分割成字符串数组
	 */
	public static String[] subAccount(String account, String start, String end) {
		String temp = null;
		Pattern pattern = null;
		if (start == null) {
			pattern = Pattern.compile("(?i)(?=(add)|(drop)).+(?<=" + end + ")");
		} else {
			pattern = Pattern.compile("(?i)(?=" + start + ").+(?<=" + end + ")");
		}
		Matcher matcher = pattern.matcher(account);
		if (matcher.find()) {
			temp = matcher.group(0);
		}
		temp = temp.split("(?i)(select)|(set)|(add)|(drop)|(from)|(where)|;")[1];// 语句中不能出现关键字，即使单词中含有也不行
		String[] fields = temp.split("(?i),|(and)");// 要查看的字段
		for (int i = 0; i < fields.length; i++) {
			fields[i] = fields[i].trim();
		}

		return fields;
	}
	/**
	 * 截取delete和update中的条件表达式
	 * @return
	 */
	public static List<List<String>> subOper(String account, String start, String end) {
		List<List<String>> list= new ArrayList<List<String>>();
		List<String> tlist = null;
		
		Pattern pattern = Pattern.compile("(?i)(?=" + start + ").+(?<=" + end + ")");
		Matcher matcher = pattern.matcher(account);
		String temp = null;
		if (matcher.find()) {
			temp = matcher.group(0);
		}
		temp = temp.split("(?i)(where)|;")[1];// 语句中不能出现关键字，即使单词中含有也不行
		String[] arr1 = temp.split("(?i)(or)");
		for (String arr2 : arr1) {
			tlist = new ArrayList<String>();
			for (String arr3 : arr2.trim().split("(?i)(and)")) {
				tlist.add(arr3.trim());
			}
			list.add(tlist);
		}
		return list;
	}
	/**
	 * 解析update语句！！！
	 * @param update
	 * @param account
	 */
	public static void parseUpdate(Update update, String account) {
		String pattern = "(?i)^(update)\\s\\w+\\s(set)(\\s\\w+(=)\\S+)(\\s?,\\s?\\w+(=)\\S+)*(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
		if (!account.matches(pattern)) {
			System.out.println("语句不合法！");
			return;
		}
		String tableName = account.split("\\s")[1];

		update.setTableName(tableName);
		
		String end = "where";
		if (!account.contains("WHERE") && !account.contains("where")) {//语句不含有where则表名没有where条件
			update.setExistWhereCondition(false);//没有where条件
			end = ";";
		}
		String[] setValues = ParseAccount.subAccount(account, "set", end);
		
		List<String[]> set =  new ArrayList<String[]>();
		String[] kv = null;
		for (String string : setValues) {
			kv = new String[2];
			kv[0] = string.split("=")[0];
			kv[1] = string.split("=")[1];
			set.add(kv);
		}
		update.setSet(set);
		
		if (update.isExistWhereCondition()) {//有where条件，
			List<List<String>> cs = ParseAccount.subOper(account, "where", ";");
			List<List<ConditionalExpression>> lo = new ArrayList<List<ConditionalExpression>>();
			List<ConditionalExpression> conditions = null;
			ConditionalExpression condition = null;
			String[] lr = null;
			String left = null;
			String right = null;
			try {
				for (List<String> arr : cs) {
					conditions = new ArrayList<ConditionalExpression>();
					for (String str : arr) {
						condition = new ConditionalExpression();
						if (str.contains("<")) {
							lr = str.split("<");
							left = lr[0];
							right = lr[1];
							condition.setOper("<");
							parseCondition(left, right, condition);
						} else if (str.contains(">")) {
							lr = str.split(">");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">");
						} else if (str.contains("<=")) {
							lr = str.split("<=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("<=");
						} else if (str.contains(">=")) {
							lr = str.split(">=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">=");
						} else {
							lr = str.split("=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("=");
						}
						conditions.add(condition);
					}
					lo.add(conditions);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
			update.setConditions(lo);
		}
		
	}
	/**
	 * 解析select！！！
	 * @param account
	 */
	public static void parseSelect(Select select, String account) {
		/*
		 * 正则表达式 pattern1
		 * 不区分大小写 匹配select语句，例如：
		 * SELECT XX.XX, XX.XX
		 * FROM XX, XX
		 * WHERE XX.XX<15 AND XX.XX=XX.XX AND XX.XX>28;
		 * 暂不支持or操作
		 */
		String pattern = "(?i)^(select)\\s([\\S]+)\\s?(,\\s?[\\S]+)*\\s(from)\\s[\\S]+\\s?(,\\s?[\\S]+)*"
				+ "(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
		if (!account.matches(pattern)) {
			System.out.println("匹配失败，语句非法！");
			return;
		}
		/************************************************************************************************************************************/
//		select = new Select();//实例化select类
		/*********************************************************************
		 * 获取from和where之间的内容
		 */
		String end = "where";
		if (!account.contains("where") && !account.contains("WHERE")) {
			end = ";";
		}
		String[] fromWhere = ParseAccount.subAccount(account, "from", end);
		List<String> list = new ArrayList<String>();
		for (String string : fromWhere) {
			Table table = null;
			try {
				if ((table = DBMS.loadedTables.get(string)) == null) {
					table = OperUtil.loadTable(string);//加载要查询的关系表
					DBMS.loadedTables.put(string, table);//将加载的关系表放入loadedTables中
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			list.add(string);
			select.setNumberTable(select.getNumberTable() + 1);
		}
		select.setTableName(list);
		/*************************************************************************************************************************************
		 * 获取select和from之间的内容
		 */
		String[] selectFrom = ParseAccount.subAccount(account, "select", "from");//字段全名数组，形如A.m
		if (selectFrom[0].equals("*")) {
			select.setSeeAll(true);
		} else {
			List<DisplayField> attributes = new ArrayList<DisplayField>();//用于存储select后的字段信息
			DisplayField df = null;//显示字段
			String tableName = null;//要显示字段所涉及的表名
			String fieldName = null;//字段名
			for (String fullName : selectFrom) {
				/*
				 * 以“.”为分割点，前半部分为字段所属表名，后半部分为字段名
				 */
				try {
					tableName = fullName.split("\\.")[0];
					fieldName = fullName.split("\\.")[1];
				} catch (Exception e) {
					System.out.println(fullName + "格式不正确！");
					return;
				}
				if (DBMS.loadedTables.get(tableName) == null) {
					System.out.println("语句非法！没有" + tableName + "这个表！");
					return;
				}
				for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {//遍历tableName表的字段集合
					if (field.getFieldName().equals(fieldName)) {//找到该字段
						df = new DisplayField(field, fullName, tableName);
						break;
					}
				}
				if (df == null) {
					System.out.println("语句非法！" + fullName + "不存在！");
					return;
				}
				attributes.add(df);
				select.setNumberFeld(select.getNumberFeld() + 1);
			}
			select.setAttributes(attributes);
		}
		/*********************************************************************
		 * 提取where后的条件表达式
		 */
		if (!account.contains("where") && !account.contains("WHERE")) {//没有where
			select.setExistWhereCondition(false);
		} else {//有where，则处理条件表达式
			List<List<String>> arr = ParseAccount.subOper(account, "where", ";");
			
			List<List<ConditionalExpression>> lc = new ArrayList<List<ConditionalExpression>>();//or表达式
			List<ConditionalExpression> conditions = null;//and表达式
			ConditionalExpression condition = null;//单个表达式
			
			String[] lr = null;
			String left = null;
			String right = null;
			try {
				for (List<String> lor : arr) {
					conditions = new ArrayList<ConditionalExpression>();
					for (String str : lor) {
						condition = new ConditionalExpression();
						if (str.contains("<")) {
							lr = str.split("<");
							left = lr[0];
							right = lr[1];
							condition.setOper("<");
							parseCondition(left, right, condition);
						} else if (str.contains(">")) {
							lr = str.split(">");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">");
						} else if (str.contains("<=")) {
							lr = str.split("<=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("<=");
						} else if (str.contains(">=")) {
							lr = str.split(">=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">=");
						} else {
							lr = str.split("=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("=");
						}
						conditions.add(condition);
					}
					lc.add(conditions);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
			select.setConditions(lc);
		}

	}
	/**
	 * 解析create-index语句
	 * @param create
	 */
	public static void parseIndex(Create create) {
		create.setIndexName(create.getAccount().split("\\s|;")[2]);//设置索引名
		create.setFieldName(create.getAccount().split("\\s|;")[5]);//设置属性名
	}
	/**
	 * 解析drop操作
	 * @param
	 * @return
	 */
	public static int parseDrop(String account) {
		String tableP = "(?i)^(drop)\\s(table)\\s\\S+\\s?;$";
		String indexP = "(?i)^(drop)\\s(index)\\s(\\S+)\\s(on)\\s(\\S+)\\s?;$";

		if (account.matches(tableP)) {
			return 1;
		} else if (account.matches(indexP)) {
			return 2;
		} else {
			return -1;
		}
	}
	public static String parseIndexName(String account) {
		return account.split("\\s")[2];
	}
}
