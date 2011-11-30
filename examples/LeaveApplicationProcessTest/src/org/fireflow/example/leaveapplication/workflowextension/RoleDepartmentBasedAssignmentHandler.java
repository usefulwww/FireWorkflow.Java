package org.fireflow.example.leaveapplication.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.taskinstance.IAssignable;
import org.fireflow.engine.taskinstance.IAssignmentHandler;
import org.fireflow.kernel.KernelException;

/**
 * 该AssignmentHandler将TaskInstance分配给特定部门的特定角色。
 * 角色名称通过performerName传入，部门和流程创建者（即请假申请人）的部门相同。
 * 
 * 在这个Project中，部门经理固定为"manager_chen"
 * @author app
 *
 */
public class RoleDepartmentBasedAssignmentHandler implements IAssignmentHandler {
	public static final String DEPARTMENT_MANAGER = "manager_chen";
	
	/**
	 * 分配任务创建新的WorkItem
	 * @param assignable 该参数实际上是TaskInstance，TaskInstance实现了IAssignable接口。
	 * @param performerName 该参数是流程定义文件中的Performer的name属性。name可以作为角色的名称，可以作为部门名称，或者业务系统约定的其他含义。
	 */	
	@Override
	public void assign(IAssignable assignable, String performerName) throws EngineException,
			KernelException {
		IWorkItem wi = assignable.asignToActor(DEPARTMENT_MANAGER);//将TaskInstance分配给manager_chen ，并创WorkItem。
	}

}
