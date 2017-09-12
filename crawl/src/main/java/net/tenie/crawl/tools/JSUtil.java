package net.tenie.crawl.tools;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import org.springframework.util.ClassUtils;
import org.xml.sax.SAXException;

public class JSUtil { 
		      
		    
public static void main(String[] args) throws IOException, SAXException {
	 
	  String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
	  System.out.println(path);
//	  path = path.substring(1)+"static/";  
//	  String cmd = path+"phantomjs.exe "+path+"foo.js";
//	  System.out.println(cmd);
//	  String s = execCommand(cmd);
//      System.out.println(s); 
}  

	 // 调用phantomjs程序，并传入js文件，并通过流拿回需要的数据。  
	  public static String getParseredHtml2(String url ,String cmd) throws IOException{  
		    Runtime rt = Runtime.getRuntime();  
//		    Process p = rt.exec(exePath + " " + jsPath + " " + url);  
		    Process p = rt.exec(cmd);  
		    InputStream is = p.getInputStream();  
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));  
		    StringBuffer sbf = new StringBuffer();  
		    String tmp = ""; 
		    int i  = 0 ;
		    while ((tmp = br.readLine()) != null){  
		       sbf.append(tmp+"\n");  
		       i++;
		   }
		  System.out.println(i); 
		  return sbf.toString();  
	 }  
	  
	  
	 public static String  execCommand(String command) throws IOException{
		 Runtime rt = Runtime.getRuntime();  
		 Process p = rt.exec(command);
		  InputStream is = p.getInputStream();  
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));  
		    StringBuffer sbf = new StringBuffer();  
		    String tmp = ""; 
		    int i  = 0 ;
		    while ((tmp = br.readLine()) != null){  
		       sbf.append(tmp+"\n");  
		       i++;
		   }
		  System.out.println(i); 
		  return sbf.toString();   
	  }
 }   