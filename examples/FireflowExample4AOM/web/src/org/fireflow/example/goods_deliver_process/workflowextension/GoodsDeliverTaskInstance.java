package org.fireflow.example.goods_deliver_process.workflowextension;

import java.util.List;

import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.example.workflowextension.IExampleTaskInstance;

public class GoodsDeliverTaskInstance extends TaskInstance implements
		IExampleTaskInstance {
	private String sn = null;
	private String goodsName = null;
	private Long quantity = null;
	private String customerName = null;
	
	private List<IWorkItem> workItems = null;

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBizInfo() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("\t\t订单号:").append(sn)
				.append("\t\t商品名称:")	.append(goodsName)
				.append("\t\t数量:").append(quantity)
				.append("\t\t客户姓名:").append(customerName);
		return sbuf.toString();
	}

	public List<IWorkItem> getWorkItems() {
		return this.workItems;
	}

	public void setWorkItems(List<IWorkItem> workItems) {
		this.workItems = workItems;
	}

}
