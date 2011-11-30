package org.fireflow.example.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.taskinstance.IAssignable;
import org.fireflow.engine.taskinstance.IAssignmentHandler;
import org.fireflow.kernel.KernelException;

/**
 * 将流程的创建者设置为当前工单的操作员。
 * @author app
 *
 */
public class ProcessInstanceCreator implements IAssignmentHandler {

	public void assign(IAssignable arg0, String arg1) throws EngineException,
			KernelException {
		TaskInstance taskInstance = (TaskInstance)arg0;
		IProcessInstance processInstance = taskInstance.getAliveProcessInstance();
		
		String creator = processInstance.getCreatorId();
		
		if (creator==null){
			throw new EngineException(processInstance,taskInstance.getTask(),"分配工单错误，流程创建者Id为null");
		}

		arg0.asignToActor(creator);
	}

}
