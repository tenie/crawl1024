package net.tenie.crawl.tools;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class JsoupTool {
	
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
		
		Set rs= JsoupTool.getUrlsSet("https://www.tenie.net/article/18", "img","src");
//		List rs=JsoupTool.getUrls("c:/Users/ten/Downloads/1024.html", "input[type='image']");
		//List rs=JsoupTool.getUrls("/Users/tenie/Desktop/1024.html", "input[type='image']");
		System.out.println(rs);
		}
	
	/**
	 * 提供url或文件路径, 和要获取最终url的选择器表达式(和jquery语法类似)  获取最终的url字符串列表, 
	 * @param path
	 * @param select
	 * @return
	 * @throws Exception
	 */
	public static List<String> getUrls(String path,String select) throws Exception{
		Document doc ;
		boolean ishttp = false; 
		if("http".equals(path.substring(0, 4))){ 
			doc = Jsoup.connect(path).get();
			ishttp=true;
		}else{
			doc =Jsoup.parse(new File(path), "utf-8");
		}
		String title = doc.title();
		Elements es =doc.select(select);
		List<String> rs = new ArrayList<>();
		 
		for(Element element :es ){ 
			String rsStr = element.attr("src"); 
			if(ishttp) {
				rsStr=parseUrl(rsStr,path);
			}
			rs.add(rsStr);
//		System.out.println(rsStr);
		} 
	
		return rs; 	
	}
	
	/**
	 * 提供url或文件路径, 和要获取最终url的选择器表达式(和jquery语法类似)  获取最终的url字符串列表, 
	 * @param path
	 * @param select
	 * @return
	 * @throws Exception
	 */
	public static Set<String> getUrlsSet(String path,String select,String attr) throws Exception{
		Document doc ;
		boolean ishttp = false; 
		if("http".equals(path.substring(0, 4))){ 
			doc = Jsoup.connect(path).get();
			ishttp=true;
		}else{
			doc =Jsoup.parse(new File(path), "utf-8");
		}
		String title = doc.title();
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
	
		return rs; 	
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
