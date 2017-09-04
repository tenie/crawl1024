package net.tenie.crawl.controller;
   
 
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import net.tenie.crawl.entity.ControllerRecord;
import net.tenie.crawl.service.MainService;
import net.tenie.crawl.tools.ApplicationContextHelper;
import net.tenie.crawl.tools.OKHttpTool;
 
 

@Controller //@RestController 等价@Controller + @ResponseBody
public class MainController { 

	@Autowired
	private OKHttpTool tool ;   
	@Autowired
	MainService service;
//	@Autowired
//	ControllerRecord record;
//	@Value("${image.save.path}")
//	private String fileSavePath;
//	private String fileSavePath = System.getProperty("user.home");
	Logger logger = LoggerFactory.getLogger(MainController.class); 
//	 String fileName = request.getSession().getServletContext().getRealPath("/");
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
		
//		http://localhost:8080/downloadZip
//		@RequestMapping("/downloadZip")
//		public String indexdownloadZip(){ 
//			return "forward:/index.html";
//		}  
		
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
		@RequestMapping(value="/downloadZip",method=RequestMethod.GET) 
		public void downloadFinishZip(HttpServletRequest request, HttpServletResponse response) throws Exception{
			try {
				ControllerRecord record = ApplicationContextHelper.getBeanByType(ControllerRecord.class);
				service.downloadFinishZip(record,response);  
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
		 
//		/**
//		 * 同步下载,  保存到指定路径
//		 * @param urlArry
//		 * @param fileName
//		 * @throws Exception
//		 */
//		private void downloadAction(String[] urlArry ,String fileName) throws Exception{
//			System.out.println("begin.....");
////			 OKHttpTool tool = 	new OKHttpTool(); 
//			//String[] urlArry =  queryParam.get("imgUrls").split("\n"); 
//			for(String url : urlArry) {
//				System.out.println("carwling= "+url);
//				Map<String, Object> rsMap; 
//					rsMap = tool.getBodyBytesAndType(url); 
//				byte[]	 imgB = (byte[]) rsMap.get("val");
//				String type = (String) rsMap.get("type"); 
//			    tool.byte2image(imgB,fileName+"/image"+new Date().getTime()+"."+tool.typeChange(type)); 
//		   }
//		
//		}
//		/**
//		 * 异步下载
//		 * @param urlArry
//		 * @param fileName
//		 * @throws Exception
//		 */
//		private void asyncDownloadAction(String[] urlArry ,String fileName) throws Exception{
//			System.out.println("begin.....");
////			 OKHttpTool tool = 	new OKHttpTool(); 
//			//String[] urlArry =  queryParam.get("imgUrls").split("\n"); 
//			//去除数组中的空字符串
//			Set<String> s = new HashSet<>();
//			Collections.addAll(s, urlArry);
//			s.remove("");
//			String[]  arr2 = new String[s.size()];
//			s.toArray(arr2);
//			BinData.setQueue(arr2.length);
//			ArrayBlockingQueue<Map<String,Object>>  queue = 	BinData.getQueue();
//			for(String url : arr2) {
//				System.out.println("carwling= "+url);
//				Map<String, Object> rsMap; 
//			    tool.asyncGetBodyByte(url,queue); 
//			  //  BinData.setIndex(BinData.getIndex()+1);
//		   }
//		   
//		   boolean tf =true;
//		   while(tf){
//			   
//			   if(queue.size()==urlArry.length){
//				   Iterator<Map<String,Object>>   it = queue.iterator();
//				   while(it.hasNext()){
//					   System.out.println("download image...");
//					   Map<String, Object> rsMap =  it.next();
//					   byte[]	 imgB = (byte[]) rsMap.get("val");
//					   String type = (String) rsMap.get("type"); 
//					   Thread.sleep(100);
//					   tool.byte2image(imgB,fileName+"/image"+new Date().getTime()+"."+tool.typeChange(type)); 
//				    }
//				   break;
//				  }else{
//					  System.out.println(queue.size());
//					  Thread.sleep(3000);
//				  }
//				  
//		   }
//		   
//		
//		}
//		
//		
//		/**
//		 * 异步下载, 输入为zip
//		 * 1. 根据url 下载图片到同步队列(集合)中,(异步操作,是一个下载队列,完成下载会把结果放入集合中)
//		 * 2. 循环判断集合是否满(根据多少个url==集合多少个元素), 集合满了开始获取集合中的元素, 进行zip打包输出到临时目录
//		 * 3. 把打包的临时zip文件全路径缓存起来,下载controller,根据这个缓存的路径名来下载
//		 * @param urlArry
//		 * @param fileName
//		 * @throws Exception
//		 */
//		private String asyncDownloadActionZip(String[] urlArry ,String fileName) throws Exception{
//			System.out.println("begin....."); 
//			String finishZIPfile = fileName+"/image"+new Date().getTime()+".zip";
//			BinData.setQueue(urlArry.length);
//			ArrayBlockingQueue<Map<String,Object>>  queue = 	BinData.getQueue();
//			for(String url : urlArry) {
//				System.out.println("carwling= "+url);
//				Map<String, Object> rsMap; 
//			    tool.asyncGetBodyByte(url,queue);  
//		   } 
//		   boolean tf =true;
//		   while(tf){
//			   
//			   if(queue.size()==urlArry.length){
//				   Iterator<Map<String,Object>>   it = queue.iterator();
//				   // 将队列中的图片byte 输出到zip中
//				    File zipFile = new File(finishZIPfile);  
//				    ZipOutputStream   zipOut = new ZipOutputStream(new FileOutputStream(zipFile)); 
//				   try {
//					   while(it.hasNext()){
//						   System.out.println("download image...");
//						   Map<String, Object> rsMap =  it.next();
//						   byte[]	 imgB = (byte[]) rsMap.get("val");
//						   String type = (String) rsMap.get("type"); 
//						   Thread.sleep(100);
//						  // tool.byte2image(imgB,fileName+"/image"+new Date().getTime()+"."+tool.typeChange(type)); 
//						   zip(zipOut, "image"+new Date().getTime()+"."+tool.typeChange(type),imgB);
//						    
//					    }
//				   } finally {
//					   zipOut.close();
//				  }
//				  
//				  
//				   break; //退出循环
//				  }else{
//					  System.out.println(queue.size());
//					  Thread.sleep(500);
//				  }
//				  
//		   }
//		   return finishZIPfile;
//		
//		}
//		
//		private void zip(ZipOutputStream  zipOut,String fileName,byte[] b) throws Exception{ 
//			zipOut.putNextEntry(new ZipEntry(fileName));
//			zipOut.write(b);
//			zipOut.flush();
//		}
//		
	 
		
		 
}

 