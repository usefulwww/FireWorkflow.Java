package org.fireflow;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.fireflow.engine.RuntimeContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.jsf.FacesContextUtils;

public abstract class AbstractManagedBean implements IManagedBean ,Serializable{

	public static final String SELF_VIEW = "SELF_VIEW";
	protected transient Logger logger = Logger.getLogger("org.fireflow.example");
	
	protected transient TransactionTemplate transactionTemplate = null;
	protected transient RuntimeContext workflowRuntimeContext = null;
	protected String outcome = SELF_VIEW;

	
	public AbstractManagedBean(){
		transactionTemplate = (TransactionTemplate)FacesContextUtils.getWebApplicationContext(
					FacesContext.getCurrentInstance()).getBean("transactionTemplate");
		
		workflowRuntimeContext = (RuntimeContext)FacesContextUtils.getWebApplicationContext(
				FacesContext.getCurrentInstance()).getBean("runtimeContext");
		
	}
	
	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	
	public RuntimeContext getWorkflowRuntimeContext() {
		return workflowRuntimeContext;
	}

	public void setWorkflowRuntimeContext(RuntimeContext workflowRuntimeContext) {
		this.workflowRuntimeContext = workflowRuntimeContext;
	}

	/**
	 * 添加错误信息
	 * 
	 * @param obj
	 * @param message
	 */
	public void addErrorMessage(String obj, String message) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				"错误信息：", message);
		context.addMessage(obj, msg);
	}

	/**
	 * 添加提示信息
	 * 
	 * @param kind
	 * @param summary
	 * @param message
	 */
	public void addMessage(Severity kind, String summary, String message) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage msg = new FacesMessage(kind, summary, message);
		context.addMessage("", msg);
	}
	
	
	//****************业务逻辑*************/
	//业务查询
	public final String doBizOperQuery() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			public void doInTransactionWithoutResult(
					TransactionStatus transactionStatus) {
				String errMsg = null;

				errMsg = executeBizOperQuery();
				if (errMsg != null && !errMsg.trim().equals("")) {
					addErrorMessage("", errMsg);
					transactionStatus.setRollbackOnly();
					return;
				}

			}
		});
		return outcome;
	}
	
	protected abstract String executeBizOperQuery();

	
	//数据选中
	public final String onSelectBizData() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			public void doInTransactionWithoutResult(
					TransactionStatus transactionStatus) {
				String errMsg = null;
				errMsg = fireBizDataSelected();
				if (errMsg != null && !errMsg.trim().equals("")) {
					addErrorMessage("", errMsg);
					transactionStatus.setRollbackOnly();
					return;
				}
			}
		});

		return outcome;
	}	
	
	protected abstract String fireBizDataSelected();

	
	
	//数据保存
	
	public final String doSaveBizData() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			public void doInTransactionWithoutResult(
					TransactionStatus transactionStatus) {


				// 保存到数据库
				String errMsg = executeSaveBizData();
				if (errMsg != null && !errMsg.trim().equals("")) {
					addErrorMessage("", errMsg);
					transactionStatus.setRollbackOnly();
					return;
				}

			}

		});

		// 导航到新的视图
		return outcome;
	}
	
	protected abstract String executeSaveBizData();


}
