package org.fireflow.workflowmanagement.mbeans;

import java.util.List;

import org.fireflow.workflowmanagement.persistence.CommonWorkflowDAO;

public class WorkflowTraceBean {
	private String sn = null;
	private List workItemList = null;
	
	CommonWorkflowDAO commonDAO = null;

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
	public List getWorkItemList(){
		return workItemList;
	}
	
	
	
	
	public CommonWorkflowDAO getCommonDAO() {
		return commonDAO;
	}

	public void setCommonDAO(CommonWorkflowDAO commonDAO) {
		this.commonDAO = commonDAO;
	}

	/**
	 * 通过流水号查询业务办理的进度
	 * @return
	 */
	public String doQuery(){
		workItemList = commonDAO.findWorkItemsForSN(sn);
		return "SELF";
	}
	
}
