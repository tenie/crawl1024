package net.tenie.crawl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

 @RestController //等价@Controller + @ResponseBody
public class ProxyController {
	Logger logger = LoggerFactory.getLogger(ProxyController.class); 
	/**
	 * 设置代理服务器
	 * @param host
	 * @param port
	 * @return
	 */
	@RequestMapping("/setProxy/{host}/{port}") 
	public String setProxy( @PathVariable(value="host") String host ,
			@PathVariable(value="port") String port ){
		System.out.println(host+" : "+port);
	    System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port);
        logger.info("代理设置成功~代理设置成功~代理设置成功~");
        return "ok";
	}
	
	
	@RequestMapping("/main/callfun")
	@ResponseBody
	public  String callfun(){ 
		System.out.println(System.getProperty("http.proxyHost"));
		return System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort");  
	}
}
