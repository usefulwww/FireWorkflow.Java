package org.fireflow.example.loan_process.mbeans;

import java.util.Date;
import java.util.List;

import org.fireflow.BasicManagedBean;
import org.fireflow.example.loan_process.persistence.LoanInfo;
import org.fireflow.example.loan_process.persistence.LoanInfoDAO;
import org.fireflow.security.util.SecurityUtilities;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.annotation.SaveState;

@ManagedBean(scope = ManagedBeanScope.REQUEST)
public class LendMoneyInfoBean extends BasicManagedBean {
	
	@SaveState
	@Bind
	private LoanInfo loanInfo = null;
	
	@ManagedProperty("#{LoanInfoDAO}")
	private LoanInfoDAO loanInfoDAO = null;
	
	@Bind
	@ManagedProperty("#{requestScope.CURRENT_WORKITEM.taskInstance.sn}")
	private String currentSn = null;
	
	public LoanInfo getLoanInfo() {
		if (loanInfo==null && currentSn!=null){
			List l = loanInfoDAO.findBySn(currentSn);
			if (l!=null && l.size()>0){
				loanInfo = (LoanInfo)l.get(0);
				loanInfo.setLendMoneyOfficer(SecurityUtilities.getCurrentUser().getName());
				loanInfo.setLendMoneyInfoInputTime(new Date());
			}
		}
		
		return loanInfo;
	}
	
	
	/**
	 * 保存放款环节录入的信息
	 */
	@Action(id="save")
	protected String executeSaveBizData(){
		loanInfoDAO.attachDirty(loanInfo);		
		return null;
	}
}
