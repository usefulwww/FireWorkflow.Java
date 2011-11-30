<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="org.fireflow.engine.*"%>
<%@ page import="org.fireflow.example.leaveapplication.workflowextension.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MyWorkItems</title>
</head>
<body>
<script>
				<!--//
				function doQuery(){
					var theForm = document.getElementById('mainForm');
					var actionName = document.getElementById('actionName');
					actionName.value="doQueryMyToDoWorkItems";
					theForm.submit();
				}

				function claimWorkItem(){
					var theForm = document.getElementById('mainForm');
					var actionName = document.getElementById('actionName');
					actionName.value="claimWorkItem";
					theForm.submit();					
				}

				function openForm(){
					var theForm = document.getElementById('mainForm');
					var actionName = document.getElementById('actionName');
					actionName.value="openForm";
					theForm.submit();		
				}
				
				function doSelectMe(id){
					var theForm = document.getElementById('mainForm');
					var theSelectedWorkItemId = document.getElementById('selectedWorkItemId');

					var allRadioList = theForm.elements;
					for (var i=0;i<allRadioList.length;i++){
						var item = allRadioList[i];
						if (item.type=="radio" ){
							if (item.id!=id){
								item.checked = false;
							}else{
								theSelectedWorkItemId.value = item.value;
								item.checked=true;
							}
						}
					}
				}
				//-->
				</script>
<form id="mainForm"
	action="<%=request.getContextPath()%>/myWorkItemServlet"
	method="post">
	<input type="hidden" id="actionName"	name="actionName" value="" /> 
	
	<input type="hidden"
	id="selectedWorkItemId" name="selectedWorkItemId" value="" />

<div align="center">
	<input type="button" value="查询" onclick="doQuery();" /> |
	<input type="button" value="签收选定的记录"	onclick="claimWorkItem();" /> | 
	<input type="button" value="处理选定的记录"	onclick="openForm();"></div>
<br />

<div align="center">

<table width="100%" border="1" cellspacing="0">
	<tr>
		<td>流程实例 Id</td>
		<td>业务信息</td>
		<td>当前环节名称</td>
		<td>当前状态</td>
		<td>选择</td>
	</tr>
	<%List myTodoWorkItems = (List) request
					.getAttribute("MY_TODO_WORKITEMS");
			for (int i = 0; myTodoWorkItems != null
					&& i < myTodoWorkItems.size(); i++) {
				IWorkItem workItem = (IWorkItem) myTodoWorkItems.get(i);%>
	<tr>
		<td><%=workItem.getTaskInstance().getProcessInstanceId()%></td>
		<td><%=((ITaskInstanceExtension)workItem.getTaskInstance()).getBizInfo() %></td>
		<td><%=workItem.getTaskInstance().getDisplayName()%></td>
		<td><%=workItem.getState() == 0 ? "待签收" : "-待-处-理-"%></td>
		<td><input id="radio<%=i%>" type="radio"
			value="<%=workItem.getId()%>" onclick="doSelectMe('radio<%=i%>')" />
		</td>
	</tr>
	<%}%>
</table>

</div>
</form>
<br/>
<br/>
<div style="color:blue;font-size:10pt">
操作提示:<br/><br/>
首先点击“查询”按钮，查询出所有的待办WorkItem。<br/><br/>
然后选择一条“待签收”记录，点击“签收选定的记录”做签收操作<br/><br/>
选择一条“-待-处-理-”，点击“处理选定的记录”可以进入业务对应的操作界面进行数据录入。<br/><br/>

</div>
</body>
</html>