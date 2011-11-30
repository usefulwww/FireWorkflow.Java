/*$Id: HelpBean.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

/**
 * 帮助页面的托管Bean
 * @author chenhongxin
 */
@ManagedBean(name="mdl.helpBean", scope=ManagedBeanScope.REQUEST)
public class HelpBean extends BeforeRenderBaseBean {

}
