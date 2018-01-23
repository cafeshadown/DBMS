package com.zhouchao.db;
/**
 * ����
 *
 */
class Handler {
	private Operate operate = null;
	
	public Handler(String account) {
		String[] operationArray = account.split("\\s");//�����ַ��ָ��������

		OperationEnum op = null;//����������
		try {
			op = OperationEnum.valueOf(operationArray[0].toUpperCase());//�������ַ���ת��Ϊö�ٶ��󣬷���switch����
		} catch (Exception e) {
			System.out.println("���Ƿ�!!!!");
			return;
		}
		switch (op) {//ƥ�����
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
			System.out.println("ָ��Ϸ�");
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
