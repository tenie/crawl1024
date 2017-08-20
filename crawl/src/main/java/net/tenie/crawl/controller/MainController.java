package net.tenie.crawl.controller;
  
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream; 
import java.io.OutputStream;
import java.net.URLEncoder; 
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import net.tenie.crawl.tools.DeleteFile;
import net.tenie.crawl.tools.JsoupTool;
import net.tenie.crawl.tools.OKHttpTool;
import net.tenie.entity.BinData;
 
 

@Controller //@RestController 等价@Controller + @ResponseBody
public class MainController {
	volatile private boolean isCrawling = false;
	
	volatile private Set<String> cache;
	volatile private String finishzipFile = "";

	@Autowired
	private OKHttpTool tool ; // = 	new OKHttpTool(); 
	@Value("${image.save.path}")
	private String fileSavePath;
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
			Set<String> rs = new HashSet<String>();
			if(isCrawling){
				rs.add("有任务在执行中, 请稍后...");
				return rs;
			} 
			String url1 = queryParam.get("url1");
			String select = queryParam.get("select");
			String attr = queryParam.get("attr");
			
	        logger.info(url1 + " ; " + select); 
	    	
	        rs= JsoupTool.getUrlsSet(url1, select,attr);
	        if(cache !=null)cache.clear();
	        cache=rs;
	        //下载图片
	         String fileName=fileSavePath;
			 String[] urlArry ;
			 if(cache!=null && !cache.isEmpty()){
				 urlArry = new String[cache.size()];
				 cache.toArray(urlArry);  
			 }else{
				 throw new RuntimeException("没人东西可爬取");
			 }  
			 Thread thread = new Thread(new Runnable() {
				public void run() {
					try { 
						finishzipFile = asyncDownloadActionZip(urlArry,fileName); 
						//asyncDownloadAction(urlArry,fileName); 
						//downloadAction(urlArry,fileName); 
					} catch (Exception e) { 
						e.printStackTrace();	
					}finally {
						isCrawling=false;
					} 
			    }
			});
			if(! isCrawling) {
				thread.start();
				isCrawling=true;
			} 
	        
	        return rs;
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
			Set<String> rs = new HashSet<String>();
			if(isCrawling){
				rs.add("有任务在执行中, 请稍后...");
				return rs;
			} 
			String url1 = queryParam.get("url1");
			String select = queryParam.get("select");
			String attr = queryParam.get("attr");
			 
	        logger.info(url1 + " ; " + select);
	         
	    	String[] strArr = url1.split("\n");
	    	for(String str : strArr){
	    		rs.addAll(JsoupTool.getUrlsSet(str, select,attr));
	    	} 
	    	if(cache !=null)cache.clear();
	        cache=rs;
	        //下载图片
	         String fileName=fileSavePath;
			 String[] urlArry ;
			 if(cache!=null && !cache.isEmpty()){
				 urlArry = new String[cache.size()];
				 cache.toArray(urlArry);  
			 }else{
				 throw new RuntimeException("没人东西可爬取");
			 }  
			 Thread thread = new Thread(new Runnable() {
				public void run() {
					try { 
						finishzipFile = asyncDownloadActionZip(urlArry,fileName); 
						//asyncDownloadAction(urlArry,fileName); 
						//downloadAction(urlArry,fileName); 
					} catch (Exception e) { 
						e.printStackTrace();	
					}finally {
						isCrawling=false;
					} 
			    }
			});
			if(! isCrawling) {
				thread.start();
				isCrawling=true;
			} 
	        
	        return rs;
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
//			 String fileName = request.getSession().getServletContext().getRealPath("/");
			
			if(isCrawling){ 
				return "任务在执行中, 请稍后... ";
			}
			String fileName=fileSavePath;
			 String[] urlArry =  queryParam.get("imgUrls").split("\n");  
			 
			 //设置线程
			 Thread thread = new Thread(new Runnable() {
				public void run() {
				try {
//					Thread.sleep(2000);
					finishzipFile = asyncDownloadActionZip(urlArry,fileName); 
//					asyncDownloadAction(urlArry,fileName); 
					//downloadAction(urlArry,fileName); 
				} catch (Exception e) { 
					e.printStackTrace();	
				}finally {
					isCrawling=false;
				} 
			}
			});
			if(isCrawling) {
				 return "有任务在爬取中...";
			}else {
				//启动线程
				thread.start();
				isCrawling=true;  
				return "开始爬取...";
			}  	       
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
//			 String fileName = request.getSession().getServletContext().getRealPath("/");
			 String fileName=fileSavePath;
			 String[] urlArry ;
			 if(cache!=null && !cache.isEmpty()){
				 urlArry = new String[cache.size()];
				 cache.toArray(urlArry);  
			 }else{
				 return "没人东西可爬取";
			 }  
			 Thread thread = new Thread(new Runnable() {
				public void run() {
				try {
//					Thread.sleep(2000);
					finishzipFile = asyncDownloadActionZip(urlArry,fileName); 
					//asyncDownloadAction(urlArry,fileName); 
					//downloadAction(urlArry,fileName); 
				} catch (Exception e) { 
					e.printStackTrace();	
				}finally {
					isCrawling=false;
				} 
			}
			});
			if(isCrawling) {
				 return "有任务在爬取中...";
			}else {
				thread.start();
				isCrawling=true;
				return "开始爬取...";
			}  	       
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
			if(isCrawling && "".equals(finishzipFile)){
				return "doing";
			}else{
				return "done";
			}  
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
			 //finishzipFile
			if(finishzipFile ==null && "".equals(finishzipFile))return;
			File file = new File(finishzipFile);   
	        //判断文件是否存在如果不存在就返回默认图标  
	        if(file.exists() && file.isFile() && file.canRead()) { 
	        	System.out.println("/downloadZip.......");
		        FileInputStream inputStream = new FileInputStream(file);  
		        byte[] data = new byte[(int)file.length()];  
		        int length = inputStream.read(data);  
		        inputStream.close();  
		        //编码否则中文浏览器不显示
		        String downloadFilename = "image.zip";
		        downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");  
//		        response.setContentType("image/png");   //这个浏览器会直接现实图片,所以改为下面的配置,可以实现下载 
		        // 写明要下载的文件的大小 
		        response.setContentLength((int) file.length());  //这个不写也没问题
		        response.setHeader("Content-Disposition", "attachment;filename="+downloadFilename);
		        response.setContentType("application/octet-stream");
		        OutputStream stream = response.getOutputStream();  
		        stream.write(data);  
		        stream.flush();  
		        stream.close(); 
		        finishzipFile = "";
		        DeleteFile.deleteAllFilesOfDir(file);
		        System.out.println("finishzipFile=="+finishzipFile);
	        }   
	       
		}
		 
		/**
		 * 同步下载,  保存到指定路径
		 * @param urlArry
		 * @param fileName
		 * @throws Exception
		 */
		private void downloadAction(String[] urlArry ,String fileName) throws Exception{
			System.out.println("begin.....");
//			 OKHttpTool tool = 	new OKHttpTool(); 
			//String[] urlArry =  queryParam.get("imgUrls").split("\n"); 
			for(String url : urlArry) {
				System.out.println("carwling= "+url);
				Map<String, Object> rsMap; 
					rsMap = tool.getBodyBytesAndType(url); 
				byte[]	 imgB = (byte[]) rsMap.get("val");
				String type = (String) rsMap.get("type"); 
			    tool.byte2image(imgB,fileName+"/image"+new Date().getTime()+"."+tool.typeChange(type)); 
		   }
		
		}
		/**
		 * 异步下载
		 * @param urlArry
		 * @param fileName
		 * @throws Exception
		 */
		private void asyncDownloadAction(String[] urlArry ,String fileName) throws Exception{
			System.out.println("begin.....");
//			 OKHttpTool tool = 	new OKHttpTool(); 
			//String[] urlArry =  queryParam.get("imgUrls").split("\n"); 
			BinData.setQueue(urlArry.length);
			ArrayBlockingQueue<Map<String,Object>>  queue = 	BinData.getQueue();
			for(String url : urlArry) {
				System.out.println("carwling= "+url);
				Map<String, Object> rsMap; 
			    tool.asyncGetBodyByte(url,queue); 
			  //  BinData.setIndex(BinData.getIndex()+1);
		   }
		   
		   boolean tf =true;
		   while(tf){
			   
			   if(queue.size()==urlArry.length){
				   Iterator<Map<String,Object>>   it = queue.iterator();
				   while(it.hasNext()){
					   System.out.println("download image...");
					   Map<String, Object> rsMap =  it.next();
					   byte[]	 imgB = (byte[]) rsMap.get("val");
					   String type = (String) rsMap.get("type"); 
					   Thread.sleep(100);
					   tool.byte2image(imgB,fileName+"/image"+new Date().getTime()+"."+tool.typeChange(type)); 
				    }
				   break;
				  }else{
					  System.out.println(queue.size());
					  Thread.sleep(3000);
				  }
				  
		   }
		   
		
		}
		
		
		/**
		 * 异步下载, 输入为zip
		 * 1. 根据url 下载图片到同步队列(集合)中,(异步操作,是一个下载队列,完成下载会把结果放入集合中)
		 * 2. 循环判断集合是否满(根据多少个url==集合多少个元素), 集合满了开始获取集合中的元素, 进行zip打包输出到临时目录
		 * 3. 把打包的临时zip文件全路径缓存起来,下载controller,根据这个缓存的路径名来下载
		 * @param urlArry
		 * @param fileName
		 * @throws Exception
		 */
		private String asyncDownloadActionZip(String[] urlArry ,String fileName) throws Exception{
			System.out.println("begin....."); 
			String finishZIPfile = fileName+"/image"+new Date().getTime()+".zip";
			BinData.setQueue(urlArry.length);
			ArrayBlockingQueue<Map<String,Object>>  queue = 	BinData.getQueue();
			for(String url : urlArry) {
				System.out.println("carwling= "+url);
				Map<String, Object> rsMap; 
			    tool.asyncGetBodyByte(url,queue);  
		   } 
		   boolean tf =true;
		   while(tf){
			   
			   if(queue.size()==urlArry.length){
				   Iterator<Map<String,Object>>   it = queue.iterator();
				   // 将队列中的图片byte 输出到zip中
				    File zipFile = new File(finishZIPfile);  
				    ZipOutputStream   zipOut = new ZipOutputStream(new FileOutputStream(zipFile)); 
				   try {
					   while(it.hasNext()){
						   System.out.println("download image...");
						   Map<String, Object> rsMap =  it.next();
						   byte[]	 imgB = (byte[]) rsMap.get("val");
						   String type = (String) rsMap.get("type"); 
						   Thread.sleep(100);
						  // tool.byte2image(imgB,fileName+"/image"+new Date().getTime()+"."+tool.typeChange(type)); 
						   zip(zipOut, "image"+new Date().getTime()+"."+tool.typeChange(type),imgB);
						    
					    }
				   } finally {
					   zipOut.close();
				  }
				  
				  
				   break; //退出循环
				  }else{
					  System.out.println(queue.size());
					  Thread.sleep(500);
				  }
				  
		   }
		   return finishZIPfile;
		
		}
		
		private void zip(ZipOutputStream  zipOut,String fileName,byte[] b) throws Exception{ 
			zipOut.putNextEntry(new ZipEntry(fileName));
			zipOut.write(b);
			zipOut.flush();
		}
		
		
		 
}

 