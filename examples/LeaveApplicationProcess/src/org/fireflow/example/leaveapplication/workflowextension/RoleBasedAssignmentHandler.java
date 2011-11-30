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
 * 该AssignmentHandler将TaskInstance分配给特定的角色，
 * 角色名称通过arg1参数传入。
 * @author app
 *
 */
public class RoleBasedAssignmentHandler implements IAssignmentHandler {

	@Override
	public void assign(IAssignable arg0, String arg1) throws EngineException,
			KernelException {
		TaskInstance taskInstance = (TaskInstance)arg0;
		IProcessInstance processInstance = taskInstance.getAliveProcessInstance();
		
		//通过arg1找到角色中的所有人
		List usersForRole = OUManagementMock.getInstance().getAllUsersForRole(arg1);
				
		if (usersForRole.size()==0){
			throw new EngineException(processInstance,taskInstance.getTask(),"没有符合条件的操作者，无法分配WorkItem！");
		}
		List actorIds = new ArrayList();
		for (int i=0;i<usersForRole.size();i++){
			User u = (User)usersForRole.get(i);
			actorIds.add(u.getId());
		}
		
		arg0.asignToActors(actorIds);
	}

}
