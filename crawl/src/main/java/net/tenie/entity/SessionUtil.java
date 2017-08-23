package net.tenie.entity;

import net.tenie.crawl.tools.ApplicationContextHelper;

public class SessionUtil {  
	
	public static  SessionBean getSession() {
		return  ApplicationContextHelper.getBeanByType(SessionBean.class);
	}
 
	 
	
	
	 
	
}
