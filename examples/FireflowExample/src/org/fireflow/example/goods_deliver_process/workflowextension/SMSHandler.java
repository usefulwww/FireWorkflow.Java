package org.fireflow.example.goods_deliver_process.workflowextension;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.taskinstance.IApplicationHandler;

public class SMSHandler implements IApplicationHandler {

	public void execute(ITaskInstance arg0) {
		// TODO Auto-generated method stub
		IProcessInstance processInstance = ((TaskInstance)arg0).getAliveProcessInstance();
		String goodsName = (String)processInstance.getProcessInstanceVariable("goodsName");
		String customerName = (String)processInstance.getProcessInstanceVariable("customerName");
		System.out.println("FireflowExample模拟调用后台程序："+customerName+"你好，你购买的"+goodsName+"即将送货，请注意查收。");
	}

}
