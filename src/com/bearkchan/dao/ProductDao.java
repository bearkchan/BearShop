package com.bearkchan.dao;

import com.bearkchan.domain.Category;
import com.bearkchan.domain.Product;
import com.bearkchan.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class ProductDao {
    //   获得热门商品
    public List<Product> findHotProductList() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where is_hot = ? limit ?,?";
        List<Product> query = runner.query(sql, new BeanListHandler<Product>(Product.class), 1, 0, 9);
        return query;
    }

    //   获得热门商品
    public List<Product> findNewProductList() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product  order by pdate desc limit ?,?";
        List<Product> query = runner.query(sql, new BeanListHandler<Product>(Product.class), 0, 9);
        return query;
    }

    public List<Category> findAllCategroy() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from category";
        List<Category> query = runner.query(sql, new BeanListHandler<Category>(Category.class));
        return query;
    }

    public int getCount(String cid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from product where cid = ? and pflag = 0 ";
        Long query = (Long) runner.query(sql, new ScalarHandler(),cid);
        return query.intValue();
    }

    public List<Product> findProductByPage(String cid, int index, int currentCount) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where cid = ? limit ?,?";
        List<Product> query = runner.query(sql, new BeanListHandler<Product>(Product.class), cid, index, currentCount);
        return query;
    }

    public Product findProductByPid(String pid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where pid = ? ";
        Product query = runner.query(sql, new BeanHandler<Product>(Product.class), pid);
        return query;
    }
}
