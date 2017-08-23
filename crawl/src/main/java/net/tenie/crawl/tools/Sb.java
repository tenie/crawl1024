package net.tenie.crawl.tools;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.stereotype.Component;

@Component
public class Sb {
	volatile private boolean isCrawling = false; 
	volatile private Set<String> cache;
	volatile private String finishzipFile = ""; 
		     private ArrayBlockingQueue<Map<String,Object>>  queue ;
	
	public boolean isCrawling() {
		return isCrawling;
	}
	public void setCrawling(boolean isCrawling) {
		this.isCrawling = isCrawling;
	}
	public Set<String> getCache() {
		return cache;
	}
	public void setCache(Set<String> cache) {
		this.cache = cache;
	}
	public String getFinishzipFile() {
		return finishzipFile;
	}
	public void setFinishzipFile(String finishzipFile) {
		this.finishzipFile = finishzipFile;
	}
	public ArrayBlockingQueue<Map<String, Object>> getQueue() {
		return queue;
	}
	public void setQueue(ArrayBlockingQueue<Map<String, Object>> queue) {
		this.queue = queue;
	}
	 
	 
}
