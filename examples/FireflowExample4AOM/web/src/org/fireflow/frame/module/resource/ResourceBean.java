/*$Id: ResourceBean.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module.resource;

import java.util.Map;

import org.operamasks.faces.annotation.BeforeRender;
import org.operamasks.faces.annotation.LocalString;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

/**
 * 国际化资源Bean
 * @author chenhongxin
 */
@ManagedBean(name="resourceBean", scope=ManagedBeanScope.REQUEST)
public class ResourceBean {
	@LocalString
	private Map<String, String> messages;
	
	public Map<String, String> getMessages() {
		return messages;
	}
	
	@BeforeRender
	void beforeRender(boolean isPostback) {
		if(!isPostback) {
		}
	}
}
