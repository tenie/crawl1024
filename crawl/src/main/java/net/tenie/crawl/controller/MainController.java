package net.tenie.crawl.controller;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller //@RestController 等价@Controller + @ResponseBody
public class MainController {
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
			 
			return "";  
		}
		
		
}

 