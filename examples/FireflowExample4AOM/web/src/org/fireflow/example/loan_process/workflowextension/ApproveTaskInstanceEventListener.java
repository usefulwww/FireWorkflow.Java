package org.fireflow.example.loan_process.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.example.loan_process.persistence.ApproveInfo;
import org.fireflow.example.loan_process.persistence.ApproveInfoDAO;
import org.fireflow.example.workflowextension.TaskInstanceEventListener4All;

/**
 * 该事件监听器首先模拟工单创建后向操作者发送通知信息，
 * 然后初始化给操作员初始化一条审批意见。
 * @author app
 *
 */
public class ApproveTaskInstanceEventListener extends
		TaskInstanceEventListener4All {
	ApproveInfoDAO approveInfoDAO = null;
	
    protected void afterWorkItemCreated(IWorkflowSession currentSession,
			IProcessInstance processInstance,ITaskInstance taskInstance,IWorkItem workItem)throws EngineException{

    	//调用父类实现“模拟向操作者发送通知信息”
    	super.afterWorkItemCreated(currentSession, processInstance, taskInstance, workItem);
    	
    	//初始化一条审批信息，审批信息属于业务数据
    	ApproveInfo approveInfo = new ApproveInfo();
    	approveInfo.setSn((String)processInstance.getProcessInstanceVariable("sn"));
    	approveInfo.setApprover(workItem.getActorId());
    	approveInfo.setDecision(null);
    	approveInfo.setDetail("--");
    	
    	approveInfoDAO.attachDirty(approveInfo);
    }

	public ApproveInfoDAO getApproveInfoDAO() {
		return approveInfoDAO;
	}

	public void setApproveInfoDAO(ApproveInfoDAO approveInfoDAO) {
		this.approveInfoDAO = approveInfoDAO;
	}
    
    
    
}
