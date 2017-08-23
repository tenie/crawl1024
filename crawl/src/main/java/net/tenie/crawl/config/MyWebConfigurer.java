package net.tenie.crawl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.tenie.crawl.interceptor.MyInterceptorl;
 


@Configuration
public class MyWebConfigurer extends WebMvcConfigurerAdapter{
	
		/**
		 * 配置默认首页
		 *  还有一种方式是写一个controller ;@RequestMap("/") ,返回字符串 "forward:/index.html"
		 */
//	   @Override
//	    public void addViewControllers( ViewControllerRegistry registry ) {
//	        registry.addViewController( "/" ).setViewName( "forward:/index.html" );
//	        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
//	        super.addViewControllers( registry );
//	    } 
	
	
	/**
	 * 把自定义的拦截器,配置到spring中去.
	 * 		  多个拦截器组成一个拦截器链
	       	  addPathPatterns 用于添加拦截规则
	          excludePathPatterns 用于排除拦截
	     注意:
	     		只要经过DispatcherServlet的请求才会走拦截器链, 我们自定义的Servlet请求是不会被拦截的,
	     		WebMvcConfigurerAdapter并非只能注册拦截器, 还有其他WEB配置用途,根据方法名自行判断
	 * @author tenie
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new MyInterceptorl()).addPathPatterns("/**");
		//registry.addInterceptor(new MyInterceptorl2()).addPathPatterns("/**");
		super.addInterceptors(registry);
	}
	
	
	/**
	 * 静态资源(图片等) 自定义目录的配置
	 * 下列中: 根据uri的匹配来映射到自己建的目录
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/myres/**").addResourceLocations("classpath:/myres/");
		 */
		// 访问myres根目录下的fengjing.jpg 的URL为 http://localhost:8080/fengjing.jpg （/** 会覆盖系统默认的配置）
		// registry.addResourceHandler("/**").addResourceLocations("classpath:/myres/").addResourceLocations("classpath:/static/");	//可以连续添加映射,前面的优先级高于后面的
	
		//使用文件系统的目录
		// 可以直接使用addResourceLocations 指定磁盘绝对路径，同样可以配置多个位置，注意路径写法需要加上file:
		//	registry.addResourceHandler("/myimgs/**").addResourceLocations("file:H:/myimgs/");
		
		//通过配置文件配置
	/**
		# 默认值为 /**
	 	spring.mvc.static-path-pattern=   //可以重新定义pattern，如修改为 /myres/** ， 只可以定义一个，目前不支持多个逗号分割的方式。
		# 默认值为 classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/ 
		spring.resources.static-locations=     //可以重新定义 pattern 所指向的路径，支持 classpath: 和 file: （上面已经做过说明） ，这里设置要指向的路径，多个使用英文逗号隔开，
		
		super.addResourceHandlers(registry);
	}*/
	
	/**
	 * 页面中的使用
	 * <body>
		    <img alt="读取默认配置中的图片" src="${pageContext.request.contextPath }/pic.jpg">
		    <br/>
		    <img alt="读取自定义配置myres中的图片" src="${pageContext.request.contextPath }/myres/fengjing.jpg">
	  </body>
	 */
}
