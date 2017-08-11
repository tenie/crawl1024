package net.tenie.crawl.controller;
 
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		public List geturl(@RequestParam Map<String, String> queryParam) throws Exception{
			String url1 = queryParam.get("url1");
			String select = queryParam.get("select");
			
	        logger.info(url1 + " ; " + select);
	    	JsoupTool jt = new JsoupTool(); 
	    	 
	        return jt.getUrls(url1, select);
		}
		
		
		
}

 