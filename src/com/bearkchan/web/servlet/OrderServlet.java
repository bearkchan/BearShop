package com.bearkchan.web.servlet;

import com.bearkchan.domain.*;
import com.bearkchan.service.OrderService;
import com.bearkchan.service.ProductService;
import com.bearkchan.utils.CommonUtils;
import com.bearkchan.utils.PaymentUtil;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@WebServlet(name = "OrderServlet", urlPatterns = "/order")
public class OrderServlet extends BaseServlet {


    //    提交订单
    public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
//        判断用户是否登陆，若果未登录下面代码不执行
        User user = (User) session.getAttribute("user");
        if (user == null) {
//            没有登陆
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;//跳转之后后面的代码不再执行。
        }


//        封装好一个Order对象，传递给service层

        Order order = new Order();

//        1. private String oid;//该订单的订单号

        String oid = CommonUtils.getUUID();
        order.setOid(oid);
//        2. private Date ordertime;//下单时间
        order.setOrdertime(new Date());
//        3. private double total;//该订单的总金额
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            double total = cart.getTotal();
            order.setTotal(total);

//        4. private int state;//订单的支付状态1代表已付款，0代表未付款
            order.setState(0);
//        5. private String addr;//收货地址
            order.setAddr(null);
//        6. private String name;//收货人
            order.setName(null);

//        7. private String telephone;//收货人电话
            order.setTelephone(null);

//        8. private User user;//该订单属于哪个用户
            order.setUser(user);
//
//
//        // 该订单中有多少订单项
//        9.List<OrderItem> orderItems = new ArrayList<OrderItem>();

//            获得购物车中购物项的集合map
            Map<String, CartItem> cartItems = cart.getCartItems();
            for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {

//                取出每一个购物项
                CartItem cartItem = entry.getValue();
                OrderItem orderItem = new OrderItem();
                orderItem.setItemid(CommonUtils.getUUID());
                orderItem.setCount(cartItem.getBuyNum());
                orderItem.setSubtotal(cartItem.getSubtotal());
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setOrder(order);

//                将该订单项添加到订单的订单集合中
                order.getOrderItems().add(orderItem);
            }


//            Order对象封装完毕


//            转递数据到service层
            OrderService service = new OrderService();
            service.submitOrder(order);

            session.setAttribute("order", order);
//            页面跳转
            response.sendRedirect(request.getContextPath() + "/order_info.jsp");
        }
    }

    //    更新收货人信息+在线支付
    public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String[]> properties = request.getParameterMap();

        Order order = new Order();
        try {
            BeanUtils.populate(order, properties);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        OrderService service = new OrderService();
        service.updateOrderAdrr(order);


        //    2.选择银行
        //只接入一个接口，这个接口已经集成所有的银行接口了  ，这个接口是第三方支付平台提供的
        //接入的是易宝支付
        // 获得 支付必须基本数据
        String orderid = request.getParameter("oid");
        //String money = order.getTotal()+"";//支付金额
        String money = "0.01";//支付金额
        // 银行
        String pd_FrpId = request.getParameter("pd_FrpId");

        // 发给支付公司需要哪些数据
        String p0_Cmd = "Buy";
        String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
        String p2_Order = orderid;
        String p3_Amt = money;
        String p4_Cur = "CNY";
        String p5_Pid = "";
        String p6_Pcat = "";
        String p7_Pdesc = "";
        // 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
        // 第三方支付可以访问网址
        String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
        String p9_SAF = "";
        String pa_MP = "";
        String pr_NeedResponse = "1";
        // 加密hmac 需要密钥
        String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
                "keyValue");
        String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
                p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
                pd_FrpId, pr_NeedResponse, keyValue);


        String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId=" + pd_FrpId +
                "&p0_Cmd=" + p0_Cmd +
                "&p1_MerId=" + p1_MerId +
                "&p2_Order=" + p2_Order +
                "&p3_Amt=" + p3_Amt +
                "&p4_Cur=" + p4_Cur +
                "&p5_Pid=" + p5_Pid +
                "&p6_Pcat=" + p6_Pcat +
                "&p7_Pdesc=" + p7_Pdesc +
                "&p8_Url=" + p8_Url +
                "&p9_SAF=" + p9_SAF +
                "&pa_MP=" + pa_MP +
                "&pr_NeedResponse=" + pr_NeedResponse +
                "&hmac=" + hmac;

        //重定向到第三方支付平台
        response.sendRedirect(url);
    }

    public void callBack(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 获得回调所有数据
        String p1_MerId = request.getParameter("p1_MerId");
        String r0_Cmd = request.getParameter("r0_Cmd");
        String r1_Code = request.getParameter("r1_Code");
        String r2_TrxId = request.getParameter("r2_TrxId");
        String r3_Amt = request.getParameter("r3_Amt");
        String r4_Cur = request.getParameter("r4_Cur");
        String r5_Pid = request.getParameter("r5_Pid");
        String r6_Order = request.getParameter("r6_Order");
        String r7_Uid = request.getParameter("r7_Uid");
        String r8_MP = request.getParameter("r8_MP");
        String r9_BType = request.getParameter("r9_BType");
        String rb_BankId = request.getParameter("rb_BankId");
        String ro_BankOrderId = request.getParameter("ro_BankOrderId");
        String rp_PayDate = request.getParameter("rp_PayDate");
        String rq_CardNo = request.getParameter("rq_CardNo");
        String ru_Trxtime = request.getParameter("ru_Trxtime");
        // 身份校验 --- 判断是不是支付公司通知你
        String hmac = request.getParameter("hmac");
        String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
                "keyValue");

        // 自己对上面数据进行加密 --- 比较支付公司发过来hamc
        boolean isValid = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd,
                r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid,
                r8_MP, r9_BType, keyValue);


        if (isValid) {
            // 响应数据有效
            if (r9_BType.equals("1")) {


                OrderService service = new OrderService();
                service.updateOrderState(r6_Order);
                // 浏览器重定向
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().println("<h1>付款成功！等待商城进一步操作！等待收货...</h1>");
            } else if (r9_BType.equals("2")) {
                // 服务器点对点 --- 支付公司通知你
                System.out.println("付款成功！");
                // 修改订单状态 为已付款
                // 回复支付公司
                response.getWriter().print("success");
            }
        } else {
            // 数据无效
            System.out.println("数据被篡改！");
        }
    }

    public void myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        //判断用户是否登陆，若果未登录下面代码不执行
        User user = (User) session.getAttribute("user");
        if (user == null) {
            //没有登陆
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;//跳转之后后面的代码不再执行。
        }

        OrderService service = new OrderService();

        //查询该用户的所有订单信息（单表的查询orders表）
        //集合中的每一个order对象的数据是不完整的，缺少OrderItems集合
        List<Order> orderList = service.findAllOrders(user.getUid());
        //循环所有的订单，为每个订单填充订单项集合信息
        if (orderList != null) {
            for (Order order :
                    orderList) {
                //获得每一个订单的oid
                String oid = order.getOid();
                //查询该订单的所有订单项----mapList封装的是多个订单项和该订单项中商品的信息
                List<Map<String, Object>> mapList = service.findAllOrderItemByOid(oid);
                //将mapList转换成List<OrderItem> orderItems
                for (Map<String, Object> map : mapList) {
                    try {
                        //从map中取出count subtotal封装到OrderItem中
                        OrderItem item = new OrderItem();

                        BeanUtils.populate(item, map);
                        //从map中取出pimage pname shop_price 封装到Product中
                        Product product = new Product();
                        BeanUtils.populate(product, map);

                        //将product封装到OrderItem
                        item.setProduct(product);
                        //将orderItem封装到order中的OrderItemList中
                        order.getOrderItems().add(item);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
        // orderList封装完整
        request.setAttribute("orderList",orderList);

        request.getRequestDispatcher("/order_list.jsp").forward(request,response);

    }


}
