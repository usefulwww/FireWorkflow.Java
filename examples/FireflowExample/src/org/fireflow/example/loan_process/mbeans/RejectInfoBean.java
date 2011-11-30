package org.fireflow.example.loan_process.mbeans;

import java.util.Date;
import java.util.List;

import org.fireflow.BasicManagedBean;
import org.fireflow.example.loan_process.persistence.LoanInfo;
import org.fireflow.example.loan_process.persistence.LoanInfoDAO;
import org.fireflow.security.util.SecurityUtilities;

public class RejectInfoBean extends BasicManagedBean {
	LoanInfo loanInfo = null;
	LoanInfoDAO loanInfoDAO = null;
	String currentSn = null;
	public LoanInfo getLoanInfo() {
		if (loanInfo==null && currentSn!=null){
			List l = loanInfoDAO.findBySn(currentSn);
			if (l!=null && l.size()>0){
				loanInfo = (LoanInfo)l.get(0);

				loanInfo.setRejectInfoInputTime(new Date());
				
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
	
	/**
	 * 保存拒绝贷款环节录入的信息
	 */
	protected String executeSaveBizData(){
		loanInfoDAO.attachDirty(loanInfo);		
		return null;
	}
}
