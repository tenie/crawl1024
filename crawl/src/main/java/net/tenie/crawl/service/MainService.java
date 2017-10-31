package net.tenie.crawl.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.tenie.crawl.entity.ControllerRecord;

public interface MainService {
	Set<String>  singleAnalyzeUrl(ControllerRecord record,Map<String, String> queryParam) throws Exception;
	Set<String>  multiAnalyzeUrl(ControllerRecord record,Map<String, String> queryParam) throws Exception;
	String queryAnalyzeFinish(ControllerRecord record) ;
	String cacheImgUrlsDownload(ControllerRecord record);
	void downloadFinishZip(ControllerRecord record,HttpServletResponse response,String persist)throws IOException ;
	String useImgUrlsDownload(Map<String, String> queryParam,ControllerRecord record);
	
	String cacheDownload(ControllerRecord record);
}
