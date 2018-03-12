<%--
  Created by IntelliJ IDEA.
  User: weixi
  Date: 2018/3/7
  Time: 10:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%
        response.sendRedirect(request.getContextPath()+"/product?method=index");
    %>
</body>
</html>
