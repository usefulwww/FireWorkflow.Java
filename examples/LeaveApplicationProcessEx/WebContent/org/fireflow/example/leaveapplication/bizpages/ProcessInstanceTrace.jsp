<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="org.fireflow.engine.*" %>
<%@ page import="org.fireflow.example.leaveapplication.workflowextension.*" %>
<%
	List taskInstanceList = (List)request.getAttribute("TASKINSTANCE_LIST");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action="<%=request.getContextPath()%>/processInstanceTraceServlet" method="post">
<div>
<select name="applicant">
	<option value="张三">张三</option>
	<option value="李四">李四</option>
	<option value="王五">王五</option>
	<option value="赵六">赵六</option>
	<option value="部门经理1">部门经理1</option>
	<option value="部门经理2">部门经理2</option>
</select>
<input type="submit" value="查询"/>
</div>
</form>
<table width="100%" border="1" cellspacing="0">
	<tr>
		<td>流程实例 Id</td>
		<td>任务名称</td>
		<td>申请人</td>
		<td>请假时段</td>
		<td>请假天数</td>
		<td>审批意见</td>
		<td>状态</td>
	</tr>
	<%for (int i=0;taskInstanceList!=null && i<taskInstanceList.size();i++){
		LeaveApplicationTaskInstanceExtension taskInstance = (LeaveApplicationTaskInstanceExtension)taskInstanceList.get(i);
		String spyj = "/";
		String state = "";
		if (taskInstance.getTaskId().equals("LeaveApplicationProcess.DepartmentManager_Approve_Activity.Approval_Task")
				&& taskInstance.getState()==ITaskInstance.COMPLETED){
			if (taskInstance.getApprovalFlag()){
				spyj="同意";
			}
			else{
				spyj="不同意";
			}
		}
		if (taskInstance.getState()<5){
			state="办理中";
		}else{
			state="已结束";
		}
	%>
	<tr style="color:<%=(taskInstance.getState()<5)?"green":"blue" %>">
		<td><%=taskInstance.getProcessInstanceId() %></td>
		<td><%=taskInstance.getDisplayName() %></td>
		<td><%=taskInstance.getApplicant() %></td>
		<td>从<%=taskInstance.getFromDate() %>至<%=taskInstance.getToDate() %></td>
		<td><%=taskInstance.getLeaveDays() %></td>
		<td><%=spyj %></td>	
		<td><%=state %></td>
	</tr>
	<%} %>
</table>
</body>
</html>