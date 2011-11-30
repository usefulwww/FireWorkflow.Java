package org.fireflow.workflowmanagement.mbeans;

import java.util.List;

import org.fireflow.BasicManagedBean;
import org.fireflow.workflowmanagement.persistence.CommonWorkflowDAO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;

@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class WorkflowDefinitionManagementBean extends BasicManagedBean {
	String workflowName4q = null;
	Integer version4q = null;
	
	protected List bizDataList = null;
	
	@ManagedProperty(value="#{CommonWorkflowDAO}")
	CommonWorkflowDAO commonWorkflowDAO;
	
	@Bind(id = "grid", attribute = "value")
	private List data;
	
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
	
	public List getData() {
		this.data = commonWorkflowDAO.findWorkflowDefinitionInfoByCriteria(Expression.sql("1=1"));
		return data;
	}
	

}
