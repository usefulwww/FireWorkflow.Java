package org.fireflow.example.leaveapplication.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.taskinstance.IAssignable;
import org.fireflow.engine.taskinstance.IAssignmentHandler;
import org.fireflow.example.ou.CurrentUserUtilities;
import org.fireflow.example.ou.User;
import org.fireflow.kernel.KernelException;

/**
 * 该AssignmentHandler将TaskInstance分配给当前用户，并且立即结束新创建的workitem。
 * 适用于填写请假单环节。
 * 
 * @author app
 *
 */
public class CurrentUserAssignmentHandler implements IAssignmentHandler{

	@Override
	public void assign(IAssignable arg0, String arg1) throws EngineException,
			KernelException {
		//取得当前系统用户，即请假申请人
		User currentUser = CurrentUserUtilities.getCurrentUser();
		//为当前用户创建WorkItem
		IWorkItem wi = arg0.asignToActor(currentUser.getId());
		
		//签收新创建的WorkItem
		wi.claim();
		
		//结束新创建的WorkItem，触发流程实例往下流转
		wi.complete();
	}
}
