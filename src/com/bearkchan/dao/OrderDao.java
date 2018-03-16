package com.bearkchan.dao;

import com.bearkchan.domain.Order;
import com.bearkchan.domain.OrderItem;
import com.bearkchan.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrderDao {
    //    向orders表插入数据
    //向orders表插入数据
    public void addOrders(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
        Connection conn = DataSourceUtils.getConnection();
        runner.update(conn,sql, order.getOid(),order.getOrdertime(),order.getTotal(),order.getState(),
                order.getAddr(),order.getName(),order.getTelephone(),order.getUser().getUid());
    }

    //    向orderitem表插入数据
    public void addOrderItem(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into orderitem values(?,?,?,?,?)";
        Connection conn = DataSourceUtils.getConnection();
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem:orderItems){
            runner.update(conn,sql,orderItem.getItemid(),orderItem.getCount(),orderItem.getSubtotal(),orderItem.getProduct().getPid(),orderItem.getOrder().getOid());
        }
    }

    public void updateOrderAdrr(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set addr=?,name=?,telephone=? where oid=?";
        runner.update(sql,order.getAddr(),order.getName(),order.getTelephone(),order.getOid());
    }

    public void updateOrderState(String r6_order) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set state=? where oid=?";
        runner.update(sql,1,r6_order);
    }

    public List<Order> findAllOrders(String uid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from orders where uid=?";
        return runner.query(sql,new BeanListHandler<Order>(Order.class),uid);
    }

    public List<Map<String, Object>> findAllOrderItemByOid(String oid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select i.count,i.subtotal,p.pimage,p.pname from orderitem i, product p where i.pid=p.pid and i.oid=?";
        List<Map<String, Object>> mapList = runner.query(sql, new MapListHandler(), oid);
        return mapList;
    }
}
