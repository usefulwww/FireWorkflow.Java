package org.fireflow.workflowmanagement.mbeans;

import org.fireflow.BasicManagedBean;
import org.fireflow.workflowmanagement.persistence.CommonWorkflowDAO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

public class WorkflowDefinitionManagementBean extends BasicManagedBean {
	String workflowName4q = null;
	Integer version4q = null;
	
	CommonWorkflowDAO commonWorkflowDAO = null;
	
	public String getWorkflowName4q() {
		return workflowName4q;
	}
	public void setWorkflowName4q(String workflowName4q) {
		this.workflowName4q = workflowName4q;
	}
	public Integer getVersion4q() {
		return version4q;
	}
	public void setVersion4q(Integer version4q) {
		this.version4q = version4q; 
	}
	public CommonWorkflowDAO getCommonWorkflowDAO() {
		return commonWorkflowDAO;
	}
	public void setCommonWorkflowDAO(CommonWorkflowDAO commonWorkflowDAO) {
		this.commonWorkflowDAO = commonWorkflowDAO;
	}
	
	protected String executeBizOperQuery(){
		Criterion exp1 = Expression.like("name", "%"+this.workflowName4q+"%");
		Criterion exp3 = exp1;
		if (this.version4q!=null){
			Criterion exp2 = Expression.eq("version", this.version4q);
			exp3 = Expression.and(exp1, exp2);
		}
		
		this.bizDataList = commonWorkflowDAO.findWorkflowDefinitionInfoByCriteria(exp3);
		return null;
	}
	

}
