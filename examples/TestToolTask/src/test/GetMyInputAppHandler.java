package test;

import java.io.File;
import java.io.IOException;

public class GetMyInputAppHandler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec("cmd /c start D:\\MyProject\\callbat\\getInput.bat");
			File inputFile = new File("D:\\MyProject\\callbat\\getInput.txt");
			java.io.FileInputStream fIn = new java.io.FileInputStream(inputFile);
//			fIn.read(b)
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
