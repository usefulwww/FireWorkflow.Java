/*$Id: FooterBean.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module;

import org.fireflow.util.ILocalStringsKey;
import org.operamasks.faces.annotation.BeforeRender;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

/**
 * 页脚页面的托管Bean
 * @author chenhongxin
 */
@ManagedBean(scope=ManagedBeanScope.SESSION)
public class FooterBean extends BaseBean {
	
	/**
	 * 绑定帮助链接的显示值
	 */
	@Bind(id="footerHelp", attribute="value")
	private String footerHelp;
	
	/**
	 * 绑定使用条款链接的显示值
	 */
	@Bind(id="footerTermsOfUse", attribute="value")
	private String footerTermsOfUse;
	
	/**
	 * 绑定法律申明链接的显示值
	 */
	@Bind(id="footerDeclareOfLaw", attribute="value")
	private String footerDeclareOfLaw;

	
	@BeforeRender
	void beforeRender(boolean isPostback) {
		if(!isPostback) {//执行链接显示值的国际化初始化
			footerHelp = getMessages().get(ILocalStringsKey.HELP_LABEL);
			footerTermsOfUse = getMessages().get(ILocalStringsKey.TERMS_OF_USE_LABEL);
			footerDeclareOfLaw = getMessages().get(ILocalStringsKey.DECLARE_OF_LAW_LABEL);
		}
	}
}
