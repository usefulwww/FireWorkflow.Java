<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.example.ou.User"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";

	User user = (User) session.getAttribute("CURRENT_USER");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>填写请假单</title>
</head>
<body>
<form action="<%=request.getContextPath() %>/submitApplicationServlet" method="post">
<div align="center" style="font-weight: bold">请假单</div>
<table align="center">
	<tr>
		<td>请假类型：</td>
		<td><select name="leaveReason">
			<option value="事假">事假</option>
			<option value="病假">病假</option>
			<option value="带薪假">带薪假</option>
			<option value="调休">调休</option>
		</select></td>
	</tr>
	<tr>
		<td>起止时间：</td>
		<td>从<input name="fromDate" type="text" />至<input name="toDate" type="text" />，共<input name="leaveDays"
			type="text" />天</td>
	</tr>
	<tr>
		<td>请假人：</td>
		<td><%=user == null ? "" : user.getName()%></td>
	</tr>
</table>
<div align="center"><input type="submit" value="保存"/></div>

<div id="msg" align="center" >
<%
String infoMsg = (String)request.getAttribute("INFO");

String errMsg = (String)request.getAttribute("ERR");
%>
<span id="info" style="color:blue"><%=infoMsg==null?"":infoMsg %></span>
<span id="err" style="color:red"><%=errMsg==null?"":errMsg %></span>
</div>
</form>
</body>
</html>