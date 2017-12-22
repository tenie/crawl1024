package net.tenie.crawl.controller;
 
import java.util.ArrayList;

import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qcloud.sms.SmsSingleSender;
import com.qcloud.sms.SmsSingleSenderResult;

import net.tenie.crawl.service.SendEMail;

@RestController //等价@Controller + @ResponseBody
public class TestController {
		
		@RequestMapping("/test/callfun")
		public  foo callfun(){
			foo f = new foo();
			f.setName("foo");
			f.setAge(100);
			return f;  //前端接受的是一个json
		}
		
		@RequestMapping("/test/sms")
		public  String callsms() throws Exception{
			int appid = 22;
    		String appkey = "222"; 
    		String phoneNumber2 = "22";
    		int tmplId = 22; //模版id
    		 //初始化单发
	    	SmsSingleSender singleSender = new SmsSingleSender(appid, appkey);
	    	SmsSingleSenderResult singleSenderResult;
	    	 //指定模板单发
	    	 //假设短信模板 id 为 1，模板内容为：测试短信，{1}，{2}，{3}，上学。
	    	ArrayList<String> params = new ArrayList<>();
	    	params.add("1314");
	    	params.add("10");
//	    	params.add("小明");
	    	singleSenderResult = singleSender.sendWithParam("86", phoneNumber2, tmplId, params, "", "", "");
	    	System.out.println(singleSenderResult); 
		 
			return singleSenderResult.toString();   
		}
		
		@RequestMapping("/test/mail")
		public  foo sendmail() throws MessagingException{
			SendEMail.simpleSendMail("tenie@tenie.net","tutucn@sohu.com","57523364","smtp.sohu.com","test","tetst body...");
 System.out.println("ok...");
			
			
			foo f = new foo();
			f.setName("foo");
			f.setAge(100);
			return f;  //前端接受的是一个json
		}
		
}

class foo{
	private String name;
	private Integer age;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
}