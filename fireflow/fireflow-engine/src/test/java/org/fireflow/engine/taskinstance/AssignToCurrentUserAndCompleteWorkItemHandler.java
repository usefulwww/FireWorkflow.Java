package org.fireflow.engine.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.kernel.KernelException;

public class AssignToCurrentUserAndCompleteWorkItemHandler implements
		IAssignmentHandler {

    public static final String ACTOR_ID = "Fireflow JUnit Tester";
    public void assign(IAssignable asignable, String performerName) throws EngineException, KernelException {
        IWorkItem wi = asignable.assignToActor(ACTOR_ID);
        wi.claim();
        wi.complete();
    }


}
