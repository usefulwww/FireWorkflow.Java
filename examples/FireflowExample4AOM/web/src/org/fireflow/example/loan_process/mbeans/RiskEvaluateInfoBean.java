package org.fireflow.example.loan_process.mbeans;

import java.util.Date;
import java.util.List;

import org.fireflow.BasicManagedBean;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.IWorkflowSessionCallback;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.example.loan_process.persistence.LoanInfo;
import org.fireflow.example.loan_process.persistence.LoanInfoDAO;
import org.fireflow.kernel.KernelException;
import org.fireflow.security.util.SecurityUtilities;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.annotation.SaveState;

@ManagedBean(scope = ManagedBeanScope.REQUEST)
public class RiskEvaluateInfoBean extends BasicManagedBean {
	
	@ManagedProperty("#{LoanInfoDAO}")
	private LoanInfoDAO loanInfoDAO = null;
	
	@Bind
	@ManagedProperty("#{requestScope.CURRENT_WORKITEM.taskInstance.sn}")
	private String currentSn = null;
	
	@Bind
	@SaveState
	@ManagedProperty("#{requestScope.CURRENT_WORKITEM.taskInstance.processInstanceId}")
	private String currentProcessInstanceId = null;
	
	@SaveState
	@Bind
	private LoanInfo loanInfo = this.getLoanInfo();
	
	public LoanInfo getLoanInfo() {
		if (loanInfo==null && currentSn!=null){
			List l = loanInfoDAO.findBySn(currentSn);
			if (l!=null && l.size()>0){
				loanInfo = (LoanInfo)l.get(0);
				loanInfo.setRiskEvaluator(SecurityUtilities.getCurrentUser().getName());
				loanInfo.setRiskInfoInputDate(new Date());
			}
		}
		
		return loanInfo;
	}
	
	
	/**
	 * 保存风险核查信息
	 */
	@Action(id="save")
	protected String executeSaveBizData(){
		//1、首先通过"收入状况是否属实"和"信用状况是否合格"这两项指标来设置RiskFlag流程变量
		if (loanInfo.getSalaryIsReal() && loanInfo.getCreditStatus()){
			loanInfo.setRiskFlag(false);//false表示风险评估通过
		}else {
			loanInfo.setRiskFlag(true);//表示有风险
		}
		//2、将RiskFlag设置到流程变量
		IWorkflowSession workflowSession = this.workflowRuntimeContext.getWorkflowSession();
		try {
			workflowSession.execute(new IWorkflowSessionCallback(){

				public Object doInWorkflowSession(RuntimeContext arg0)
						throws EngineException, KernelException {
					IPersistenceService persistenceService = arg0.getPersistenceService();
					IProcessInstance processInstance = persistenceService.findAliveProcessInstanceById(currentProcessInstanceId);
					processInstance.setProcessInstanceVariable("RiskFlag", loanInfo.getRiskFlag());
					persistenceService.saveOrUpdateProcessInstance(processInstance);
					return null;
				}
				
			});
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (KernelException e) {
			e.printStackTrace();
		}
		
		//3、保存业务数据
		loanInfoDAO.attachDirty(loanInfo);
		return null;
	}
}
