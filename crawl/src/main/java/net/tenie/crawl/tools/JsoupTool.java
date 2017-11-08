package net.tenie.crawl.tools;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Env;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
@Component
public class JsoupTool {
	
	@Value("${phantomjs.path1}") 
	private   String exePath;
	
	@Value("${script.path1}")
	private   String scriptPath;
	
	private static  String itemPath;
	
	
	
	public String getExePath() {
		return exePath;
	}
	public void setExePath(String exePath) {
		this.exePath = exePath;
	}
	public String getScriptPath() {
		return scriptPath;
	}
	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
	public static String getItemPath() {
		return itemPath;
	}
	public static void setItemPath(String itemPath) {
		JsoupTool.itemPath = itemPath;
	}

	
 
	
	
	public static void main(String[] args) throws Exception {
		 
		// Document doc = Jsoup.connect("http://t66y.com/htm_mob/8/1705/2420439.html").get();
//		Document doc =Jsoup.parse(new File("/Users/tenie/Desktop/1024.html"), "utf-8");
//		//String title = doc.title();
//		//System.out.println( doc);
//		Elements e =doc.select("input[type='image']");
//		 
//		System.out.println(e.size());
//		for(Element i :e){
//		System.out.println(i.attr("src"));
//		}
//		System.setProperty("http.proxySet", "true");
//	    System.setProperty("http.proxyHost", "127.0.0.1");
//	    System.setProperty("http.proxyPort", "6766");
//		Set rs= JsoupTool.getUrlsSet("http://t66y.com/htm_mob/7/1709/2628582.html", "input","src","false");
//		List rs=JsoupTool.getUrls("c:/Users/ten/Downloads/1024.html", "input[type='image']");
		//List rs=JsoupTool.getUrls("/Users/tenie/Desktop/1024.html", "input[type='image']");
//		System.out.println(rs);
//		System.out.println(rs.size());
		}
	private static void initItemPath(){ 
		 String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		 path.indexOf("/crawl/");
		 path =  path.substring(0,path.indexOf("/crawl/")+7); 
		  if(path.indexOf(":") >0){
			  path =path.substring(path.indexOf("/")+1); 
		  }else{
			  path =path.substring(path.indexOf("/")); 
		  }
		  itemPath = path;
	}
	private static String getCMD(String path,String isDynamic){
//		 String scriptPath= 
		JsoupTool tool =  ApplicationContextHelper.getBeanByType(JsoupTool.class); 
		String scriptPath= tool.getScriptPath();
		String exePath =tool.getExePath();
		String cmd = "";
		if(exePath.length()<3){
			   if(itemPath == null ||itemPath.length()<3){initItemPath();}
			   exePath = itemPath+"phantomjs.exe " ; 
		}
		if(scriptPath.length()<3){
			 if(itemPath == null ||itemPath.length()<3){initItemPath();}
			 scriptPath = itemPath+"script.js ";
		}
		 cmd = exePath + " " +scriptPath + " " +isDynamic + " " +path;   
		 return cmd ;
	}
	
	/**
	 * 使用shell 命令调用phantomjs命令,获取命令返回的网页信息后, 在处理成url字符串,返回给调用者
	 * 提供url或文件路径, 和要获取最终url的选择器表达式(和jquery语法类似)  获取最终的url字符串列表, 
	 * @param path
	 * @param select
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Object>  getUrlsSet(String path,String select,String attr,String isDynamic) throws Exception{
		Document doc ;
		boolean ishttp = false; 
		if("http".equals(path.substring(0, 4))){ 
			//doc = Jsoup.connect(path).get();
			ishttp=true; 
			 
			  String cmd = getCMD(path,isDynamic);	 
			  System.out.println(cmd);
			  String rs = JSUtil.execCommand(cmd);
//		      System.out.println(rs); 
		      if("error".equals(rs)){
		    	  throw new Exception("打开url失败");
		      }
		      doc= Jsoup.parse(rs);
		}else{
			doc =Jsoup.parse(new File(path), "utf-8");
		}
		String title = doc.title().replace(" ", "_");
		System.out.println("title===="+title);
		Elements es =doc.select(select);
		Set<String> rs = new HashSet<>();
		 
		for(Element element :es ){ 
			String rsStr = element.attr(attr); 
			System.out.println("result==="+rsStr);
			if(ishttp) {
				rsStr=parseUrl(rsStr,path);
			}
			rs.add(rsStr); 
		} 
		Map<String,Object> reMap = new HashMap<String,Object>();
		reMap.put("set", rs); 
		reMap.put("title",title.length()>30? title.substring(0, 30):title); //截取title过程的字符串
		return reMap; 	
	}
	
	/**
	 * 如果截取出的url是../ 或 ./  或 / 开头的对和host拼接
	 * @param url
	 * @param host
	 * @return
	 */
	public static String parseUrl(String url,String host) {
		if(null != url && url.length()>3) {
			String prefix  = url.substring(0, 2);  
			if("..".equals(prefix)) {
				//对../ 做处理
				int lastIndex = host.lastIndexOf("/");
				String subHost = host.substring(0,lastIndex);
				lastIndex = subHost.lastIndexOf("/");
				subHost = host.substring(0,lastIndex); 
				subHost+=url.substring(2);
//				System.out.println(subHost);
				return subHost;
			}else if("./".equals(prefix)){
				//对./ 做处理
				int lastIndex = host.lastIndexOf("/");
				String subHost = host.substring(0,lastIndex); 
				
				subHost+=url.substring(2);
//				System.out.println(subHost);
				return subHost;
			}else { //处理 /前缀
				String prefix2  = url.substring(0, 1);
				if("/".equals(prefix2)) {
					int lastIndex = host.lastIndexOf("/");
					String subHost = host.substring(0,lastIndex);
					lastIndex = subHost.lastIndexOf("/");
					
					String[] slpithost =  host.split("//");
					String subffixhost =  slpithost[1];
					subffixhost = subffixhost.substring(0, subffixhost.indexOf("/"));
					
					String nowHost = slpithost[0]+"//"+subffixhost;  
					subHost=nowHost + url;
//					System.out.println(subHost);
					return subHost;
				}
			}
			
			 
		}
		
		return url;
	}
}
