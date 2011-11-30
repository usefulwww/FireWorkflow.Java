package org.fireflow.example.loan_process.workflowextension;

import java.util.List;

import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.example.workflowextension.IExampleTaskInstance;

public class LoanTaskInstance extends TaskInstance implements IExampleTaskInstance{
	String sn = null;
	String applicantName = null;
	Integer loanValue= null;
	Boolean riskFlag = null;
	Boolean decision = null;
	
	List<IWorkItem> workitems = null;
	
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public Integer getLoanValue() {
		return loanValue;
	}

	public void setLoanValue(Integer loanValue) {
		this.loanValue = loanValue;
	}

	public Boolean getRiskFlag() {
		return riskFlag;
	}

	public void setRiskFlag(Boolean riskFlag) {
		this.riskFlag = riskFlag;
	}

	public Boolean getDecision() {
		return decision;
	}

	public void setDecision(Boolean decision) {
		this.decision = decision;
	}

	public String getBizInfo() {
		String riskEvaluateState = "办理中";
		if (this.getStepNumber()<=2 || this.getRiskFlag()==null){
			riskEvaluateState = "办理中";
		}
		else if (this.getStepNumber()>2 && this.getRiskFlag()!=null && this.getRiskFlag()==false){
			riskEvaluateState = "通过";
		}else if (this.getStepNumber()>2 && this.getRiskFlag()!=null && this.getRiskFlag()==true){
			riskEvaluateState = "不通过";
		}
		String approveFlag = "办理中";
		if (this.getStepNumber()<=3 || this.getDecision()==null){
			approveFlag = "办理中";
		}
		else if (this.getStepNumber()>3 && this.getDecision()!=null && this.getDecision()==false){
			approveFlag="不通过";
		}else if(this.getStepNumber()>3 && this.getDecision()!=null && this.getDecision()==true){
			approveFlag="通过";
		}
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("业务流水号:").append(this.getSn()).append("    ")
			.append("客户姓名:").append(this.getApplicantName()).append("    ")
			.append("贷款金额:").append(this.getLoanValue()).append("    ")
			.append("风险核查:").append(riskEvaluateState).append("    ")
			.append("贷款审批:").append(approveFlag);
		return sbuf.toString();
	}

	public List<IWorkItem> getWorkItems() {
		return workitems;
	}

	public void setWorkItems(List<IWorkItem> workItems) {
		this.workitems = workItems;
		
	}

}
