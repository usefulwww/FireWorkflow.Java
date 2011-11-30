/*$Id: HeaderBean.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.fireflow.security.persistence.User;
import org.fireflow.security.util.SecurityUtilities;
import org.fireflow.util.ILocalStringsKey;
import org.fireflow.util.TagPanelUtil;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.BeforeRender;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

/**
 * 页脚页面的托管Bean
 * @author chenhongxin
 */
@ManagedBean(scope=ManagedBeanScope.SESSION)
public class HeaderBean extends BaseBean {
	
	/**
	 * 绑定欢迎标语Text域的值
	 */
	@Bind(id="welcome", attribute="value")
	private String welcome;
	
	/**
	 * 绑定header的js脚本回调
	 */
	@Bind(id="headerScripter", attribute="script")
	private String headerScripter;
	
	@BeforeRender
	void beforeRender(boolean isPostback) {
		if(!isPostback) {
			headerScripter = "";//因为该Bean的Scope是Session的，所以要清空上次页面交互留下的脚本信息
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> session = context.getExternalContext().getSessionMap();
			User currentUser = SecurityUtilities.getCurrentUser();
			//User user = (User)session.get(IUserConstants.USER_BEAN);
			String label = getMessages().get(ILocalStringsKey.WELCOME_LABEL);
			if(label != null) {//替换占位符，将登陆用户的名称载如标语
				welcome = String.format(label, currentUser.getName());
			}
		}
	}
	
	/**
	 * 绑定注销按钮的点击事件
	 */
	@Action(id="logout")
	public void logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> session = context.getExternalContext().getSessionMap();
		//session.put(IUserConstants.USER_BEAN, null);//清空Session的USER_BEAN记录
		headerScripter = "location.href='"+TagPanelUtil.getBasePath()+"login.jsp'";//使用js脚本回调执行页面跳转
	}
}
