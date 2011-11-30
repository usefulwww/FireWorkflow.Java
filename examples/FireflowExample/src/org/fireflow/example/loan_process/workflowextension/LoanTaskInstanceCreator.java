package org.fireflow.example.loan_process.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.taskinstance.ITaskInstanceCreator;
import org.fireflow.model.Task;
import org.fireflow.model.net.Activity;

public class LoanTaskInstanceCreator implements ITaskInstanceCreator {

	public ITaskInstance createTaskInstance(IWorkflowSession workflowSession,
			RuntimeContext rtCtx, IProcessInstance processInstance, Task task, Activity activity)
			throws EngineException {
		LoanTaskInstance taskInstance = new LoanTaskInstance();
		taskInstance.setSn((String)processInstance.getProcessInstanceVariable("sn"));
		taskInstance.setApplicantName((String)processInstance.getProcessInstanceVariable("applicantName"));
		taskInstance.setLoanValue((Integer)processInstance.getProcessInstanceVariable("loanValue"));
		taskInstance.setRiskFlag((Boolean)processInstance.getProcessInstanceVariable("RiskFlag"));
		taskInstance.setDecision((Boolean)processInstance.getProcessInstanceVariable("Decision"));

		return taskInstance;
	}

}
