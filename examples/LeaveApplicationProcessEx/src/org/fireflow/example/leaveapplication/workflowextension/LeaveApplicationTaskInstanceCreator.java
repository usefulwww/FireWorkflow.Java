package org.fireflow.example.leaveapplication.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.taskinstance.ITaskInstanceCreator;
import org.fireflow.example.leaveapplication.data.LeaveApplicationInfo;
import org.fireflow.example.leaveapplication.data.LeaveApplicationInfoDAO;
import org.fireflow.model.Task;
import org.fireflow.model.net.Activity;

public class LeaveApplicationTaskInstanceCreator implements ITaskInstanceCreator {
	@Override
	public ITaskInstance createTaskInstance(IWorkflowSession wfSession,
			RuntimeContext rtctx, IProcessInstance processInstance, Task task, Activity activity)
			throws EngineException {
		LeaveApplicationTaskInstanceExtension taskInstance = new LeaveApplicationTaskInstanceExtension();
		//将业务特征数据从流程变量中取出设置到MyTaskInstanceExtension中。
		String sn = (String)processInstance.getProcessInstanceVariable("sn");
		taskInstance.setSn(sn);
		taskInstance.setLeaveDays((Integer)processInstance.getProcessInstanceVariable("leaveDays"));
		taskInstance.setApprovalFlag((Boolean)processInstance.getProcessInstanceVariable("approvalFlag"));
						
		//MyTaskInstanceExtension中的属性和流程变量中的数据没有必然的映射关系。
		//所以，你也可通过其他方式（例如WorkflowSession的attribute或者直接从业务表）来获得数据。
		LeaveApplicationInfo leaveApplicationInfo = leaveApplicationInfoDAO.findBySn(sn);
		taskInstance.setApplicant(leaveApplicationInfo.getApplicantName());
		taskInstance.setFromDate(leaveApplicationInfo.getFromDate());
		taskInstance.setToDate(leaveApplicationInfo.getToDate());
		
		return taskInstance;
	}

	public LeaveApplicationInfoDAO getLeaveApplicationInfoDAO() {
		return leaveApplicationInfoDAO;
	}

	public void setLeaveApplicationInfoDAO(
			LeaveApplicationInfoDAO leaveApplicationInfoDAO) {
		this.leaveApplicationInfoDAO = leaveApplicationInfoDAO;
	}	
	
	LeaveApplicationInfoDAO leaveApplicationInfoDAO = null;	
}
