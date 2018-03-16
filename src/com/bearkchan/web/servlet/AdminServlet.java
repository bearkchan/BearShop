package com.bearkchan.web.servlet;

import com.bearkchan.domain.Category;
import com.bearkchan.service.AdminService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminServlet",urlPatterns = "/admin")
public class AdminServlet extends BaseServlet {
    public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    //    提供一个List<Category>转成json字符串
        AdminService service = new AdminService();
        List<Category> categoryList = service.findAllCategory();

        Gson gson = new Gson();
        String json = gson.toJson(categoryList);
        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().write(json);
    }


}
