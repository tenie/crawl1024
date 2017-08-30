package net.tenie.crawl.entity;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.tenie.crawl.tools.DeleteFile;

@Component
@Scope("session")
public class ControllerRecord {
	volatile private boolean  isCrawling = false;  
	volatile private boolean isDownloading = false; 
	volatile private Set<String> cache ;
	private String fileSavePath = System.getProperty("user.home").replace("\\", "/");  
	volatile private String finishzipFile = "";
	private static ArrayBlockingQueue queue;
	
	private Thread thread;
	
	public void clear() {
		if(thread !=null ) {
			thread.interrupt();
			thread=null;
		}
		if( finishzipFile!=null && !"".equals(finishzipFile)) {
			File file = new File(finishzipFile);   
		    if(file.exists() && file.isFile() && file.canRead()) { 
		    	DeleteFile.deleteAllFilesOfDir(file);
			} 
		}
		finishzipFile = "";
		String fileSavePath = System.getProperty("user.home").replace("\\", "/");  
		cache=null;
		isCrawling = false;  
		isDownloading = false; 
	}
	
	
	
	public Thread getThread() {
		return thread;
	}
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	public static ArrayBlockingQueue getQueue() {
		return queue;
	}
	public static void setQueue(ArrayBlockingQueue queue) {
		ControllerRecord.queue = queue;
	}
	public boolean isCrawling() {
		return isCrawling;
	}
	public void setCrawling(boolean isCrawling) {
		this.isCrawling = isCrawling;
	}
	 
	public boolean isDownloading() {
		return isDownloading;
	}
	public void setDownloading(boolean isDownloading) {
		this.isDownloading = isDownloading;
	}
	public Set<String> getCache() {
		return cache;
	}
	public void setCache(Set<String> cache) {
		this.cache = cache;
	}
	public String getFileSavePath() {
		return fileSavePath;
	}
	public void setFileSavePath(String fileSavePath) {
		this.fileSavePath = fileSavePath;
	}
	public String getFinishzipFile() {
		return finishzipFile;
	}
	public void setFinishzipFile(String finishzipFile) {
		this.finishzipFile = finishzipFile;
	}
	@Override
	public String toString() {
		return "ControllerRecord [isCrawling=" + isCrawling + ", isdownloading=" + isDownloading + ", cache=" + cache
				+ ", fileSavePath=" + fileSavePath + ", finishzipFile=" + finishzipFile + "]";
	}
	
	
	 
	
	
	
 
	
	
	
}
