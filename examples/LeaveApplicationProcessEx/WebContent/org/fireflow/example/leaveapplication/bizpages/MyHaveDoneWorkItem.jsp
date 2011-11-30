<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>   
<%@ page import="org.fireflow.example.leaveapplication.data.*" %> 
<%@ page import="org.fireflow.example.ou.*" %>
<%@ page import="org.fireflow.engine.*" %>
<%@ page import="org.fireflow.kernel.*" %>
<%@ page import="org.fireflow.engine.persistence.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>    
<%@ page import="org.fireflow.example.leaveapplication.workflowextension.*"%>
<%
WebApplicationContext ctx = WebApplicationContextUtils
.getWebApplicationContext(getServletContext());

User currentUser = (User) session.getAttribute("CURRENT_USER");
RuntimeContext runtimeContext = (RuntimeContext) ctx.getBean("runtimeContext");

IWorkflowSession wflsession = runtimeContext.getWorkflowSession();

final String actorId = currentUser.getId();
List myHaveDoneWorkItems = null;
try {
	myHaveDoneWorkItems = (List) wflsession	.execute(new IWorkflowSessionCallback() {

		public Object doInWorkflowSession(RuntimeContext arg0)
				throws EngineException, KernelException {
			// TODO Auto-generated method stub
			IPersistenceService persistenceService = arg0
					.getPersistenceService();
			return persistenceService
					.findHaveDoneWorkItems(actorId);
		}

	});
} catch (EngineException e) {
e.printStackTrace();
} catch (KernelException e) {
e.printStackTrace();
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<div align="center" style="font-weight:bold">我的已办工作</div>
<table width="100%" border="1" cellspacing="0">
	<tr>
		<td>流程实例 Id</td>
		<td>业务信息</td>
		<td>当前环节名称</td>
		<td>当前状态</td>
	</tr>
	<%
	String stateStr = "";
	for (int i = 0; myHaveDoneWorkItems != null
					&& i < myHaveDoneWorkItems.size(); i++) {
				IWorkItem workItem = (IWorkItem) myHaveDoneWorkItems.get(i);
				
				if (workItem.getState()==7){
					stateStr="已结束";
				}else if (workItem.getState()==9){
					stateStr="已取消";
				}else{
					stateStr=Integer.toString(workItem.getState());
				}				
				%>

				
	<tr>
		<td><%=workItem.getTaskInstance().getProcessInstanceId()%></td>
		<td><%=((ITaskInstanceExtension)workItem.getTaskInstance()).getBizInfo() %></td>		
		<td><%=workItem.getTaskInstance().getDisplayName()%></td>
		<td><%=stateStr%></td>
	</tr>
	<%}%>
</table>
</body>
</html>