package org.fireflow.example.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.taskinstance.IAssignable;
import org.fireflow.engine.taskinstance.IAssignmentHandler;
import org.fireflow.kernel.KernelException;
import org.fireflow.security.persistence.User;
import org.springframework.security.context.SecurityContextHolder;


/**
 * 将系统当前用户设置为workitem的操作员。
 * @author 非也
 *
 */
public class CurrentUserAssignmentHandler implements IAssignmentHandler{

	public void assign(IAssignable arg0, String arg1) throws EngineException,KernelException {
		User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		//将当前用户设置为操作员
		IWorkItem wi = arg0.asignToActor(currentUser.getLoginid());
		
		//假设在目前的两个example中都规定：首个环节信息录入后，立即提交到下一个环节。
		//则需要对新创建的WorkItem执行 claim()和complete()操作。
		wi.claim();
		wi.complete();
	}
}
