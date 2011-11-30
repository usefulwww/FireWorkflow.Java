<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>   
<%@ page import="org.fireflow.example.leaveapplication.data.*" %> 
<%@ page import="org.fireflow.example.ou.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%
WebApplicationContext ctx = WebApplicationContextUtils
		.getWebApplicationContext(getServletContext());
EmailMockDAO emailDAO = (EmailMockDAO)ctx.getBean("EmailMockDAO");
User currentUser = (User) session.getAttribute("CURRENT_USER");
List emailList = emailDAO.findByUserId(currentUser.getId());
SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<div align="center" style="font-weight:bold">我的邮件</div>
<table width="100%" border="1" cellspacing="0">
<tr>
	<td width="25%">时间</td>
	<td>内容</td>
</tr>
<% for (int i=0; emailList!=null && i<emailList.size();i++){
	EmailMock email = (EmailMock)emailList.get(i);
%>
	<tr>
	<td width="25%"><%=dFormat.format(email.getCreateTime()) %></td>
	<td><%=email.getContent() %></td>	
	</tr>
<%} %>
</table>
</body>
</html>