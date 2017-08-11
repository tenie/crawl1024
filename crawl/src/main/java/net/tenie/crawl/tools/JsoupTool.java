package net.tenie.crawl.tools;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		
		List rs=JsoupTool.getUrls("https://www.tenie.net/article/18", "img");
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
		 
		if("http".equals(path.substring(0, 4))){
			doc = Jsoup.connect(path).get();
		}else{
			doc =Jsoup.parse(new File(path), "utf-8");
		}
		String title = doc.title();
		Elements e =doc.select(select);
		List<String> rs = new ArrayList<>();
		 
		for(Element i :e ){ 
			rs.add(e.attr("src"));
		System.out.println(e.attr("src"));
		} 
	
		return rs; 
	}
}
