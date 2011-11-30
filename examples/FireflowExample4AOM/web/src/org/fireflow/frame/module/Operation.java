/*$Id: Operation.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module;

/**
 * 管理操作枚举类，枚举管理操作的基本行为
 * @author chenhongxin
 */
enum Operation {
	/**
	 * 无操作
	 */
	NOTHING,
	
	/**
	 * 添加操作
	 */
	ADD,
	
	/**
	 * 修改操作
	 */
	MODIFY
}
