package net.tenie.crawl.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置 
 * 在这配置的@Bean类似在xml中使用<bean> 来配置对象
 * @author tenie
 *
 */
@Configuration
public class FileUploadConfiguration {
	
	@Bean
	public MultipartConfigElement  multipartCongfigElement(){
			MultipartConfigFactory factory = new MultipartConfigFactory();
			/**
			 * 设置文件大小限制, 超出设置页面会抛出异常信息
			 * 这样在文件上传的地方就需要进行异常信息的处理了
			 */
			factory.setMaxFileSize("256MB"); //KB,MB
			//设置总上传数据总大小
			factory.setMaxRequestSize("512MB");
			
			//设置文件存储的目录
//			factory.setLocation("c:/");  
		return factory.createMultipartConfig();
		
	}
}
