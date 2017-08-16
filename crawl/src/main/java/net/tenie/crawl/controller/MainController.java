package net.tenie.crawl.controller;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.tenie.crawl.tools.JsoupTool;
import net.tenie.crawl.tools.OKHttpTool;
 
 

@Controller //@RestController 等价@Controller + @ResponseBody
public class MainController {
	volatile private boolean isCrawling = false;
	
	volatile private Set<String> cache;

	@Autowired
	private OKHttpTool tool ; // = 	new OKHttpTool(); 
	@Value("${image.save.path}")
	private String fileSavePath;
	Logger logger = LoggerFactory.getLogger(MainController.class); 
		/**
		 * 首页的配置
		 * @return
		 */
		@RequestMapping("/")
		public String index(){
			
			return "forward:/index.html";
		}
		
	
		@RequestMapping("/main/callfun")
		@ResponseBody
		public  String callfun(){
			
			 System.out.println(System.getProperty("http.proxyHost"));
			return System.getProperty("http.proxyHost");  
		}
		
		/**
		 * 设置代理服务器
		 * @param host
		 * @param port
		 * @return
		 */
		@RequestMapping("/setProxy/{host}/{port}")
		@ResponseBody
		public String setProxy( @PathVariable(value="host") String host ,
				@PathVariable(value="port") String port ){
			System.out.println(host+" : "+port);
		    System.setProperty("http.proxySet", "true");
	        System.setProperty("http.proxyHost", host);
	        System.setProperty("http.proxyPort", port);
	        logger.info("代理设置成功~代理设置成功~代理设置成功~");
	        return "ok";
		}
		
		/**
		 * 获取单个页面中的src的字符串, 
		 * @param queryParam
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value="/url",method=RequestMethod.POST)
		@ResponseBody
		public Set<String> geturl(@RequestParam Map<String, String> queryParam) throws Exception{
			String url1 = queryParam.get("url1");
			String select = queryParam.get("select");
			String attr = queryParam.get("attr");
			
	        logger.info(url1 + " ; " + select);
	    	//JsoupTool jt = new JsoupTool(); 
	    	
	        Set<String> rs= JsoupTool.getUrlsSet(url1, select,attr);
	        if(cache !=null)cache.clear();
	        cache=rs;
	        return rs;
		}
		
		/**
		 * 获取多个页面中的src的字符串, 
		 * @param queryParam
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value="/urls",method=RequestMethod.POST)
		@ResponseBody
		public Set<String> geturls(@RequestParam Map<String, String> queryParam) throws Exception{
			String url1 = queryParam.get("url1");
			String select = queryParam.get("select");
			String attr = queryParam.get("attr");
			Set<String> rs = new HashSet<String>();
	        logger.info(url1 + " ; " + select);
	        
	    	//JsoupTool jt = new JsoupTool(); 
	    	String[] strArr = url1.split("\n");
	    	for(String str : strArr){
	    		rs.addAll(JsoupTool.getUrlsSet(str, select,attr));
	    	} 
	    	if(cache !=null)cache.clear();
	        cache=rs;
	        return rs;
		}
		
		/**
		 * 获取多个页面中的src的字符串, 
		 * @param queryParam
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value="/downloadImage",method=RequestMethod.POST) 
		@ResponseBody
		public String downloadImage(@RequestParam Map<String, String> queryParam,HttpServletRequest request) throws Exception{
//			 String fileName = request.getSession().getServletContext().getRealPath("/");
			 String fileName=fileSavePath;
			 Thread thread = new Thread(new Runnable() {
				public void run() {
				try {
					Thread.sleep(5000);
					OKHttpTool tool = 	new OKHttpTool(); 
					String[] urlArry =  queryParam.get("imgUrls").split("\n"); 
					for(String url :urlArry) {
						Map<String, Object> rsMap; 
							rsMap = tool.getBodyBytesAndType(url); 
						byte[]	 imgB = (byte[]) rsMap.get("val");
						String type = (String) rsMap.get("type"); 
					    tool.byte2image(imgB,fileName+"/image"+new Date().getTime()+"."+tool.typeChange(type)); 
					} 
				} catch (Exception e) { 
					e.printStackTrace();	
				}finally {
					isCrawling=false;
				}
				
			}
			});
			if(isCrawling) {
				 return "有任务在爬取中...";
			}else {
				thread.start();
				isCrawling=true;
				return "开始爬取...";
			} 
	        
	       
		}
		
		/**
		 * 查询下载图片是否完成 
		 * @param queryParam
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value="/downloadFinish",method=RequestMethod.GET) 
		@ResponseBody
		public String downloadFinish(@RequestParam Map<String, String> queryParam,HttpServletRequest request) throws Exception{
			if(isCrawling){
				return "doing";
			}else{
				return "done";
			}  
		}
		 
		
	 
}

 