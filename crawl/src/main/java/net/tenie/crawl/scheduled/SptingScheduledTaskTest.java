package net.tenie.crawl.scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.tenie.crawl.service.SendEMail;
 
@Component  
//@EnableScheduling	//启用定时任务
public class SptingScheduledTaskTest {  
   
    /** 
      * cron表达式：* * * * * *（共6位，使用空格隔开，具体如下）  
      * cron表达式：*(秒0-59) *(分钟0-59) *(小时0-23) *(日期1-31) *(月份1-12或是JAN-DEC) *(星期1-7或是SUN-SAT)  
     * 注意： 30 * * * * * 表示每分钟的第30秒执行，而（*斜杠30）表示每30秒执行 
      *  
      * */  
//    @Scheduled(cron="*/30 * * * * *")  
    public void firstTask() throws MessagingException{  
            System.out.println("==============it is first task!时间："
        	+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));  
        	SendEMail.simpleSendMail("tenie@tenie.net","tutucn@sohu.com","57523364","111.202.126.72","test","tetst body...");
        	 System.out.println("ok...");
    }  
}  