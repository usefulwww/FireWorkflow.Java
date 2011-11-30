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

public class ApproveInfoBean extends BasicManagedBean{
	LoanInfo loanInfo = null;
	ApproveInfo approveInfo = null;
	LoanInfoDAO loanInfoDAO = null;
	ApproveInfoDAO approveInfoDAO = null;
	String currentSn = null;
	
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
	
	public void setLoanInfo(LoanInfo loanInfo) {
		this.loanInfo = loanInfo;
	}
	public LoanInfoDAO getLoanInfoDAO() {
		return loanInfoDAO;
	}
	public void setLoanInfoDAO(LoanInfoDAO loanInfoDAO) {
		this.loanInfoDAO = loanInfoDAO;
	}
	public String getCurrentSn() {
		return currentSn;
	}
	public void setCurrentSn(String currentSn) {
		this.currentSn = currentSn;
	}

	public ApproveInfo getApproveInfo() {
		if (approveInfo==null && currentSn!=null){
			User currentUser = SecurityUtilities.getCurrentUser();
			approveInfo = approveInfoDAO.findBySnAndUserId(currentSn,currentUser.getLoginid());
			
		}
			
		return approveInfo;
	}

	public void setApproveInfo(ApproveInfo approveInfo) {
		this.approveInfo = approveInfo;
	}

	public ApproveInfoDAO getApproveInfoDAO() {
		return approveInfoDAO;
	}

	public void setApproveInfoDAO(ApproveInfoDAO approveInfoDAO) {
		this.approveInfoDAO = approveInfoDAO;
	}
	
	/**
	 * 保存审批信息
	 */
	protected String executeSaveBizData(){
		approveInfoDAO.attachDirty(approveInfo);
		return null;
	}
}
