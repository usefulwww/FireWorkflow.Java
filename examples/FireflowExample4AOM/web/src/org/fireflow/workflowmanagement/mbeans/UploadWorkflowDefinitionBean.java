package org.fireflow.workflowmanagement.mbeans;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.fireflow.BasicManagedBean;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.Dom4JFPDLParser;
import org.fireflow.model.io.FPDLParserException;
import org.fireflow.security.util.SecurityUtilities;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.UIFileUpload;
import org.operamasks.faces.component.widget.fileupload.FileUploadItem;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

@ManagedBean(name="UploadWorkflowDefinitionBean", scope=ManagedBeanScope.REQUEST)
public class UploadWorkflowDefinitionBean extends BasicManagedBean {
	
	@Bind
	private UIFileUpload  upFile;

	@Bind
	private Boolean published = Boolean.TRUE;

	public void processUpload(final FileUploadItem fileUploadItem) {
		
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			public void doInTransactionWithoutResult(
					TransactionStatus transactionStatus) {
				InputStream processbyte;
				String errMsg = null;
				try {
					processbyte = fileUploadItem.openStream();

					Dom4JFPDLParser parser = new Dom4JFPDLParser();

					WorkflowProcess process = parser.parse(processbyte);

					WorkflowDefinition workflowdef = new WorkflowDefinition();

					workflowdef.setWorkflowProcess(process);
					workflowdef.setState(published);

					workflowdef.setUploadUser(SecurityUtilities.getCurrentUser()
							.getName());
					workflowdef.setUploadTime(workflowRuntimeContext
							.getCalendarService().getSysDate());

					if (published) {
						workflowdef.setPublishUser(SecurityUtilities.getCurrentUser()
								.getName());
						workflowdef.setPublishTime(workflowRuntimeContext
								.getCalendarService().getSysDate());
					}
					IPersistenceService persistenceService = workflowRuntimeContext
							.getPersistenceService();
					persistenceService.saveOrUpdateWorkflowDefinition(workflowdef);
				} catch (UnsupportedEncodingException e) {
					errMsg = e.getMessage();
					e.printStackTrace();
				} catch (IOException e) {
					errMsg = e.getMessage();
					e.printStackTrace();
				} catch (FPDLParserException e) {
					errMsg = e.getMessage();
					e.printStackTrace();
				}
				if (errMsg != null && !errMsg.trim().equals("")) {
					//TODO:addErrorMessage("", errMsg);
					transactionStatus.setRollbackOnly();
					return;
				}
			}
		});
		
		
		
		
	}

	@Action
	public void save(){
		
	}

	public UIFileUpload getUpFile() {
		return upFile;
	}

	public void setUpFile(UIFileUpload upFile) {
		this.upFile = upFile;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

}
