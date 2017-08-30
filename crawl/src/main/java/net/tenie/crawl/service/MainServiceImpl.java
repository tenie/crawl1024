package net.tenie.crawl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import net.tenie.crawl.entity.BinData;
import net.tenie.crawl.entity.ControllerRecord;
import net.tenie.crawl.tools.DeleteFile;
import net.tenie.crawl.tools.JsoupTool;
import net.tenie.crawl.tools.OKHttpTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse; 


@Service
public class MainServiceImpl implements MainService{
	Logger logger = LoggerFactory.getLogger(MainServiceImpl.class); 
	
		@Autowired
		private OKHttpTool tool ;
		 
		private static <T>  List<T> trimArray(T[] t){ 
			Set<T> set = new HashSet<T>(); 
			Collections.addAll(set, t);
			set.remove("");  
			return new ArrayList(set);
		}
		
		public static void main(String[] args) throws  Exception {
//			String[]  arr = {"","aa","","bb"};
//			String[]  arr2 =   (String[]) trimArray(arr).toArray();
//			System.out.println(Arrays.toString(arr2));;
			
			SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//			simpledateformat.applyPattern("yyyy-MM-dd_HH:mm:ss");
			System.out.println(simpledateformat.format(new Date()));
			
			//C:/Users/ten/Downloads/image2017-08-28_16-31-39.zip
			  File zipFile = new File("C:/Users/ten/Downloads/image2017-08-28_16-31-39.zip");  
			   ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile)); 
			   byte[]	 imgB  = {1,2,3,4};
			   for(int i = 0 ;i<5;i++) {
				   zip(zipOut, "test.test"+i,imgB); 
			   }
			   
			   zipOut.close();
		}
		
		/**
		 * 异步下载, 输出.zip文件到指定目录
		 * 1. 根据url 下载图片到同步队列(集合)中,(异步操作,是一个下载队列,完成下载会把结果放入集合中)
		 * 2. 循环判断集合是否满(根据多少个url==集合多少个元素), 集合满了开始获取集合中的元素, 进行zip打包输出到临时目录
		 * 3. 把打包的临时zip文件全路径缓存起来,下载controller,根据这个缓存的路径名来下载
		 * @param urlArry
		 * @param fileName 压缩文件全路径
		 * @throws Exception
		 */
		private String asyncDownloadActionZip(String[] urlArry ,String fileName) throws Exception{
			System.out.println("begin....."+ System.getProperty("http.proxyHost")+":"+System.getProperty("http.proxyPort")); 
			
			System.out.println(Arrays.toString(urlArry));   
			int arrSize = urlArry.length;
			//图片下载
			ArrayBlockingQueue<Map<String,Object>>  queue =  new ArrayBlockingQueue<Map<String,Object>>(arrSize);
			for(String url : urlArry) {
				System.out.println("carwling= "+url);
				Map<String, Object> rsMap; 
			    tool.asyncGetBodyByte(url,queue);  
		   } 
		   //图片保存到硬盘 
		   String finishZIPfile = fileName+"/image_"+new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())+".zip";  
		   File zipFile = new File(finishZIPfile); 
		   ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile)); 
		   try { 
			   for(int i=0; i<arrSize; i++) { 
				   System.out.println("队列中获取"+i);
				   System.out.println(queue.size());
				// 将队列中的图片byte 输出到zip中 
				    Map<String, Object> rsMap =  queue.take();
				    byte[]	 imgB = (byte[]) rsMap.get("val");
				    String type = (String) rsMap.get("type");  
				    zip(zipOut, "image"+new Date().getTime()+"."+tool.typeChange(type),imgB); 
				    System.out.println("结束队列中获取"+i);
			   }  
		   } finally {
			   if( zipOut !=null)
			   zipOut.close(); 
		   } 
		   return finishZIPfile; 
		}
		
		private static void zip(ZipOutputStream  zipOut,String fileName,byte[] b) throws Exception{  
			zipOut.putNextEntry(new ZipEntry(fileName));
			zipOut.write(b);
			zipOut.flush();
		}
		
		/**
		 * 处理客户端传递的单个页面的url
		 */
		@Override
		public Set<String>  singleAnalyzeUrl(ControllerRecord record,  Map<String, String> queryParam) throws Exception {
			Set<String> rs = new HashSet<String>();
			if(record.isCrawling()){
				rs.add("有任务在执行中, 请稍后..."); 
				return rs;
			} 
			String url1 = queryParam.get("url1");
			String select = queryParam.get("select");
			String attr = queryParam.get("attr");
			 
	    	//解析html
	        rs= JsoupTool.getUrlsSet(url1, select,attr);
	        Set<String>  cache=  record.getCache();
	        if(cache !=null)cache.clear();
 	        cache=rs;
	        record.setCache(rs);
	        
	        //下载图片
	         String fileName=record.getFileSavePath();
			 String[] urlArry ;
			 if(cache!=null && !cache.isEmpty()){
				 urlArry = new String[cache.size()];
				 cache.toArray(urlArry);  
			 }else{
				 throw new RuntimeException("没人东西可爬取");
			 }  
			 //线程下载图片
			 Thread thread = new Thread(new Runnable() {
				public void run() {
					try { 
//						Thread.sleep(25000);
						record.setCrawling(true);
						record.setDownloading(true);
						record.setFinishzipFile( asyncDownloadActionZip(urlArry,fileName)); 
					} catch (Exception e) { 
						e.printStackTrace();	
					}finally {
						record.setCrawling(false);
						record.setDownloading(false);
						System.out.println("singleAnalyzeUrl === "+record.toString()); 
					} 
			    }
			});
			if(! record.isCrawling()) {
				 System.out.println("tets==="+ record.isCrawling()); 
				 record.setCrawling(true); 
				 record.setThread(thread);
				 thread.start();
				 System.out.println("tets2==="+ record.isCrawling());
			}
			return rs; 
	       
		}
		/**
		 * 处理客户端传递的多个页面url
		 */
		@Override
		public Set<String> multiAnalyzeUrl(ControllerRecord record, Map<String, String> queryParam) throws Exception {
			Set<String> rs = new HashSet<String>();
			if(record.isCrawling()){
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
	    	Set<String> cache =record.getCache();
	    	if(cache !=null)cache.clear();
	        cache=rs;
	        record.setCache(rs);
	        //下载图片
	         String fileName=record.getFileSavePath();
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
						record.setFinishzipFile(asyncDownloadActionZip(urlArry,fileName)); 
					} catch (Exception e) { 
						e.printStackTrace();	
					}finally { 
						record.setCrawling(false);
						
					} 
			    }
			});
			if(! record.isCrawling()) {
				 record.setCrawling(true);
				 record.setThread(thread);
				 thread.start(); 
			} 
			return rs;
		}
		@Override
		public String queryAnalyzeFinish(ControllerRecord record) {
			if(record.isCrawling() || record.isDownloading() ){
				return "doing";
			}else{
				return "done";
			}   
		}
		@Override
		public String cacheImgUrlsDownload(ControllerRecord record) {
			 if(record.isCrawling()) {
				 return "有任务在爬取中...";
			 }
			 
			 String fileName=record.getFileSavePath();
			 String[] urlArry ;
			 Set<String> cache = record.getCache();
			 if(cache!=null && !cache.isEmpty()){
				 urlArry = new String[cache.size()];
				 cache.toArray(urlArry);  
			 }else{
				 return "没人东西可爬取";
			 }  
			 
			 Thread thread = new Thread(new Runnable() {
				public void run() {
				try { 
					record.setFinishzipFile(asyncDownloadActionZip(urlArry,fileName)); 
				} catch (Exception e) { 
					e.printStackTrace();	
				}finally {
					record.setCrawling(false);
					
				} 
			}
			});
			    record.setCrawling(true);
			    record.setThread(thread);
				thread.start();  
				return "开始爬取..."; 
		}
		/**
		 * 把下载的  图片.zip包   传递给客户端
		 */
		@Override
		public void downloadFinishZip(ControllerRecord record,HttpServletResponse response) throws IOException {
		    String finishzipFile = record.getFinishzipFile();
			
			if(finishzipFile == null || "".equals(finishzipFile)){ 
				throw new RuntimeException("no file"); 
			} 
			File file = new File(finishzipFile);   
	        //判断文件是否存在如果不存在就返回默认图标  
	        if(file.exists() && file.isFile() && file.canRead()) { 
	        	System.out.println("/downloadZip.......");
		        FileInputStream inputStream = new FileInputStream(file);  
		        byte[] data = new byte[(int)file.length()];  
		        int length = inputStream.read(data);  
		        inputStream.close();  
		        //编码否则中文浏览器不显示
		        String downloadFilename = "image"+new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())+".zip";
		        downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");  
//			        response.setContentType("image/png");   //这个浏览器会直接现实图片,所以改为下面的配置,可以实现下载 
		        // 写明要下载的文件的大小 
		        response.setContentLength((int) file.length());  //这个不写也没问题
		        response.setHeader("Content-Disposition", "attachment;filename="+downloadFilename);
		        response.setContentType("application/octet-stream");
		        OutputStream stream = response.getOutputStream();  
		        stream.write(data);  
		        stream.flush();  
		        stream.close(); 
		        record.setFinishzipFile(""); 
		        DeleteFile.deleteAllFilesOfDir(file);
		        System.out.println("finishzipFile=="+finishzipFile);
		    } 
			
		}
		
		/**
		 * 使用前端传递的url来下载
		 */
		@Override
		public String useImgUrlsDownload(Map<String, String> queryParam, ControllerRecord record) {
			
			if(record.isCrawling() || record.isDownloading()){ 
				return "任务在执行中, 请稍后... ";
			}
			 String fileName=record.getFileSavePath();
			 String[] urlArry =  queryParam.get("imgUrls").split("\n");  
			 
			 //设置线程
			 Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						
						record.setFinishzipFile(asyncDownloadActionZip(urlArry,fileName)); 
					} catch (Exception e) { 
						e.printStackTrace();	
					}finally {
						record.setCrawling(false); 
					} 
			    }
			}); 
				//启动线程
			 	record.setThread(thread);
				thread.start();
				record.setCrawling(true); 
				return "开始爬取...";
		}
		
		/**
		 * 从record缓存中获取,目前没有用上
		 */
		@Override
		public String cacheDownload(ControllerRecord record) {
			if(record.isCrawling()) {
				 return "有任务在爬取中...";
			}
			 String fileName=record.getFileSavePath();
			 String[] urlArry ;
			 Set<String> cache = record.getCache();
			 if(cache!=null && !cache.isEmpty()){
				 urlArry = new String[cache.size()];
				 cache.toArray(urlArry);  
			 }else{
				 return "没人东西可爬取";
			 }  
			 Thread thread = new Thread(new Runnable() {
				public void run() {
				try { 
					record.setFinishzipFile(asyncDownloadActionZip(urlArry,fileName));
				} catch (Exception e) { 
					e.printStackTrace();	
				}finally {
					record.setCrawling(false); 
				} 
			}
			});
			 	record.setCrawling(true);
			 	record.setThread(thread);
				thread.start(); 
				return "开始爬取...";
			 
		}
	
}
