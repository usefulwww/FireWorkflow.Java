package org.fireflow;

import org.fireflow.engine.RuntimeContext;
import org.operamasks.faces.annotation.ManagedProperty;
import org.springframework.transaction.support.TransactionTemplate;

public class BasicManagedBean {

	public static final String SELF_VIEW = "SELF_VIEW";
	
	@ManagedProperty(value="#{runtimeContext}")
	protected transient RuntimeContext workflowRuntimeContext = null;
	
	@ManagedProperty(value="#{transactionTemplate}")
	protected transient TransactionTemplate transactionTemplate = null;
}
