package org.fireflow.example.testtooltask.applicationhandler;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.taskinstance.IApplicationHandler;

public class ErrorHandler implements IApplicationHandler{

	public void execute(ITaskInstance arg0) {

		System.out.println("This is ErrorHandler::outputType数据不正确，打印错误信息....");
		IProcessInstance processInstance = ((TaskInstance)arg0).getAliveProcessInstance();
		String outputType = (String)processInstance.getProcessInstanceVariable("outputType");
		System.out.println("错误的outputType值是："+outputType);
	}

}
