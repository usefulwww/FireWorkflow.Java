package org.fireflow.example.leaveapplication.workflowextension;

import org.fireflow.engine.impl.TaskInstance;

public class LeaveApplicationTaskInstanceExtension extends TaskInstance implements ITaskInstanceExtension{
	String sn = null;//流水号
	String applicant = null;//申请人
	Integer leaveDays = null;//请假天数
	Boolean approvalFlag = null;//审批意见
	String fromDate = null;//开始日期
	String toDate = null;//结束日期
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getApplicant() {
		return applicant;
	}
	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}
	public Integer getLeaveDays() {
		return leaveDays;
	}
	public void setLeaveDays(Integer leaveDays) {
		this.leaveDays = leaveDays;
	}
	public Boolean getApprovalFlag() {
		return approvalFlag;
	}
	public void setApprovalFlag(Boolean approvalFlag) {
		this.approvalFlag = approvalFlag;
	}
	

	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	@Override
	public String getBizInfo() {
		return "申请人："+applicant+"；从"+fromDate+"至"+toDate+"；共"+leaveDays+"天";
	}
	
	
}
