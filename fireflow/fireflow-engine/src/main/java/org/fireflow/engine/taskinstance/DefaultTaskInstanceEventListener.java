package org.fireflow.engine.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.event.ITaskInstanceEventListener;
import org.fireflow.engine.event.TaskInstanceEvent;

public class DefaultTaskInstanceEventListener implements
		ITaskInstanceEventListener {


	/* (non-Javadoc)
	 * @see org.fireflow.engine.event.ITaskInstanceEventListener#onTaskInstanceEventFired(org.fireflow.engine.event.TaskInstanceEvent)
	 */
	public void onTaskInstanceEventFired(TaskInstanceEvent e) throws EngineException {
		IWorkflowSession session = e.getWorkflowSession();
		IProcessInstance proceInst = e.getProcessInstance();
		ITaskInstance taskInst = (ITaskInstance)e.getSource();
		IWorkItem wi = e.getWorkItem();
		if (e.getEventType()==TaskInstanceEvent.BEFORE_TASK_INSTANCE_START){
			beforeTaskInstanceStart(session,proceInst,taskInst);
		}else if (e.getEventType()==TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE){
			afterTaskInstanceCompleted(session,proceInst,taskInst);
		}
		else if (e.getEventType()==TaskInstanceEvent.AFTER_WORKITEM_CREATED){
			afterWorkItemCreated(session,proceInst,taskInst,wi);
		}else if (e.getEventType()==TaskInstanceEvent.AFTER_WORKITEM_COMPLETE){
			afterWorkItemComplete(session,proceInst,taskInst,wi);
		}

	}
	
	protected void  beforeTaskInstanceStart(IWorkflowSession currentSession,
				IProcessInstance processInstance,ITaskInstance taskInstance)throws EngineException{
		
	}
	protected void  afterTaskInstanceCompleted(IWorkflowSession currentSession,
			IProcessInstance processInstance,ITaskInstance taskInstance)throws EngineException{
		
	}
    protected void afterWorkItemCreated(IWorkflowSession currentSession,
			IProcessInstance processInstance,ITaskInstance taskInstance,IWorkItem workItem)throws EngineException{
    	
    }
    
    protected void afterWorkItemComplete(IWorkflowSession currentSession,
			IProcessInstance processInstance,ITaskInstance taskInstance,IWorkItem workItem)throws EngineException{
//    	System.out.println("---------------------------------after workitem complete!!!!!!!!!!!!!!");
    }    
}
