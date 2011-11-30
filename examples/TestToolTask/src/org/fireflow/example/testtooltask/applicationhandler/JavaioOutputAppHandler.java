package org.fireflow.example.testtooltask.applicationhandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.taskinstance.IApplicationHandler;

public class JavaioOutputAppHandler implements IApplicationHandler {

	public void execute(ITaskInstance arg0) {

		System.out.println("This is JavaioOutputApphandler::模拟调用java程序");

		try {
			File f = new File("output.txt");
			FileOutputStream fout = new FileOutputStream(f);
			String s = "Hello World From Java.io!";

			fout.write(s.getBytes());
			fout.flush();
			fout.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
