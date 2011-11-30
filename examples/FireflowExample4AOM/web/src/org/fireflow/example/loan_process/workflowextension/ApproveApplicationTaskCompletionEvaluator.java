package org.fireflow.example.loan_process.workflowextension;

import java.util.List;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.ITaskInstanceCompletionEvaluator;
import org.fireflow.example.loan_process.persistence.ApproveInfo;
import org.fireflow.example.loan_process.persistence.ApproveInfoDAO;
import org.fireflow.example.loan_process.persistence.LoanInfo;
import org.fireflow.example.loan_process.persistence.LoanInfoDAO;
import org.fireflow.kernel.KernelException;

/**
 * 审批任务实例 “终结评价器”。根据特殊的业务规则来决定任务实例是否可以结束。
 * @author 非也 
 */
public class ApproveApplicationTaskCompletionEvaluator implements
		ITaskInstanceCompletionEvaluator {
	ApproveInfoDAO approveInfoDAO = null;
	LoanInfoDAO loanInfoDAO = null;
	
	public ApproveInfoDAO getApproveInfoDAO() {
		return approveInfoDAO;
	}
	public void setApproveInfoDAO(ApproveInfoDAO approveInfoDAO) {
		this.approveInfoDAO = approveInfoDAO;
	}
	
	
	public LoanInfoDAO getLoanInfoDAO() {
		return loanInfoDAO;
	}
	public void setLoanInfoDAO(LoanInfoDAO loanInfoDAO) {
		this.loanInfoDAO = loanInfoDAO;
	}
	/**
	 * 审批环节的会签规则是：只要3各独立审核员中有2个或者2个以上审核通过，则认为
	 * 贷款审批通过。
	 */
	public boolean taskInstanceCanBeCompleted(IWorkflowSession workflowSession,
			RuntimeContext runtimeContext, IProcessInstance processInstance, ITaskInstance taskInstance)
			throws EngineException, KernelException {
        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<IWorkItem> workItems = persistenceService.findWorkItemsForTaskInstance(taskInstance.getId());
        
        //从流程变量中获取业务流水号
        String sn = (String)processInstance.getProcessInstanceVariable("sn");

        //已经完成的WorkItem数量
        int completedWorkItemCount = 0;

      //审批同意的决定的数量
        int approvedDecitionCount = 0;
        
        StringBuffer examinerList = new StringBuffer();//所有审核人名单
        StringBuffer approverList = new StringBuffer();//同意者名单
        StringBuffer opponentList = new StringBuffer();//不同意者名单
        for (int i=0;i<workItems.size();i++){
        	IWorkItem wi = workItems.get(i);
        	ApproveInfo approveInfo = approveInfoDAO.findBySnAndUserId(sn, wi.getActorId());
        	examinerList.append(approveInfo.getApprover()).append(",");
        	if (wi.getState()==IWorkItem.COMPLETED){
        		completedWorkItemCount++;
        		if (approveInfo.getDecision()){
        			approvedDecitionCount++;
        			approverList.append(approveInfo.getApprover()).append(",");
        		}else{
        			opponentList.append(approveInfo.getApprover()).append(",");
        		}
        	}
        }
        
        
        //------------------判断是否可以结束该汇签任务-----------
        float size = Float.valueOf(Integer.toString(workItems.size()));
        float theRule = 2/3f;
        float currentCompletedPercentage = completedWorkItemCount/size;//已经完成的工单占总工单的比例
        float currentAggreePercentage = approvedDecitionCount/size;//已经同意的比例
        
        
        
        //如果完成的工单数量小于2/3，则直接返回false,即不可以结束TaskInstance
        if (currentCompletedPercentage<theRule){
        	return false;
        }
        //如果同意的数量达到2/3则直接结束TaskInstance
        else if (currentAggreePercentage>=theRule){
        	
        	//修改流程变量的值
        	processInstance.setProcessInstanceVariable("Decision",Boolean.TRUE);
        	
        	//将最终审批决定纪录到业务表中
        	List<LoanInfo> loanInfoList = loanInfoDAO.findBySn(sn);
        	if (loanInfoList.size()>0){
        		LoanInfo loanInfo  = loanInfoList.get(0);
        		loanInfo.setDecision(Boolean.TRUE);
        		loanInfo.setExaminerList(examinerList.toString());
        		loanInfo.setApproverList(approverList.toString());
        		loanInfo.setOpponentList(opponentList.toString());
        		loanInfoDAO.attachDirty(loanInfo);
        	}
        	
        	return true;
        }
        //当所有的workItem结束时，可以结束TaskInstance 
        else if (completedWorkItemCount==workItems.size()){
        	List<LoanInfo> loanInfoList = loanInfoDAO.findBySn(sn);
        	LoanInfo loanInfo = null;
        	if (loanInfoList.size()>0){
        		loanInfo  = loanInfoList.get(0);
        	}        	
        	
        	if (currentAggreePercentage<theRule){
        		//修改流程变量的值
        		processInstance.setProcessInstanceVariable("Decision",Boolean.FALSE);
        		
        		//将最终审批决定记录到业务表中
        		if (loanInfo!=null )loanInfo.setDecision(Boolean.FALSE);
        		loanInfo.setExaminerList(examinerList.toString());
        		loanInfo.setApproverList(approverList.toString());
        		loanInfo.setOpponentList(opponentList.toString());
        		loanInfoDAO.attachDirty(loanInfo);        		
        	}else{
        		//修改流程变量的值
        		processInstance.setProcessInstanceVariable("Decision",Boolean.TRUE);
        		
        		//将最终审批决定记录到业务表中
        		if (loanInfo!=null )loanInfo.setDecision(Boolean.TRUE);
        		loanInfo.setExaminerList(examinerList.toString());
        		loanInfo.setApproverList(approverList.toString());
        		loanInfo.setOpponentList(opponentList.toString());
        		loanInfoDAO.attachDirty(loanInfo);        		
        	}
        	
        	return true;
        }
        return false;
	}
	


}
