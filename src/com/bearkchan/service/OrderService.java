package com.bearkchan.service;

import com.bearkchan.dao.OrderDao;
import com.bearkchan.domain.Order;
import com.bearkchan.domain.OrderItem;
import com.bearkchan.utils.DataSourceUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrderService {

    //    提交订单 将订单的数据和订单项的数据存储到数据库中
    public void submitOrder(Order order) {

        OrderDao dao = new OrderDao();
        try {
//            1.开启事务
            DataSourceUtils.startTransaction();
//            2.调用dao存储order表数据的方法
            dao.addOrders(order);
//            3.调用dao存储orderitem表数据的方法
            dao.addOrderItem(order);

        } catch (SQLException e) {
            try {
                DataSourceUtils.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                DataSourceUtils.commitAndRelease();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
//
    public void updateOrderAdrr(Order order) {
        OrderDao dao = new OrderDao();
        try {
            dao.updateOrderAdrr(order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderState(String r6_order) {
        OrderDao dao = new OrderDao();
        try {
            dao.updateOrderState(r6_order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //获得指定用户的订单集合
    public List<Order> findAllOrders(String uid) {
        OrderDao dao = new OrderDao();
        List<Order> orderList= null;
        try {
            orderList = dao.findAllOrders(uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Map<String, Object>> findAllOrderItemByOid(String oid) {
        OrderDao dao = new OrderDao();
        List<Map<String, Object>> mapList= null;
        try {
            mapList = dao.findAllOrderItemByOid(oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapList;
    }
}
