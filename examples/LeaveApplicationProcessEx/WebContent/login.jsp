<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String loginErr = (String)request.getAttribute("LOGIN_ERROR");

%>
<html>
	<head>
	</head>
	<body>

		<div align="center">
			<table>
				<tr>
					<td>
						<span align="center"><img
								src="<%=request.getContextPath() %>/org/fireflow/example/resources/images/logo.gif" /> </span>
					</td>
					<td>
						<span
							style="font-size: 24px; font-weight: bold; color: blue; margin-bottom: 10px">Example</span>
					</td>
				</tr>
			</table>

			<br />
			<form action="<%=request.getContextPath() %>/loginServlet" method="post">
				<table>
					<tr>
						<td colspan="2" style="color: red">
							<%=loginErr==null?"":loginErr%>
						</td>
					</tr>
					<tr>
						<td>
							用户名:
						</td>
						<td>
							<input type="text" name="userId" />
						</td>
					</tr>
					<tr>
						<td>
							密码:
						</td>
						<td>
							<input type="password" name="userPwd" />
						</td>
					</tr>
					<tr>
						<td colspan="2" align="right">
							<input type="submit"/> 
						</td>
					</tr>
				</table>
			</form>
			<hr/>
			<div align="center"><img src="LeaveApplicationProcessEx.PNG"/></div>
			
			<table>
				<tr>
					<td align="center">
						<table cellspacing="0" cellpadding="4" border="1">
							<tr>
								<td colspan="4" style="font-weight: bold">
									测试用户列表
								</td>
							</tr>
							<tr>
								<td>
									角色名称
								</td>
								<td>
									用户名
								</td>
								<td>
									密码
								</td>
								<td>
									中文名
								</td>
							</tr>
							<tr>
								<td rowspan="3">
									部门1
								</td>
								<td>
									Zhang
								</td>
								<td>
									123456
								</td>
								<td>
									张三
								</td>
							</tr>
							<tr>

								<td>
									Li
								</td>
								<td>
									123456
								</td>
								<td>
									李四
								</td>
							</tr>
							<tr>

								<td>
									DeptManager1
								</td>
								<td>
									123456
								</td>
								<td>
									部门经理1
								</td>
							</tr>
							<tr>
								<td rowspan="3">
									部门2
								</td>
								<td>
									Wang
								</td>
								<td>
									123456
								</td>
								<td>
									王五
								</td>
							</tr>
							<tr>

								<td>
									Zhao
								</td>
								<td>
									123456
								</td>
								<td>
									赵六
								</td>
							</tr>
							<tr>

								<td>
									DeptManager2
								</td>
								<td>
									123456
								</td>
								<td>
									部门经理2
								</td>
							</tr>							
							<tr>
								<td rowspan="3">
									人力资源部
								</td>
								<td>
									hr1
								</td>
								<td>
									123456
								</td>
								<td>
									人力资源部职员1
								</td>
							</tr>
							<tr>

								<td>
									hr2
								</td>
								<td>
									123456
								</td>
								<td>
									人力资源部职员2
								</td>
							</tr>
							<tr>

								<td>
									deptManagerHR
								</td>
								<td>
									123456
								</td>
								<td>
									人力资源部张经理
								</td>
							</tr>
							<tr>
								<td>
									公司经理
								</td>
								<td>
									ManagerChen
								</td>
								<td>
									123456
								</td>
								<td>
									陈总经理
								</td>
							</tr>
						</table>
						<br/>


					</td>
				</tr>
			</table>
		</div>
	</body>
</html>