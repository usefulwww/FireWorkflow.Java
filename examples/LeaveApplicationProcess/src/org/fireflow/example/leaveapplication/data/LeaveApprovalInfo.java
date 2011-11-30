package org.fireflow.example.leaveapplication.data;

import java.util.Date;

/**
 * 请假审批信息POJO
 * @author app
 *
 */
public class LeaveApprovalInfo implements java.io.Serializable {
	String id = null;
	String sn = null;//即申请流水号
	String approver = null;//审批人
	Boolean approvalFlag = Boolean.FALSE;//审批决定
	String detail = null;//详细意见
	Date approvalTime = null;//审批时间
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}

	public Boolean getApprovalFlag() {
		return approvalFlag;
	}
	public void setApprovalFlag(Boolean approvalFlag) {
		this.approvalFlag = approvalFlag;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public Date getApprovalTime() {
		return approvalTime;
	}
	public void setApprovalTime(Date approvalTime) {
		this.approvalTime = approvalTime;
	}

	
}
