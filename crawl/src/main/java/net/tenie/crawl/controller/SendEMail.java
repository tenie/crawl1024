package net.tenie.crawl.controller;


import java.util.Properties;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeMessage; 
 

public class SendEMail { 
	public static void main(String[] args) throws MessagingException {
		SendEMail.simpleSendMail("tenie@tenie.net","tutucn@sohu.com","57523364","smtp.sohu.com","tes1t","tetst body...");
	}
	
	
	public static void simpleSendMail(String to,String from,String password,String host,String Subject,String body) throws  MessagingException{
		
		System.out.println("email info : to="+to+
				"\n from="+from+
				"\n password="+password+
				"\n host="+host+
				"\n Subject="+Subject+
				"\n body="+body);
		 

  	try{ 	  
	      // ��ȡϵͳ����
	      Properties properties = System.getProperties();   
	      //��Ȩ��;���ŵ��ʺ����� 
	      if(password.length()>0){
	    	  System.out.println("use password send ....");
		      // �����ʼ�������
		      properties.setProperty("mail.smtp.host", host);
		      properties.put("mail.smtp.auth", "true"); 
			  MyAuthenticator authenticator = new MyAuthenticator(from,password ); 
		      Session session = Session.getInstance(properties,authenticator);   
	          MimeMessage message = new MimeMessage(session);  
	          message.setFrom(new InternetAddress(from));  
	          message.addRecipient(Message.RecipientType.TO,   new InternetAddress(to));  
	          message.setSubject(Subject,"utf-8");   
	          message.setContent(body,  "text/html;charset=utf-8" );  
	          Transport.send(message);  
	      }else{
	    	  System.out.println("not use password send ....");
		      properties.setProperty("mail.smtp.host", "localhost"); 
		      Session session = Session.getInstance(properties);//,authenticator);  
		      MimeMessage message = new MimeMessage(session);   
		      message.addRecipient(Message.RecipientType.TO,   new InternetAddress(to));  
		      message.setSubject(Subject,"utf-8");   
		      message.setContent(body,  "text/html;charset=utf-8" );  
		      Transport.send(message);  
	      } 
  	  }catch (Exception mex) {
	         mex.printStackTrace();
	         throw  new MessagingException();
	      }
	}
	 
} 

 