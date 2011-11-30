/*$Id: IndexBean.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;

import org.fireflow.util.ILocalStringsKey;
import org.fireflow.util.TagPanelUtil;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.ActionListener;
import org.operamasks.faces.annotation.BeforeRender;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.widget.UIButton;
import org.operamasks.faces.component.widget.UISeparator;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.component.widget.menu.UICommandMenuItem;
import org.operamasks.faces.component.widget.menu.UIMenu;


/**
 * 主页面的托管Bean
 * @author chenhongxin
 */
@ManagedBean(scope = ManagedBeanScope.SESSION)
public class IndexBean extends BaseBean {
	private static final String HELP_PAGE_URL = "help.faces";
	private static final String HELP_ICON_URL = "../resources/images/help.png";
	private static final String REFRESH_ICOM_URL = "../resources/images/refresh.png";
	
	/**
	 * 注入服务提供Bean，该Bean提供各种的业务操作对象
	 */
	//@ManagedProperty("#{serviceBean}")
	//private ServiceBean service;

	/**
	 * 绑定页面上的ToolBar组件
	 */
	@Bind(id = "toolBar", attribute = "binding")
	private UIToolBar toolBar;

	/**
	 * 绑定菜单项的Updater
	 */
	@Bind(id = "menuUpdater", attribute = "binding")
	private AjaxUpdater menuUpdater;
	
	/**
	 * 绑定页面的js脚本回调
	 */
	@Bind(id="scripter", attribute="script")
	private String scripter;

	@BeforeRender
	public void beforeRender(boolean isPostBack) {
		initBaseResource();
		if (!isPostBack) {
			/*ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, Object> session = eContext.getSessionMap();
			User user = (User)session.get(IUserConstants.USER_BEAN);
			Set<String> validateItems = getValidateModuleItemIdSet(user.getRole());
			
			FacesContext context = FacesContext.getCurrentInstance();
			Application application = context.getApplication();
			List<Module> modules = service.getModuleService().listModule();
			if (modules != null) {
				for (Module module : modules) {
					//创建菜单
					UIMenu menu = (UIMenu)application.createComponent(UIMenu.COMPONENT_TYPE);
					menu.setLabel(module.getName());
					menu.setImage(module.getIcon());

					Set<ModuleItem> items = module.getModuleItems();
					if (items != null) {
						for (ModuleItem item : items) {
							//创建菜单下拉项
							UICommandMenuItem uiItem = (UICommandMenuItem)application.createComponent(UICommandMenuItem.COMPONENT_TYPE);
							uiItem.setLabel(item.getName());
							uiItem.setImage(Util.getBasePath() + item.getIcon());
							uiItem.setValue(item.getUrl());
							//添加事件监听
							uiItem.addActionListener(Util.createMethodExpressionActionListener(context, "#{mdl.indexBean.redirect}"));
							if(validateItems.contains(item.getId())) {//只有当前用户角色可见的模块项才添加到菜单
								menu.getChildren().add(uiItem);
							}
						}
					}

					toolBar.getChildren().add(menu);
					UISeparator separator = (UISeparator)application.createComponent(UISeparator.COMPONENT_TYPE);
					toolBar.getChildren().add(separator);
				}
				
				//创建帮助按钮
				UIButton helpBtn = (UIButton)application.createComponent(UIButton.COMPONENT_TYPE);
				helpBtn.setId("help");
				helpBtn.setLabel(getMessages().get(ILocalStringsKey.MENU_HELP_LABEL));
				helpBtn.setImage(HELP_ICON_URL);
				helpBtn.setValue(HELP_PAGE_URL);
				toolBar.getChildren().add(helpBtn);
				
				//创建刷新按钮
				UIButton refreshBtn = (UIButton)application.createComponent(UIButton.COMPONENT_TYPE);
				refreshBtn.setId("refresh");
				refreshBtn.setLabel(getMessages().get(ILocalStringsKey.MENU_REFRESH_LABEL));
				refreshBtn.setImage(REFRESH_ICOM_URL);
				toolBar.getChildren().add(refreshBtn);*/
			FacesContext context = FacesContext.getCurrentInstance();
			Application application = context.getApplication();
			//创建菜单
			UIMenu dailyMemu = createMenu("日常工作");
			//dailyMemu.getChildren().add(createCommandMenuItem("待办工单", "/org/fireflow/example/workflowextension/MyWorkItem.faces"));
			dailyMemu.getChildren().add(createCommandMenuItem("待办工单", "/org/fireflow/example/workflowextension/WorkItemList.faces"));
			dailyMemu.getChildren().add(createCommandMenuItem("已办工单", "/org/fireflow/example/workflowextension/MyHaveDoneWorkItem.faces"));
			toolBar.getChildren().add(dailyMemu);
			
			//商场送货业务
			UIMenu goodsdeliverMemu = createMenu("商场送货业务");
			goodsdeliverMemu.getChildren().add(createCommandMenuItem("收银", "/org/fireflow/example/goods_deliver_process/Payment.faces"));
			toolBar.getChildren().add(goodsdeliverMemu);
			
			//银行贷款业务
			UIMenu loanMemu = createMenu("银行贷款业务");
			loanMemu.getChildren().add(createCommandMenuItem("贷款申请", "/org/fireflow/example/loan_process/SubmitApplicationInfo.faces"));
			toolBar.getChildren().add(loanMemu);	
			
			//流程后台管理
			UIMenu wfMemu = createMenu("工作流后台管理");
			wfMemu.getChildren().add(createCommandMenuItem("上载流程定义文件到数据库", "/org/fireflow/workflowmanagement/UploadWorkflowDefinition.faces"));
			wfMemu.getChildren().add(createCommandMenuItem("流程定义管理", "/org/fireflow/workflowmanagement/WorkflowDefinitionManagement.faces"));
			wfMemu.getChildren().add(createCommandMenuItem("流程实例管理", "/org/fireflow/workflowmanagement/instances_data_viewer/InstancesDataViewer.faces"));
			toolBar.getChildren().add(wfMemu);	
			
			
			//用户和权限
			UIMenu securityMemu = createMenu("用户和权限");
			securityMemu.getChildren().add(createCommandMenuItem("用户管理", "/org/fireflow/security/user/UserManagement.faces"));
			securityMemu.getChildren().add(createCommandMenuItem("角色管理", "/org/fireflow/security/RoleManagement.faces"));
			toolBar.getChildren().add(securityMemu);	
			
			UISeparator separator = (UISeparator) application
					.createComponent(UISeparator.COMPONENT_TYPE);
			toolBar.getChildren().add(separator);

			// 创建帮助按钮
			UIButton helpBtn = (UIButton) application
					.createComponent(UIButton.COMPONENT_TYPE);
			helpBtn.setId("help");
			helpBtn.setLabel(getMessages().get(ILocalStringsKey.MENU_HELP_LABEL));
			helpBtn.setImage(HELP_ICON_URL);
			helpBtn.setValue(HELP_PAGE_URL);
			toolBar.getChildren().add(helpBtn);

			// 创建刷新按钮
			UIButton refreshBtn = (UIButton) application
					.createComponent(UIButton.COMPONENT_TYPE);
			refreshBtn.setId("refresh");
			refreshBtn.setLabel(getMessages().get(
					ILocalStringsKey.MENU_REFRESH_LABEL));
			refreshBtn.setImage(REFRESH_ICOM_URL);
			toolBar.getChildren().add(refreshBtn);

			// 使用AjaxUpdater动态刷型区域组件
			menuUpdater.reload();
			
		}
	}
	
	/**
	 * 绑定帮助按钮点击事件
	 */
	@Action(id="help")
	public void help() {
		scripter = formatScript(getMessages().get(ILocalStringsKey.MENU_HELP_LABEL), 
								HELP_PAGE_URL, 
								HELP_ICON_URL);
	}
	
	/**
	 * 绑定刷新当前标签页的按钮点击事件
	 */
	@Action(id="refresh")
	public void refresh() {
		scripter = "refreshTag();";
	}

	/**
	 * 事件监听函数，用于执行菜单按钮点击后的页面切换的脚本回调
	 * @param e ActionEvent
	 */
	@ActionListener
	public void redirect(ActionEvent e) {
		if (e.getComponent() instanceof UICommandMenuItem) {
			UICommandMenuItem item = (UICommandMenuItem) e.getComponent();
			Object obj = item.getValue();
			String url = (obj instanceof String ? (String)obj : obj.toString());
			url = TagPanelUtil.getBasePath() + url;
			scripter = formatScript(item.getLabel(), url, item.getImage());
		}
	}
	
	/**
	 * 格式化调用添加标签页的js回调脚本
	 * @param title 标签页的名称
	 * @param url 标签页内容页面的url
	 * @param icon 标签页的小图标地址
	 * @return 格式化好的js回调脚本字符串
	 */
	private String formatScript(String title, String url, String icon) {
		return String.format("addTab('%s','%s','%s');", title, url, TagPanelUtil.getTabPanelIconCls(icon));
	}
	

	/**
	 * 创建菜单
	 * 
	 * @param name
	 * @return
	 */
	private UIMenu createMenu(String name) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		UIMenu menu = (UIMenu) application
				.createComponent(UIMenu.COMPONENT_TYPE);
		menu.setLabel(name);
		// menu.setImage(null);
		return menu;
	}

	/**
	 * 创建菜单下拉项
	 * 
	 * @return
	 */
	private UICommandMenuItem createCommandMenuItem(String name, String url) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		//
		UICommandMenuItem uiItem = (UICommandMenuItem) application
				.createComponent(UICommandMenuItem.COMPONENT_TYPE);
		uiItem.setLabel(name);
		// uiItem.setImage(null);
		uiItem.setValue(url);
		// 添加事件监听
		uiItem.addActionListener(this.createMethodExpressionActionListener(
				context, "#{IndexBean.redirect}"));
		return uiItem;
	}
	
	/**
	 * 创建指定EL方法表达式的MethodExpressionActionListener
	 * 
	 * @param context
	 *            FacesContext
	 * @param expression
	 *            EL方法表达式
	 * @return MethodExpressionActionListener
	 */
	public MethodExpressionActionListener createMethodExpressionActionListener(
			FacesContext context, String expression) {
		return new MethodExpressionActionListener(context.getApplication()
				.getExpressionFactory().createMethodExpression(
						context.getELContext(), expression, Void.class,
						new Class[] { ActionEvent.class }));
	}
}
