package com.zhouchao.db;

import java.io.*;

public class admin {
public void newlogin(Loginpass l){
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
public void loginread(Loginpass l){
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
    }catch(Exception e){
        System.out.println("I/O Error!");
    }
}
}


