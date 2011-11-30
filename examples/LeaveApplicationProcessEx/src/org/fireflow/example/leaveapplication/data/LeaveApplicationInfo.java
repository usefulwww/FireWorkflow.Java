package org.fireflow.example.leaveapplication.data;

import java.util.Date;

/**
 * 请假申请信息POJO
 * @author app
 *
 */
public class LeaveApplicationInfo implements java.io.Serializable {
	private String id = null;
	private String sn = null;//业务流水号
	private String leaveReason = null;//请假类型
	private String fromDate = null;//起始日期
	private String toDate = null;//结束日期
	private Integer leaveDays = 0;//请假天数
	private String applicantId = null;//申请人id
	private String applicantName = null;//申请人姓名
	private String submitTime = null;//申请时间
	private Boolean approvalFlag = Boolean.FALSE;//审批情况，false=不同意，true=同意
	private String approvalDetail = null;//详细意见
	private String approver = null;//审批人
	private Date approvalTime = null;//审批时间
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLeaveReason() {
		return leaveReason;
	}
	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
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
	public Integer getLeaveDays() {
		return leaveDays;
	}
	public void setLeaveDays(Integer leaveDays) {
		this.leaveDays = leaveDays;
	}

	public String getApplicantId() {
		return applicantId;
	}
	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	public String getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}
	public Boolean getApprovalFlag() {
		return approvalFlag;
	}
	public void setApprovalFlag(Boolean approvalFlag) {
		this.approvalFlag = approvalFlag;
	}

	public String getApprovalDetail() {
		return approvalDetail;
	}
	public void setApprovalDetail(String approvalDetail) {
		this.approvalDetail = approvalDetail;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	public Date getApprovalTime() {
		return approvalTime;
	}
	public void setApprovalTime(Date approvalTime) {
		this.approvalTime = approvalTime;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	
}
