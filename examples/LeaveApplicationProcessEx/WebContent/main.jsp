<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.fireflow.example.ou.User"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";

	User user = (User) session.getAttribute("CURRENT_USER");
%>
<html>

<head>
<style>
<!--
.mainmenu {
	font-size: 14px;
	font-weight: bold;
	color: white;
}

a {
	text-decoration: none;
}

.tableHeader {
	background-image: url(resources/images/grid3-hrow-over.gif);
}

.row1 {
	background-image: url(resources/images/row-over.gif);
}

.row2 {
	
}
-->
</style>
</head>
<body>
<table width="1010" align="center" cellspacing="0">
	<tr>
		<td colspan="2" valign="top">
		<div align="left">
		<table width="100%" border="0" cellpadding="0" cellspacing="0"
			bgcolor="#FFFFFF">
			<!--DWLayoutTable-->
			<tr>

				<td height="19" valign="middle"><!-- logo 245*52 --> <img
					src="<%=request.getContextPath()%>/org/fireflow/example/resources/images/logo.gif"></img>
				</td>

				<td valign="top" align="right" style="color: blue">当前用户:<%=(user == null ? "--" : user.getName())%>
				<br />
				<br />
				<a href="<%=request.getContextPath()%>/login.jsp">重新登陆</a></td>
				<td></td>

			</tr>


		</table>
		</div>
		</td>
	</tr>
	<tr>
		<td colspan="2"><ui:insert name="mainmenu" /></td>
	</tr>
	<tr height="8px">
		<td></td>
	</tr>
	<tr>
		<td valign="top" width="200px">
		<div
			style="width: 220px; height: 430px; border-right: 1px solid silver; padding-left: 10px">
		<table>
			<tr>
				业务菜单
			</tr>
			<tr>
				<td><a href="<%=request.getContextPath() %>/org/fireflow/example/leaveapplication/bizpages/SubmitApplication.jsp"
					target="Workspace">
							|--填写请假单</h:outputLink></td>
			</tr>



			<tr>
				<td>|-- <a
					href="<%=request.getContextPath() %>/org/fireflow/example/leaveapplication/bizpages/MyWorkItem.jsp"
					target="Workspace">我的待办工单</a>
				</td>
			</tr>
			<tr>
				<td>|-- <a
					href="<%=request.getContextPath() %>/org/fireflow/example/leaveapplication/bizpages/MyHaveDoneWorkItem.jsp"
					target="Workspace">我的已办工单</a>
				</td>
			</tr>

			<tr>
				<td>|-- <a
					href="<%=request.getContextPath() %>/org/fireflow/example/leaveapplication/bizpages/MyEmail.jsp"
					target="Workspace">我的邮箱(模拟接受审批结果)</a>
				</td>
			</tr>
			<tr>
				<td>|-- <a
					href="<%=request.getContextPath() %>/org/fireflow/example/leaveapplication/bizpages/ProcessInstanceTrace.jsp"
					target="Workspace">根据姓名查询请假审批情况</a>
				</td>
			</tr>			
			<tr>
				<td height="10px"></td>
			</tr>

		</table>
		</div>
		</td>
		<td valign="top"><iframe id="Workspace" name="Workspace" frameborder="0"
						scrolling="auto" style="width:700px;height:450px"></iframe></td>
	</tr>
</table>
</body>

</html>
