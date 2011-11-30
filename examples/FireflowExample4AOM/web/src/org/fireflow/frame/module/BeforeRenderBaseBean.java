/*$Id: BeforeRenderBaseBean.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module;

import org.operamasks.faces.annotation.BeforeRender;

/**
 * 供没有使用BeforeRender的托管Bean继承，无需显示去调用
 * 统一资源串的初始化方法。如果本身有实现BeforeRender的
 * 托管Bean不应继承，否则会造成两次BeforeRender的执行。
 * @author chenhongxin
 */
public class BeforeRenderBaseBean extends BaseBean {
	
	/**
	 * 在BeforeRender时执行统一资源串的初始化调用
	 * @param isPostBack 是postBack否是回调
	 */
	@BeforeRender
	protected void beforeRender(boolean isPostBack) {
		initBaseResource();
	}
}
