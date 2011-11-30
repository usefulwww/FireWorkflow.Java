package org.fireflow.example.leaveapplication.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.taskinstance.IAssignable;
import org.fireflow.engine.taskinstance.IAssignmentHandler;
import org.fireflow.kernel.KernelException;

/**
 * 该AssignmentHandler适用于填写请假单环节。
 * 将TaskInstance分配给当前系统用户，在本系统项目中固定为"zhang"。
 * 
 * 
 * @author app
 *
 */
public class CurrentUserAssignmentHandler implements IAssignmentHandler{
	public static final String APPLICANT="zhang"; //请假申请人
	
	/**
	 * 分配任务创建新的WorkItem
	 * @param assignable 该参数实际上是TaskInstance，TaskInstance实现了IAssignable接口。
	 * @param performerName 该参数是流程定义文件中的Performer的name属性。name可以作为角色的名称，可以作为部门名称，或者业务系统约定的其他含义。
	 */
	@Override
	public void assign(IAssignable assignable, String performerName) throws EngineException,
			KernelException {

		IWorkItem wi = assignable.asignToActor(APPLICANT);//将TaskInstance分配给zhang ，并创WorkItem。

	}
}
