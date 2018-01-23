package com.zhouchao.db;

import java.io.Serializable;
import java.util.List;

public class Loginpass implements Serializable {
    private static final long serialVersionUID=7981560250804078367L;
    List<Login> logins;

    public List<Login> getLogins() {
        return logins;
    }

    public void setLogins(List<Login> logins) {
        this.logins = logins;
    }

    @Override
    public String toString() {
        return "Loginpass{" +
                "logins=" + logins +
                '}';
    }
}
