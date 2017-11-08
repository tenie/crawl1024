package net.tenie.crawl.controller;
   
 
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import net.tenie.crawl.entity.ControllerRecord;
import net.tenie.crawl.service.MainService;
import net.tenie.crawl.tools.ApplicationContextHelper;
import net.tenie.crawl.tools.DeleteFile;
import net.tenie.crawl.tools.JSUtil;
import net.tenie.crawl.tools.OKHttpTool;
 
 

@Controller //@RestController 等价@Controller + @ResponseBody 
public class MainController { 

	@Autowired
	private OKHttpTool tool ;   
	@Autowired
	MainService service;
	@Value("${password.file}")
	private String pwd;
	@Value("${dest.file}")
	private String destFile;
	
	Logger logger = LoggerFactory.getLogger(MainController.class);  
		/**
		 * 首页的配置
		 * @return
		 */
		@RequestMapping("/")
		public String index(HttpServletRequest request, HttpServletResponse response){ 
			Enumeration<String> e = request.getHeaderNames();
			while(e.hasMoreElements()) {
				String name = e.nextElement();
				System.out.println(name);
				System.out.println(request.getHeader(name));;
			}
			
			return "forward:/index.html";
		}
		 
		/**
		 * 解析单个页面中要获取的url字符串, url字符串返回给前端, 并在新的线程中下载这些url对于的图片,
		 * 1. 判断是否有认为在执行, 有任务执行中就退出
		 * 2. 获取前台传递的参数 
		 * 3. 调用API: 解析url, 获取其中的图片url,放入set集合中, 返回, 缓存
		 * 4. 调用API: 根据缓存中的图片url集, 下载每个图片, 放入到同步集合中,(此时在图片还是byte,这样可以给任何输出流使用,)
		 * 5. 把图片url集合输出到前端,前端可以看到有多少个图片需要下载了.
		 * 
		 * @param queryParam
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value="/url",method=RequestMethod.POST)
		@ResponseBody
		public Set<String> geturl(@RequestParam Map<String, String> queryParam) throws Exception{	    
			
			ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
			System.out.println("/url === "+record.toString());
			 
			System.out.println(queryParam.toString());
	        return service.singleAnalyzeUrl(record,queryParam);
		}
		
		/**
		 * 解析多个页面中的src的字符串, 
		 * 1. 判断是否有认为在执行, 有任务执行中就退出
		 * 2. 获取前台传递的参数, 把url字符串根据换行符分割为url数组
		 * 3. 调用API: 解析url, 获取其中的图片url,放入set集合中, 返回, 缓存
		 * 4. 调用API: 根据缓存中的图片url集, 下载每个图片, 放入到同步集合中,(此时在图片还是byte,这样可以给任何输出流使用,)
		 * 5. 把图片url集合输出到前端,前端可以看到有多少个图片需要下载了.
		 * 
		 * @param queryParam
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value="/urls",method=RequestMethod.POST)
		@ResponseBody
		public Set<String> geturls(@RequestParam Map<String, String> queryParam) throws Exception{  
			ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
	        return service.multiAnalyzeUrl(record, queryParam);
		}
		
		/**
		 * 图片下载, 
		 * 1. 逻辑和前面都是类似的, 首先判断是否有任务在执行
		 * 2. 根据前端传递的图片url, 根据换行符分割为数组
		 * 3. 调研下载图片到同步队列的api,完成图片(byte)的下载
		 * @param queryParam
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value="/downloadImage",method=RequestMethod.POST) 
		@ResponseBody
		public String downloadImage(@RequestParam Map<String, String> queryParam,HttpServletRequest request) throws Exception{
			ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
			return service.useImgUrlsDownload(queryParam,record);	       
		}
		
		/**
		 * 从缓存url集合中, 下载图片
		 * 目前没有用上该API
		 * @param queryParam
		 * @param request
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(value="/cacheDownload",method=RequestMethod.GET) 
		@ResponseBody
		public String cacheDownload(@RequestParam Map<String, String> queryParam,HttpServletRequest request) throws Exception{
			ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
			return service.cacheDownload(record); 	       
		}
		
		/**
		 * 查询下载图片线程是否完成 
		 * 
		 * @param queryParam
		 * @return
		 * @throws Exception 
		 */
		@RequestMapping(value="/downloadFinish",method=RequestMethod.GET) 
		@ResponseBody
		public String downloadFinish(@RequestParam Map<String, String> queryParam,HttpServletRequest request) throws Exception{ 
			ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
			System.out.println("/downloadFinish==="+record.toString());
			return service.queryAnalyzeFinish(record);
		}
		 
		/**
		 * 下载图片集合的压缩包
		 * 1. 根据缓存的临时文件.zip, 把这个文件输出给前端
		 * 2. 完成输出后, 删除临时文件
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		//TODO
		@RequestMapping(value="/downloadZip/{persist}",method=RequestMethod.GET) 
		public void downloadFinishZip(HttpServletRequest request, HttpServletResponse response ,@PathVariable(value="persist") String persist ) throws Exception{
			try {
				ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
				if(record.getAppendItem() == 0 ){ 
				}else{
					service.downloadFinishZip(record,response,persist);  
				} 
				
			} catch (Exception e) {
			    e.printStackTrace();
			}  
		}
		
		/**
		 * 中断任务
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(value="/stopRuning",method=RequestMethod.GET) 
		@ResponseBody
		public String  stopRuning(HttpServletRequest request, HttpServletResponse response) {
			try {
				ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
				Thread thread  = record.getThread();
				if(thread == null )return "根本就没有任务在运行"; 
				record.clear(); 
			} catch (Exception e) {
				e.printStackTrace(); 
				return "出错了..";
			} 
			return "任务已经暂停";  
		}
		
		/**
		 * 查询是否在运行任务
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(value="/isRuning",method=RequestMethod.GET) 
		@ResponseBody
		public String  isRuning(HttpServletRequest request, HttpServletResponse response) {
			try {
				ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
				Thread thread  = record.getThread();
				if(thread == null || ! thread.isAlive() )return "no" ; else return "yes";  
			} catch (Exception e) {
				e.printStackTrace(); 
				return "出错了..";
			} 
			 
		}
		
		/**
		 * 查询是否有文件可以下载
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(value="/hasFileCanDownload",method=RequestMethod.GET) 
		@ResponseBody
		public String  hasFileCanDownload(HttpServletRequest request, HttpServletResponse response) {
			try {
				ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
				String finishFile = record.getFinishzipFile(); 
				if(finishFile !=null && !"".equals(finishFile)) return "yes" ; else return "no";  
			} catch (Exception e) {
				e.printStackTrace(); 
				return "出错了..";
			} 
			 
		}
		
		/**
		 * 清空服务器端保留的zip缓存
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(value="/cleanPersist",method=RequestMethod.GET) 
		@ResponseBody
		public String  cleanPersist(HttpServletRequest request, HttpServletResponse response) {
			try {
				ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
				List<String> ls = record.getHistoryZip();
				System.out.println(record.getHistoryZip());
				for(String str:ls) {
					File file = new File(str);  
					DeleteFile.deleteAllFilesOfDir(file);
					
					record.setHistoryZip(new ArrayList<String>());
					System.out.println(record.getHistoryZip());
				}
				return "yes"  ;
			} catch (Exception e) {
				e.printStackTrace(); 
				return "出错了..";
			} 
			 
		}
		
		/**
		 * 拷贝文件到其他服务器
		 */
		@RequestMapping(value="/copyZipFile",method=RequestMethod.GET) 
		@ResponseBody
		public String  copyZipFileToOtherServer(HttpServletRequest request, HttpServletResponse response) {
			try {
				ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
				List<String> ls = record.getHistoryZip();
				System.out.println(record.getHistoryZip());
				for(String str:ls) {
//					JSUtil.execCommand("")
					 Thread thread = new Thread(new Runnable() {
							public void run() {
								try { 
									JSUtil.execCommand(
									"sshpass -f "+pwd+" scp "+ls+" " +destFile);
								} catch (Exception e) { 
									e.printStackTrace();	
								}finally { 
									 
								} 
						    }
						});
					 thread.start();
				}
				return "yes"  ;
			} catch (Exception e) {
				e.printStackTrace(); 
				return "出错了..";
			} 
			 
		} 
}

 