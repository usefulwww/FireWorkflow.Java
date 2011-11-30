package org.fireflow.example.loan_process.persistence;

import java.util.Date;

/**
 * AbstractLoanInfo entity provides the base persistence definition of the
 * LoanInfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractLoanInfo implements java.io.Serializable {

	// Fields

	private String id;
	private String sn;
	private String applicantName;
	private String applicantId;
	private String address;
	private Integer salary;
	private Integer loanValue;
	private String returnDate;
	private String loanteller;
	private Date appInfoInputDate;
	private Boolean salaryIsReal;
	private Boolean creditStatus;
	private Boolean riskFlag;
	private String riskEvaluator;
	private Date riskInfoInputDate;
	private Boolean decision;
	private String lendMoneyInfo;
	private String lendMoneyOfficer;
	private Date lendMoneyInfoInputTime;
	private String rejectInfo;
	private Date rejectInfoInputTime;
	String examinerList = null;
	String approverList = null;
	String opponentList = null;
	// Constructors

	/** default constructor */
	public AbstractLoanInfo() {
	}



	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSn() {
		return this.sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getApplicantName() {
		return this.applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getApplicantId() {
		return this.applicantId;
	}

	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getSalary() {
		return this.salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	public Integer getLoanValue() {
		return this.loanValue;
	}

	public void setLoanValue(Integer loanValue) {
		this.loanValue = loanValue;
	}

	public String getReturnDate() {
		return this.returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public String getLoanteller() {
		return this.loanteller;
	}

	public void setLoanteller(String loanteller) {
		this.loanteller = loanteller;
	}

	public Date getAppInfoInputDate() {
		return this.appInfoInputDate;
	}

	public void setAppInfoInputDate(Date appInfoInputDate) {
		this.appInfoInputDate = appInfoInputDate;
	}

	public Boolean getSalaryIsReal() {
		return this.salaryIsReal;
	}

	public void setSalaryIsReal(Boolean salaryIsReal) {
		this.salaryIsReal = salaryIsReal;
	}

	public Boolean getCreditStatus() {
		return this.creditStatus;
	}

	public void setCreditStatus(Boolean creditStatus) {
		this.creditStatus = creditStatus;
	}

	public String getRiskEvaluator() {
		return this.riskEvaluator;
	}

	public void setRiskEvaluator(String riskEvaluator) {
		this.riskEvaluator = riskEvaluator;
	}

	public Date getRiskInfoInputDate() {
		return this.riskInfoInputDate;
	}

	public void setRiskInfoInputDate(Date riskInfoInputDate) {
		this.riskInfoInputDate = riskInfoInputDate;
	}

	public Boolean getDecision() {
		return this.decision;
	}

	public void setDecision(Boolean decision) {
		this.decision = decision;
	}

	public String getLendMoneyInfo() {
		return this.lendMoneyInfo;
	}

	public void setLendMoneyInfo(String lendMoneyInfo) {
		this.lendMoneyInfo = lendMoneyInfo;
	}

	public String getLendMoneyOfficer() {
		return this.lendMoneyOfficer;
	}

	public void setLendMoneyOfficer(String lendMoneyOfficer) {
		this.lendMoneyOfficer = lendMoneyOfficer;
	}

	public Date getLendMoneyInfoInputTime() {
		return this.lendMoneyInfoInputTime;
	}

	public void setLendMoneyInfoInputTime(Date lendMoneyInfoInputTime) {
		this.lendMoneyInfoInputTime = lendMoneyInfoInputTime;
	}

	public String getRejectInfo() {
		return this.rejectInfo;
	}

	public void setRejectInfo(String rejectInfo) {
		this.rejectInfo = rejectInfo;
	}

	public Date getRejectInfoInputTime() {
		return this.rejectInfoInputTime;
	}

	public void setRejectInfoInputTime(Date rejectInfoInputTime) {
		this.rejectInfoInputTime = rejectInfoInputTime;
	}

	public Boolean getRiskFlag() {
		return riskFlag;
	}

	public void setRiskFlag(Boolean riskFlag) {
		this.riskFlag = riskFlag;
	}
	public String getExaminerList() {
		return examinerList;
	}

	public void setExaminerList(String examinerList) {
		this.examinerList = examinerList;
	}

	public String getApproverList() {
		return approverList;
	}

	public void setApproverList(String approverList) {
		this.approverList = approverList;
	}

	public String getOpponentList() {
		return opponentList;
	}

	public void setOpponentList(String opponentList) {
		this.opponentList = opponentList;
	}

}