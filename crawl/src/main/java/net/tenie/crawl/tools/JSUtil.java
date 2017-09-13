package net.tenie.crawl.tools;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import org.springframework.util.ClassUtils;
import org.xml.sax.SAXException;

public class JSUtil {
	  
	 public static String  execCommand(String command) throws IOException{
		 Runtime rt = Runtime.getRuntime();  
		 Process p = rt.exec(command);
		  InputStream is = p.getInputStream();  
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));  
		    StringBuffer sbf = new StringBuffer();  
		    String tmp = "";  
		    while ((tmp = br.readLine()) != null){  
		       sbf.append(tmp+"\n");   
		   } 
		  return sbf.toString();   
	  }
 }   