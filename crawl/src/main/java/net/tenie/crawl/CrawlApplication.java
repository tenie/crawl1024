package net.tenie.crawl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@ServletComponentScan 
@SpringBootApplication
public class CrawlApplication  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CrawlApplication.class, args);
	}
	
	@Bean //session过期时间
	public EmbeddedServletContainerCustomizer containerCustomizer(){
	       return new EmbeddedServletContainerCustomizer() {
	           @Override
	           public void customize(ConfigurableEmbeddedServletContainer Container) {
	        	   Container.setSessionTimeout(1800);//单位为S
	          }
	    };
	}
}
