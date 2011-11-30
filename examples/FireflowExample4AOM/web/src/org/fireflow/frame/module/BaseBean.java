/*$Id: BaseBean.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module;

import java.util.Map;

import org.fireflow.util.ILocalStringsKey;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.LocalString;

/**
 * 供module下托管Bean继承的基础Bean，提供
 * 统一资源的国际化
 * @author chenhongxin
 */
public class BaseBean {
	
	/**
	 * 国际化资源串
	 */
	@LocalString
	private Map<String, String> messages;
	
	/**
	 * 绑定“添加”按钮的显示值
	 */
	@Bind(id = "add", attribute = "label")
	private String add_label;

	/**
	 * 绑定“修改”按钮的显示值
	 */
	@Bind(id = "modify", attribute = "label")
	private String modify_label;

	/**
	 * 绑定“删除”按钮的显示值
	 */
	@Bind(id = "del", attribute = "label")
	private String del_label;
	
	/**
	 * 绑定“确定”按钮的显示值
	 */
	@Bind(id = "ok", attribute = "label")
	private String ok_label;

	/**
	 * 绑定“取消”按钮的显示值
	 */
	@Bind(id = "cancel", attribute = "label")
	private String cancel_label;
	
	/**
	 * 获取国际化资源串
	 * @return 国际化资源串
	 */
	public Map<String, String> getMessages() {
		return messages;
	}
	
	/**
	 * 初始化统一资源的显示值，继承类需显示调用该方法
	 */
	protected void initBaseResource() {
		add_label = messages.get(ILocalStringsKey.ADD_LABEL_BUTTON);
		modify_label = messages.get(ILocalStringsKey.MODIFY_LABEL_BUTTON);
		del_label = messages.get(ILocalStringsKey.DELETE_LABEL_BUTTON);
		ok_label = messages.get(ILocalStringsKey.OK_LABEL_BUTTON);
		cancel_label = messages.get(ILocalStringsKey.CANCEL_LABEL_BUTTON);
	}
}
