package org.fireflow.example.loan_process.persistence;

public class AbstractApproveInfo implements java.io.Serializable{
	String id = null;
	String sn = null;
	String approver = null;
	Boolean decision = null;
	String detail = null;
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
	public Boolean getDecision() {
		return decision;
	}
	public void setDecision(Boolean decision) {
		this.decision = decision;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
}
