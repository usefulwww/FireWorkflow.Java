package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BatCaller {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec("D:\\MyProject\\callbat\\getInput.bat");	//cmd /c start D:\\MyProject\\callbat\\test.bat
			
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line=null;

			while((line=input.readLine()) != null) {
			System.out.println(line);
			}
	
			//Method waitFor() will make the current thread to wait until 
			//the external program finish and return the exit value to 
			//the waited thread.
			System.out.println("Exit Value = " + p.waitFor());
	
			
			System.out.println("End....");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
