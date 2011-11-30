package org.fireflow.example.workflowextension;

import java.util.List;
import java.util.Vector;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.taskinstance.IAssignable;
import org.fireflow.engine.taskinstance.IAssignmentHandler;
import org.fireflow.kernel.KernelException;
import org.fireflow.security.persistence.User;
import org.fireflow.security.persistence.UserDAO;

/**
 * 根据角色名称获得该角色的所有人员，并分配工单给这些人员。
 * @author app
 *
 */
public class RoleBasedAssignmentHandler implements IAssignmentHandler {
	UserDAO userDAO = null;
	
	public void assign(IAssignable arg0, String arg1) throws EngineException,KernelException {
		ITaskInstance taskInst = (ITaskInstance)arg0;

		String roleName = arg1==null?"":arg1.trim();
		List users = userDAO.findUsersByRoleCode(roleName);
		if (users==null || users.size()==0){
			throw new EngineException(taskInst.getProcessInstanceId(),
					taskInst.getWorkflowProcess(),taskInst.getTaskId(),"没有任何用户和角色"+arg1+"相关联，无法分配任务实例[id="+taskInst.getId()+",任务名称="+taskInst.getDisplayName());
		}
		List userIds = new Vector();
		for (int i=0;i<users.size();i++){
			userIds.add(((User)users.get(i)).getLoginid());
		}

		arg0.asignToActors(userIds);
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	
}
