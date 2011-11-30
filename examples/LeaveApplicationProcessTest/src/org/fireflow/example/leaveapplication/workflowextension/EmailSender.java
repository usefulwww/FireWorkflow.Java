package org.fireflow.example.leaveapplication.workflowextension;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.taskinstance.IApplicationHandler;

/**
 * 模拟向邮件系统发送邮件
 * @author app
 *
 */
public class EmailSender implements IApplicationHandler {

	@Override
	public void execute(ITaskInstance arg0) {
		IProcessInstance processInstance = ((TaskInstance)arg0).getAliveProcessInstance();
		//从流程变量中取出相关的信息
		String applicant = (String)processInstance.getProcessInstanceVariable("applicant");//申请人
		Integer leaveDays = (Integer)processInstance.getProcessInstanceVariable("leaveDays");//请假天数
		Boolean approvalFlag = (Boolean)processInstance.getProcessInstanceVariable("approvalFlag");//审批标志
		System.out.println("\n\n=========EmailSender:模拟发送邮件给申请人.....");
		System.out.println("申请人是:"+applicant);
		System.out.println("请假天数是:"+leaveDays);
		System.out.println("审批通过否:"+(approvalFlag?"通过":"不通过"));
		System.out.println("==============EmailSender执行结束================");
	}

}
