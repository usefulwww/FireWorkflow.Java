package org.fireflow.mainframe.mbeans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.navmenu.NavigationMenuItem;
import org.fireflow.security.persistence.User;
import org.fireflow.security.util.SecurityUtilities;

/**
 * 构建系统主菜单
 * 
 * @author app
 * 
 */
public class MainFrameBean {
	public static final String CONTENT_PANE = "CONTENT_PANE";
	public static String CONTEXT_PATH = null;

	public MainFrameBean() {
		if (CONTEXT_PATH == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			CONTEXT_PATH = facesContext.getExternalContext()
					.getRequestContextPath();
		}

	}

	public List getMainMenuItems() {
		List mainMenuItems = new ArrayList();

		NavigationMenuItem menuItem_dayly = getMenuNaviagtionItem("日常工作", null);
		NavigationMenuItem menuItem_todo = getMenuNaviagtionItem("待办工单",
				"/org/fireflow/example/workflowextension/MyWorkItem.faces");
		
		NavigationMenuItem menuItem_havedone = getMenuNaviagtionItem("已办工单",
		"/org/fireflow/example/workflowextension/MyHaveDoneWorkItem.faces");	
		
		NavigationMenuItem menuItem_todo_bizquery = getMenuNaviagtionItem("复杂条件查询",
		"/org/fireflow/example/workflowextension/MyTodoWorkItemsByComplexCondition.faces");
		
		
		menuItem_dayly.add(menuItem_todo);
		menuItem_dayly.add(menuItem_havedone);
		menuItem_dayly.add(menuItem_todo_bizquery);
		mainMenuItems.add(menuItem_dayly);
		
		
		//商场送货业务
		NavigationMenuItem menuItem_goodsdeliver = getMenuNaviagtionItem("商场送货业务", null);
		NavigationMenuItem menuItem_cache = getMenuNaviagtionItem("收银",
		"/org/fireflow/example/goods_deliver_process/Payment.faces");
		menuItem_goodsdeliver.add(menuItem_cache);
		mainMenuItems.add(menuItem_goodsdeliver);
		
		//银行贷款业务
		NavigationMenuItem menuItem_loan = getMenuNaviagtionItem("银行贷款业务", null);
		NavigationMenuItem menuItem_apply = getMenuNaviagtionItem("贷款申请",
		"/org/fireflow/example/loan_process/SubmitApplicationInfo.faces");
		menuItem_loan.add(menuItem_apply);
		mainMenuItems.add(menuItem_loan);		
		
		//流程后台管理
		NavigationMenuItem menuItem_workflowmanagement = getMenuNaviagtionItem("工作流后台管理", null);
		
		NavigationMenuItem menuItem_uploaddefinition = getMenuNaviagtionItem("上载流程定义文件到数据库",
		"/org/fireflow/workflowmanagement/UploadWorkflowDefinition.faces");
		menuItem_workflowmanagement.add(menuItem_uploaddefinition);
		
		NavigationMenuItem menuItem_definitionManagement = getMenuNaviagtionItem("流程定义管理",
		"/org/fireflow/workflowmanagement/WorkflowDefinitionManagement.faces");
		menuItem_workflowmanagement.add(menuItem_definitionManagement);
		
		
		NavigationMenuItem menuItem_procInst = getMenuNaviagtionItem("流程实例管理",
		"/org/fireflow/workflowmanagement/instances_data_viewer/InstancesDataViewer.faces");
		menuItem_workflowmanagement.add(menuItem_procInst);
		
		mainMenuItems.add(menuItem_workflowmanagement);	
		
		//用户和权限
		NavigationMenuItem menuItem_security = getMenuNaviagtionItem("用户和权限", null);
		NavigationMenuItem menuItem_user = getMenuNaviagtionItem("用户管理",
		"/org/fireflow/security/user/UserManagement.faces");
		menuItem_security.add(menuItem_user);		
		
		NavigationMenuItem menuItem_role = getMenuNaviagtionItem("角色管理",
		"/org/fireflow/security/RoleManagement.faces");
		menuItem_security.add(menuItem_role);
		mainMenuItems.add(menuItem_security);			

		return mainMenuItems;
	}

	private static NavigationMenuItem getMenuNaviagtionItem(String label,
			String action) {
		NavigationMenuItem item = new NavigationMenuItem();
		item.setLabel(label);
		item.setValue(label);
		if (action!=null && !action.trim().equals("")){
			StringBuffer actionStr = new StringBuffer("javascript:window.open(\"")
					.append(CONTEXT_PATH);
			if (!action.startsWith("/")){
				actionStr.append("/");
			}
			actionStr.append(action)
					.append("?timestamp=\"+(new Date()).getTime()")
					.append(",\"")
					.append(CONTENT_PANE)
					.append("\");");

			item.setAction(actionStr.toString());
		}
		return item;
	}

	public String getContentPaneName() {
		return CONTENT_PANE;
	}
	
	public User getCurrentUser(){
		return SecurityUtilities.getCurrentUser();
	}
}
