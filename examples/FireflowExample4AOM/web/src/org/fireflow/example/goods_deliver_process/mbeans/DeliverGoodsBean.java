package org.fireflow.example.goods_deliver_process.mbeans;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean( scope = ManagedBeanScope.REQUEST)
public class DeliverGoodsBean {

	/**
	 * 保存业务数据。 在设个demo中，备货操作实际上没有什么数据需要保存，所以没有具体的逻辑代码。
	 * 此方法只是说明逻辑操作和流程操作（例如completeWorkItem)一般情况下是分开的。
	 * 
	 * @return
	 */
	@Action
	public String save() {
		return "SELF";
	}

}
