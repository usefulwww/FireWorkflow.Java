package org.fireflow.workflowmanagement.mbeans;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.fireflow.BasicManagedBean;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.Dom4JFPDLParser;
import org.fireflow.model.io.FPDLParserException;
import org.fireflow.security.util.SecurityUtilities;

public class UploadWorkflowDefinitionBean extends BasicManagedBean {
	private UploadedFile _upFile;
	
	private Boolean published = Boolean.TRUE;

	public UploadedFile getUpFile() {
		return _upFile;
	}

	public void setUpFile(UploadedFile file) {
		_upFile = file;
	}
	
	

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	protected String executeSaveBizData() {
		InputStream processbyte;
		String errMsg = null;
		try {
			processbyte = _upFile.getInputStream();
			

			Dom4JFPDLParser parser = new Dom4JFPDLParser();
			
			WorkflowProcess process = parser.parse(processbyte);
			
			WorkflowDefinition workflowdef = new WorkflowDefinition();

			
			
			workflowdef.setWorkflowProcess(process);
			workflowdef.setState(published);
			
			workflowdef.setUploadUser(SecurityUtilities.getCurrentUser().getName());
			workflowdef.setUploadTime(workflowRuntimeContext.getCalendarService().getSysDate());
			
			if (published){
				workflowdef.setPublishUser(SecurityUtilities.getCurrentUser().getName());
				workflowdef.setPublishTime(workflowRuntimeContext.getCalendarService().getSysDate());
			}
			IPersistenceService persistenceService = this.workflowRuntimeContext
					.getPersistenceService();
			persistenceService.saveOrUpdateWorkflowDefinition(workflowdef);
		}
		catch(UnsupportedEncodingException e){
			errMsg = e.getMessage();
			e.printStackTrace();
		}		
		catch (IOException e) {
			errMsg = e.getMessage();
			e.printStackTrace();
		}
		catch(FPDLParserException e){
			errMsg = e.getMessage();
			e.printStackTrace();
		}
		return errMsg;
	}

}
