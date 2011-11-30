<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="org.fireflow.example.leaveapplication.data.*" %>
<%@ page import="org.fireflow.example.ou.*" %>
<%@ page import="org.fireflow.engine.*" %>
<%
	List approvalInfoList = (List)request.getAttribute("APPROVAL_INFO_LIST");
	LeaveApplicationInfo leaveApplicationInfo = (LeaveApplicationInfo)request.getAttribute("LEAVE_APPLICATION");
	if (leaveApplicationInfo==null) leaveApplicationInfo = new LeaveApplicationInfo();
	String sn = (String)request.getAttribute("SN");
	
	IWorkItem wi = (IWorkItem) request.getAttribute("CURRENT_WORKITEM");
	
	User currentUser = (User) session.getAttribute("CURRENT_USER");	
	
	Boolean haveDecided = Boolean.FALSE;
	LeaveApprovalInfo myLeaveApprovalInfo = null;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script>
<!--//

function completeWorkItem(){
	var theForm = document.getElementById('mainForm');
	var actionName = document.getElementById('actionName');
	actionName.value="completeWorkItem";
	theForm.action="<%=request.getContextPath()%>/myWorkItemServlet";
	theForm.submit();					
}

//-->
</script>
</head>
<body>
<div align="center" style="font-weight: bold">请假单</div>

<table align="center" width="100%">
	<tr>
		<td>请假类型：</td>
		<td><input name="leaveReason" value="<%=leaveApplicationInfo.getLeaveReason() %>" readonly="true"/></td>
	</tr>
	<tr>
		<td>起止时间：</td>
		<td>从<input name="fromDate" type="text" value="<%=leaveApplicationInfo.getFromDate() %>" readonly="true"/>
			至<input name="toDate"	type="text" value="<%=leaveApplicationInfo.getToDate() %>" readonly="true"/>，
			共<input name="leaveDays" type="text" value="<%=leaveApplicationInfo.getLeaveDays() %>" readonly="true"/>天</td>
	</tr>
	<tr>
		<td>请假人：<%=leaveApplicationInfo.getApplicantName() %></td>
		<td>申请时间：<%=leaveApplicationInfo.getSubmitTime() %></td>
	</tr>
</table>
<br/>
<table  align="center" width="100%">
	<tr>
		<td colspan="3" style="font-weight:bold">审批历史</td>
	</tr>
	<tr>
		<td>审批人</td>
		<td>审批意见</td>
		<td>审批时间</td>
	</tr>
	<%
		for (int i=0;approvalInfoList!=null && i<approvalInfoList.size();i++){
			LeaveApprovalInfo leaveApprovalInfo = (LeaveApprovalInfo)approvalInfoList.get(i);
			if (leaveApprovalInfo.getApprover()!=null && currentUser.getId().equals(leaveApprovalInfo.getApprover())){
				haveDecided = Boolean.TRUE;
				myLeaveApprovalInfo = leaveApprovalInfo;
			}
	%>
	<tr>
		<td><%=leaveApprovalInfo.getApprover() %></td>
		<td><%=leaveApprovalInfo.getApprovalFlag()?"同意请假":"不同意请假" %></td>
		<td><%=leaveApprovalInfo.getApprovalTime() %></td>
	</tr>
	<%
		}
	%>
</table>
<br>
<form action="<%=request.getContextPath()%>/approveApplicationServlet"
	method="post" id="mainForm">
<input type="hidden" name="sn" value="<%=sn %>"/>
<input type="hidden" name="selectedWorkItemId" value="<%=wi.getId()%>"/>
<input type="hidden" id="actionName"	name="actionName" value="" /> 
<input type="hidden" id="currentProcessInstanceId"	name="currentProcessInstanceId" value="<%=wi.getTaskInstance().getProcessInstanceId()%>" /> 
<table  align="center" width="100%">
	<tr>
		<td style="font-weight:bold"> 我的审批意见</td>
	</tr>
	<tr>
		<td><input type="radio" name="decision" value="1" 
			<%=haveDecided?"disabled":"" %>  
			<%if (myLeaveApprovalInfo!=null && myLeaveApprovalInfo.getApprovalFlag()){ %>checked<%} %>>同意
			<br/>
		<input type="radio" name="decision" value="0" 
			<%=haveDecided?"disabled":"" %> 
			<%if (myLeaveApprovalInfo!=null && !myLeaveApprovalInfo.getApprovalFlag()){ %>checked<%} %> >不同意
		</td>
	</tr>
	<tr>
		<td><input type="submit" value="保存" style="display:<%=haveDecided?"none":"block" %>"/>
		&nbsp;&nbsp;&nbsp;
		<input type="button" value="结束工单" onclick="completeWorkItem()"/></td>
	</tr>
</table>
</form>
</body>
</html>