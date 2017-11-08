package net.tenie.crawl.controller;
 
import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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