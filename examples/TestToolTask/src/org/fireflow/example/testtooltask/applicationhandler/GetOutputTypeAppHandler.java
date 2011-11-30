package org.fireflow.example.testtooltask.applicationhandler;

import java.io.IOException;
import java.util.Properties;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.taskinstance.IApplicationHandler;

public class GetOutputTypeAppHandler implements IApplicationHandler{

	public void execute(ITaskInstance arg0) {
		System.out.println("This is GetOutputTypeAppHandler::模拟调用bat文件，并取得返回值；根据返回值决定路由。");		
		
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec("getOutputType.bat");	//cmd /c start D:\\MyProject\\callbat\\test.bat
			
			Properties properties = new Properties();
			properties.load(p.getInputStream());

			String outputType = properties.getProperty("outputType");
			
			IProcessInstance processInstance = ((TaskInstance)arg0).getAliveProcessInstance();
			processInstance.setProcessInstanceVariable("outputType", outputType);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
