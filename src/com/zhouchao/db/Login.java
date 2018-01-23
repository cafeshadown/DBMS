package com.zhouchao.db;

import java.io.Serializable;
import java.util.List;

public class Login implements Serializable {
    private static final long serialVersionUID=1L;
       String loginname;//用户名
       String password;//密码
       int type;       //类型
       List<String> database;//管理的数据库

    @Override
    public String toString() {
        return "Login{" +
                "loginname='" + loginname + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", database=" + database +
                '}';
    }

    public List<String> getDatabase() {
        return database;
    }

    public void setDatabase(List<String> database) {
        this.database = database;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
