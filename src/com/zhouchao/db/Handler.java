package com.zhouchao.db;
/**
 * 工厂
 *
 */
class Handler {
	private Operate operate = null;
	
	public Handler(String account) {
		String[] operationArray = account.split("\\s");//按空字符分割输入语句

		OperationEnum op = null;//声明操作符
		try {
			op = OperationEnum.valueOf(operationArray[0].toUpperCase());//将操作字符串转化为枚举对象，方便switch操作
		} catch (Exception e) {
			System.out.println("语句非法!!!!");
			return;
		}
		switch (op) {//匹配操作
		case SHOW:
			this.operate = new Show(account);
			break;
		case CREATE:
			this.operate = new Create(account);
			break;
		case INSERT:
			Check.hadUseDatabase();
			this.operate = new Insert(account);
			break;
		case DELETE:
			Check.hadUseDatabase();
			this.operate = new Delete(account);
			break;
		case ALTER:
			Check.hadUseDatabase();
			this.operate = new Alter(account);
			break;
		case UPDATE:
			Check.hadUseDatabase();
			this.operate = new Update(account);
			break;
		case DROP:
			Check.hadUseDatabase();
			this.operate = new Drop(account);
			break;
		case SELECT:
			Check.hadUseDatabase();
			this.operate = new Select(account);
			break;
		case USE:
			this.operate = new Use(account);
			break;
		default:
			System.out.println("指令不合法");
			break;
		}
	}
	
	public void start() {
		try {
			this.operate.start();
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
