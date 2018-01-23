package com.zhouchao.db;
import javax.swing.event.ListDataEvent;
import java.io.*;
import java.util.*;

/**
 * 数据库主类
 *
 */
public class DBMS {
	public static final String ROOTPATH = "." + File.separator + "database";//根目录
	public static String currentPath;//当前目录
	public static DataDictionary dataDictionary = null;//数据库字典
	public static Map<String, Table> loadedTables;//已加载的关系表
	public static Map<String, Field> indexEntry;//索引集合
	public static String loginname;
	public static String password;
	public static List<String> databases;
	public static int type;
	public static File file = new File("admin.txt");
	public static Loginpass l;
	public DBMS() {
		DBMS.loadedTables = new HashMap<String, Table>();
		DBMS.indexEntry = new HashMap<String, Field>();
		DBMS.currentPath = ROOTPATH;

	}
	/**
	 * 输入指令
	 * @throws Exception 
	 */
	public void start() throws Exception {
		Scanner scan = new Scanner(System.in);
		String account = null;
		while (true) {
			account = OperUtil.input(scan);//从控制台接收一条命令
			new Handler(account).start();//工厂运转
			
		}
	}
	public static void loginwrite(){
		try{
			FileOutputStream fout = new FileOutputStream("database.config");
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(l);
			out.close();
			fout.close();
		}catch(FileNotFoundException e){
			System.out.println("File Not Found!");
		}catch(IOException e){
			System.out.println("I/O Error!");
		}
	}
	public static void loginread(){
		try{
			FileInputStream fin = new FileInputStream("database.config");
			ObjectInputStream in = new ObjectInputStream(fin);
			l=(Loginpass) in.readObject();
			in.close();
			fin.close();
		}catch(FileNotFoundException e){
			System.out.println("File Not Found!");
		}catch(ClassNotFoundException e){
			System.out.println("ClassNotFoundException!");
		}catch(IOException e){
			System.out.println("I/O Error!");
		}
	}
	public static  void login() throws IOException, ClassNotFoundException {
		Scanner S = new Scanner(System.in);
		System.out.println("Please enter your username：");
		loginname = S.next();
		System.out.println("Please enter your password:");
		password = S.next();
		loginread();
		List<Login> logins1= l.getLogins();
		int chos=2;
		for (int i = 0; i <logins1.size() ; i++) {
			if (logins1.get(i).loginname.equals(loginname)&&logins1.get(i).password.equals(password)){
				chos=logins1.get(i).type;
				databases = logins1.get(i).database;
			}
		}
		System.out.println(l);
		if (chos==1){
			System.out.println("管理员登陆成功");
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.append(loginname);
			ps.close();
		}else if (chos==0){
			System.out.println("普通用户登录成功");
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.append(loginname);
			ps.close();
		}else{
			System.out.println("用户名或密码错误");
			return;
		}
	}
	public static  void newlogin() throws IOException, ClassNotFoundException {
		int i=0;
        admin admin = new admin();
	    loginread();
		System.out.println(l);
		Login log= new Login();
		Scanner C = new Scanner(System.in);
		System.out.println("Please enter your username：");
		log.setLoginname(C.next());
		System.out.println("Please enter your password:");
		log.setPassword(C.next());
		System.out.println("Please enter type:(1:Admin，0:Common)");
		log.setType(C.nextInt());
		if(log.getType()==1){
		}else {
			System.out.println("Please enter controlled tables:");
			List<String> databases = new ArrayList<String>();
			while (i!=2) {
				System.out.println("Continue：1,Finish 2");
				Scanner scanner =new Scanner(System.in);
				i=scanner.nextInt();
				switch (i) {
					case 1:
						System.out.println("Please enter tablename：");
						databases.add(C.next());
						break;
					case 2:
						System.out.println("Finish");
						log.setDatabase(databases);
						break;
				}
			}
		}
       try {
		   l.getLogins().add(log);
		   admin.newlogin(l);
		   System.out.println("Sign up successful");
		   login();
	   }catch (Exception e){
		   System.out.println("Sign up failed");
		   return;
	   }
	}
	/**
	 * 启动程序
	 * @param args
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		System.out.println("1：Login");
		System.out.println("2：Sign up");
		Scanner scanner = new Scanner(System.in);
		int i = scanner.nextInt();
		switch (i){
			case 1:
				login();
				break;
			case 2:
                newlogin();
				break;

		}
		try {
			new DBMS().start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
