package org.fireflow.example.testtooltask.applicationhandler;

import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.taskinstance.IApplicationHandler;

public class BatOutputAppHandler implements IApplicationHandler{

	public void execute(ITaskInstance arg0) {

		System.out.println("This is BatOutputAppHandler::模拟调用bat文件，并输入参数。");
		
		
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec("output.bat Hello_World_From_Bat");	//cmd /c start D:\\MyProject\\callbat\\test.bat
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

}
