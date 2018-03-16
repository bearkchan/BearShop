package com.bearkchan.service;

import com.bearkchan.dao.UserDao;
import com.bearkchan.domain.User;

import java.sql.SQLException;

public class UserService {
    public boolean regist(User user) throws SQLException {
        UserDao dao = new UserDao();
        int row = dao.regist(user);
        return row > 0 ? true : false;
    }

    //激活
    public void active(String activeCode) {
        UserDao dao = new UserDao();
        try {
            dao.active(activeCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //校验用户名是否存在
    public boolean checkUsername(String username) {
        UserDao dao = new UserDao();
        Long isExists = 0L;
        try {
            isExists = dao.checkUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isExists > 0 ? true : false;
    }

    //用户登录的方法
    public User login(String username, String password) throws SQLException {
        UserDao dao = new UserDao();
        return dao.login(username,password);
    }
}
