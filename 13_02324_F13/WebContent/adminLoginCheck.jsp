<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title></title>
</head>
<body>
<%
if(session.getAttribute("loginId")==null)
{
	response.sendRedirect("login.jsp");
}
else if(!session.getAttribute("loginId").toString().equals("10"))
{
	response.sendRedirect("menu.jsp");
}
%>
</body>
</html>