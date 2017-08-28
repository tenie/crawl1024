package net.tenie.crawl.entity;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ControllerRecord {
	volatile private boolean  isCrawling = false;  
	volatile private boolean isDownloading = false; 
	volatile private Set<String> cache ;
	private String fileSavePath = System.getProperty("user.home");
	volatile private String finishzipFile = "";
	
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
