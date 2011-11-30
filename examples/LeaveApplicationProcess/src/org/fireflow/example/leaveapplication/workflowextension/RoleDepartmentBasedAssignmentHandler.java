package org.fireflow.example.leaveapplication.workflowextension;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.taskinstance.IAssignable;
import org.fireflow.engine.taskinstance.IAssignmentHandler;
import org.fireflow.example.ou.OUManagementMock;
import org.fireflow.example.ou.User;
import org.fireflow.kernel.KernelException;

/**
 * 该AssignmentHandler将TaskInstance分配给特定部门的特定角色。
 * 角色名称通过arg1传入，部门和流程创建者（即请假申请人）的部门相同。
 * @author app
 *
 */
public class RoleDepartmentBasedAssignmentHandler implements IAssignmentHandler {

	@Override
	public void assign(IAssignable arg0, String arg1) throws EngineException,
			KernelException {
		//通过processInstance.getCreatorId()找到请假申请人
		TaskInstance taskInstance = (TaskInstance)arg0;
		IProcessInstance processInstance = taskInstance.getAliveProcessInstance();
		String applicantId = processInstance.getCreatorId();
		User applicant = OUManagementMock.getInstance().findUserById(applicantId);
		if (applicant==null){
			throw new EngineException(processInstance,taskInstance.getTask(),"没有在用户系统中找到请假申请人！");
		}

		//通过arg1找到角色中的所有人
		List usersForRole = OUManagementMock.getInstance().getAllUsersForRole(arg1);
		
		
		List usersForRoleAndDepartment = new ArrayList();//部门领导
		//从所有的部门领导中找到和申请人同一个部门的领导
		for (int i=0;i<usersForRole.size();i++){
			User user = (User)usersForRole.get(i);
			if (user.getDepartmentCode().equals(applicant.getDepartmentCode())){
				usersForRoleAndDepartment.add(user.getId());
			}
		}
		
		if (usersForRoleAndDepartment.size()==0){
			throw new EngineException(processInstance,taskInstance.getTask(),"没有符合条件的操作者，无法分配WorkItem！");
		}
	
		//分配Workitem
		arg0.asignToActors(usersForRoleAndDepartment);
	}

}
