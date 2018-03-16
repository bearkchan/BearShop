package com.bearkchan.web.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.bearkchan.domain.Category;
import com.bearkchan.domain.Product;
import com.bearkchan.service.AdminService;
import com.bearkchan.utils.BeanFactory;
import com.bearkchan.utils.CommonUtils;

public class AdminAddProductServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //目的：收集表单的数据 封装一个Product实体 将上传图片存到服务器磁盘上

        Product product = new Product();

        //收集数据的容器
        Map<String,Object> map = new HashMap<String,Object>();

        try {
            //创建磁盘文件项工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //创建文件上传核心对象
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解析request获得文件项对象集合

            List<FileItem> parseRequest = upload.parseRequest(request);
            for(FileItem item : parseRequest){
                //判断是否是普通表单项
                boolean formField = item.isFormField();
                if(formField){
                    //普通表单项 获得表单的数据 封装到Product实体中
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");

                    map.put(fieldName, fieldValue);

                }else{
                    //文件上传项 获得文件名称 获得文件的内容
                    String fileName = item.getName();
                    String path = this.getServletContext().getRealPath("upload");
                    InputStream in = item.getInputStream();
                    OutputStream out = new FileOutputStream(path+"/"+fileName);//I:/xxx/xx/xxx/xxx.jpg
                    IOUtils.copy(in, out);
                    in.close();
                    out.close();
                    item.delete();

                    map.put("pimage", "upload/"+fileName);

                }

            }

            BeanUtils.populate(product, map);
            //是否product对象封装数据完全
            //private String pid;
            product.setPid(CommonUtils.getUUID());
            //private Date pdate;
            product.setPdate(new Date());
            //private int pflag;
            product.setPflag(0);
            //private Category category;
            Category category = new Category();
            category.setCid(map.get("cid").toString());
            product.setCategory(category);

            //将product传递给service层
            AdminService service = (AdminService) BeanFactory.getBean("adminService");
            service.saveProduct(product);





        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
