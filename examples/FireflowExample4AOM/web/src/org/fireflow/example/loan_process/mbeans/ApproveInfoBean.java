package org.fireflow.example.loan_process.mbeans;

import java.util.Date;
import java.util.List;

import org.fireflow.BasicManagedBean;
import org.fireflow.example.loan_process.persistence.ApproveInfo;
import org.fireflow.example.loan_process.persistence.ApproveInfoDAO;
import org.fireflow.example.loan_process.persistence.LoanInfo;
import org.fireflow.example.loan_process.persistence.LoanInfoDAO;
import org.fireflow.security.persistence.User;
import org.fireflow.security.util.SecurityUtilities;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.annotation.SaveState;

@ManagedBean(scope = ManagedBeanScope.REQUEST)
public class ApproveInfoBean extends BasicManagedBean{
	
	
	@SaveState
	@Bind
	private LoanInfo loanInfo = this.getLoanInfo();
	
	@SaveState
	@Bind
	private ApproveInfo approveInfo = this.getApproveInfo();
	
	@ManagedProperty("#{LoanInfoDAO}")
	private LoanInfoDAO loanInfoDAO = null;
	
	@ManagedProperty("#{ApproveInfoDAO}")
	private ApproveInfoDAO approveInfoDAO = null;
	
	@SaveState
	@Bind
	@ManagedProperty("#{requestScope.CURRENT_WORKITEM.taskInstance.sn}")
	private String currentSn = null;
	
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

	public ApproveInfo getApproveInfo() {
		if (approveInfo==null && currentSn!=null){
			User currentUser = SecurityUtilities.getCurrentUser();
			approveInfo = approveInfoDAO.findBySnAndUserId(currentSn,currentUser.getLoginid());
			
		}
			
		return approveInfo;
	}

	
	/**
	 * 保存审批信息
	 */
	@Action(id="save")
	protected String executeSaveBizData(){
		approveInfoDAO.attachDirty(approveInfo);
		return null;
	}
}
