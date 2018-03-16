package com.bearkchan.service;

import com.bearkchan.domain.Category;
import com.bearkchan.dao.AdminDao;
import com.bearkchan.domain.Product;

import java.sql.SQLException;
import java.util.List;

public class AdminService {
    public List<Category> findAllCategory() {
        AdminDao dao = new AdminDao();
        try {
            return  dao.findAllCategory();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveProduct(Product product) throws SQLException {
        AdminDao dao = new AdminDao();
        dao.saveProduct(product);
    }
}
