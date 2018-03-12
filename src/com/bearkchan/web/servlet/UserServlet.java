package com.bearkchan.web.servlet;

import com.bearkchan.domain.User;
import com.bearkchan.service.UserService;
import com.bearkchan.utils.CommonUtils;
import com.bearkchan.utils.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@WebServlet(name = "UserServlet", urlPatterns = "/user")
public class UserServlet extends BaseServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        doGet(request, response);
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
////        获得请求的哪个方法的method
//
//
//
//        String methodName = request.getParameter("method");
//        if ("active".equals(methodName)) {
//            active(request, response);
//        } else if ("checkUsername".equals(methodName)) {
//            checkUsername(request, response);
//        } else if ("register".equals(methodName)) {
//            register(request, response);
//        }
//    }


    //    激活注册用户的功能
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        获得激活码
        String activeCode = request.getParameter("activeCode");
        UserService service = new UserService();
        service.active(activeCode);

//        跳转到登陆页面
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    //    检查用户名是否重复的功能
    public void checkUsername(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        获得用户名
        String username = request.getParameter("username");
        UserService service = new UserService();
        boolean isExist = service.checkUsername(username);
        String json = "{\"isExist\":" + isExist + "}";
        response.getWriter().write(json);
    }

    //    用户注册功能
    public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        获得表单数据
        Map<String, String[]> properties = request.getParameterMap();
        User user = new User();
        try {
//            自己指定一个类型转换器（将String转成Date）
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class aClass, Object o) {
//                    将String转成Date
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date parse = null;
                    try {
                        parse = format.parse(o.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return parse;
                }
            }, Date.class);
//            映射封装
            BeanUtils.populate(user, properties);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

//        private String uid;
        user.setUid(CommonUtils.getUUID());
//        private String telephone;
        user.setTelephone(null);
//        private int state;//是否激活
        user.setState(0);
//        private String code;//激活码
        String activeCode = CommonUtils.getUUID();
        user.setCode(activeCode);

//        将user传递给service层
        UserService service = new UserService();
        boolean isRegisterSuccess = false;
        try {
            isRegisterSuccess = service.regist(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        判断是否注册成功
        if (isRegisterSuccess) {
//            发送激活邮件
            String emailMsg = "恭喜您注册成功，请点击下面的连接进行激活账户" +
                    "<a href='http://localhost:8080/bearshop/user?method=active&activeCode=" + activeCode
                    + "'>http://localhost:8080/bearshop/user?method=active&activeCode=" + activeCode + "</a>";
            try {
                MailUtils.sendMail(user.getEmail(), emailMsg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

//            跳转到注册成功的页面
            response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
        } else {
//            跳转到注册失败的页面
            response.sendRedirect(request.getContextPath() + "/registerFail.jsp");

        }
    }
}
