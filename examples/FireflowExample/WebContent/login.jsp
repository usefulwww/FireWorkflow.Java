<%@ page language="java" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<body>

<div id="banner" align="center">
<table width="560">
	<tr>
		<td><span align="center"><img
			src="<%=request.getContextPath() %>/org/fireflow/mainframe/resources/images/logo2.gif" /> </span></td>
		<td><span
			style="font-size: 24px; font-weight: bold; color: blue; margin-bottom: 10px">Examples</span>
		</td>
	</tr>
</table>
</div>
<br />

<div id="loginForm" align="center">
<form action="<%=request.getContextPath() %>/j_spring_security_check" method="post">
<table>
	<tr>
		<td colspan="2" style="color: red"></t:messages></td>
	</tr>
	<tr>
		<td>用户名:</td>
		<td><input name="j_username" value="" /></td>
	</tr>
	<tr>
		<td>密码:</td>
		<td><input type="password" name="j_password" value="" /></td>
	</tr>
	<tr>
		<td colspan="2" align="right"><input type="submit" value="登录" />
		(<a href="manual.pdf" target="_blank">操作手册</a>)</td>
	</tr>
</table>
</form>
</div>
<hr />
<div id="example_list" align="center">
<table>
	<tr>
		<td align="center"><img width="500" height="250"
			src="<%=request.getContextPath() %>/org/fireflow/mainframe/resources/images/TheLoanProcess.gif" /> <br />

		<table cellspacing="0" cellpadding="4" border="1">
			<tr>
				<td colspan="4" style="font-weight: bold">测试用户列表</td>
			</tr>
			<tr>
				<td>角色名称</td>
				<td>用户名</td>
				<td>密码</td>
				<td>中文名</td>
			</tr>
			<tr>
				<td rowspan="2">信贷员</td>
				<td>Loanteller1</td>
				<td>123456</td>
				<td>信贷员1</td>
			</tr>
			<tr>

				<td>Loanteller2</td>
				<td>123456</td>
				<td>信贷员2</td>
			</tr>

			<tr>
				<td rowspan="2">风险审核岗</td>
				<td>RiskEvaluator1</td>
				<td>123456</td>
				<td>风险审核员1</td>
			</tr>
			<tr>

				<td>RiskEvaluator2</td>
				<td>123456</td>
				<td>风险审核员2</td>
			</tr>
			<tr>
				<td rowspan="3">审批岗</td>
				<td>Approver1</td>
				<td>123456</td>
				<td>审批员1</td>
			</tr>
			<tr>

				<td>Approver2</td>
				<td>123456</td>
				<td>审批员2</td>
			</tr>
			<tr>

				<td>Approver3</td>
				<td>123456</td>
				<td>审批员3</td>
			</tr>
			<tr>
				<td>放款操作岗</td>
				<td>LendMoneyOfficer1</td>
				<td>123456</td>
				<td>放款操作员1</td>
			</tr>			
			<tr>
				<td>管理岗</td>
				<td>manager</td>
				<td>123456</td>
				<td>管理员</td>
			</tr>
		</table>
		</td>
		<td align="center"><img
			width="400" height="250"
			src="<%=request.getContextPath() %>/org/fireflow/mainframe/resources/images/TheGoodsDeliverProcess.gif" />

		<br />

		<table cellspacing="0" cellpadding="4" border="1">
			<tr>
				<td colspan="4" style="font-weight: bold">测试用户列表</td>
			</tr>
			<tr>
				<td>角色名称</td>
				<td>用户名</td>
				<td>密码</td>
				<td>中文名</td>
			</tr>
			<tr>
				<td rowspan="2">收银岗</td>
				<td>Cashier1</td>
				<td>123456</td>
				<td>收银员1</td>
			</tr>
			<tr>

				<td>Cashier2</td>
				<td>123456</td>
				<td>收银员2</td>
			</tr>

			<tr>
				<td rowspan="2">仓管岗</td>
				<td>WarehouseKeeper1</td>
				<td>123456</td>
				<td>仓管员1</td>
			</tr>
			<tr>

				<td>WarehouseKeeper2</td>
				<td>123456</td>
				<td>仓管员2</td>
			</tr>
			<tr>
				<td rowspan="3">送货岗</td>
				<td>Deliveryman1</td>
				<td>123456</td>
				<td>送货员1</td>
			</tr>
			<tr>

				<td>Deliveryman2</td>
				<td>123456</td>
				<td>送货员2</td>
			</tr>
			<tr>

				<td>Deliveryman3</td>
				<td>123456</td>
				<td>送货员3</td>
			</tr>
			<tr>
				<td>管理岗</td>
				<td>manager</td>
				<td>123456</td>
				<td>管理员</td>
			</tr>
		</table>

		</td>

	</tr>
</table>

</div>

<br />
<br />
<hr />
<div id="change_logs" align="center">
<table cellspacing="0" cellpadding="4" border="1">
	<tr>
		<td colspan="3" style="font-weight: bold">更新历史</td>
	</tr>

	<tr>
		<td>时间</td>
		<td>作者</td>
		<td>内容</td>
	</tr>
	<tr>
		<td>2009-05-02</td>
		<td>非也</td>
		<td>将Example升级到1.0，并增加了一个“银行贷款审批”流程示例</td>
	</tr>
	<tr>
		<td>2009-02-17</td>
		<td>回电</td>
		<td>将hsqldb集成到Example中，免去运行前的一大堆数据库配置和建表操作</td>
	</tr>
	<tr>
		<td>2009-02-17</td>
		<td>非也</td>
		<td>修改了网友报告的一些bug</td>
	</tr>
	<tr>
		<td>2009-02-08</td>
		<td>非也</td>
		<td>创建并发布</td>
	</tr>


</table>

</div>
</body>
</html>