package net.tenie.crawl.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.stream.FileImageOutputStream;
import org.springframework.stereotype.Component;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

@Component
public class OKHttpTool {
	   
	   private static String host ;
	   private static String port ;
//	    static String url = "http://1212.ip138.com/ic.asp";
//	    static String url = "http://t66y.com";
	    private static OkHttpClient client =   new OkHttpClient.Builder()
	    		.connectTimeout(20, TimeUnit.SECONDS)  
	            .readTimeout(20, TimeUnit.SECONDS)  
	            .build();
	    
	    public OKHttpTool(){
	       
    
	    }
//	 static{
//    	 System.setProperty("http.proxySet", "true");
//         System.setProperty("http.proxyHost", host);
//         System.setProperty("http.proxyPort", port + "");
//    }
	//1
	public static void setProxy(String host,String port){
	   System.setProperty("http.proxySet", "true");
       System.setProperty("http.proxyHost", host);
       System.setProperty("http.proxyPort", port );
	}
	 
	
	/**
	 * 获取body中的字符串
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String getBodyString(String url) throws IOException {
	  Request request = new Request.Builder()
	      .url(url)
	      .build(); 
	  Response response = client.newCall(request).execute(); 
	  if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	  return response.body().string();//获取响应结果
	}
	
	/**
	 * 获取body中的byte[] 
	 * @throws Exception
	 */
	public  byte[] getBodyBytes(String url) throws Exception {
		Builder b =new Request.Builder();
		b.url(url);
		//b.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
	   //  b.url("https://www.tenie.net/lib/img/JGT_meitu_3.jpg");
	    Request request = b.build();
//		Request request = new Request.Builder().url("http://cdn1.metarthunter.com/content/140303/zlatka-a-bunnu-15.jpg").build();
	   
	    Response response = client.newCall(request).execute();
	    //判断是否响应成功(http状态为200~300,都返回true)
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	 
	    //获取响应头中的键值对 
	    Headers responseHeaders = response.headers(); 
	    String type = responseHeaders.get("Content-Type").split("/")[1];
	    byte[] by =response.body().bytes();
	    return by;
//	    System.out.println(by.length);
//	    byte2image(by, "/Users/tenie/Desktop/foo."+type);
	}
	
	/**
	 * 获取body中的byte[] 和 content-type
	 * @param url
	 * @return 返回val = byte[] 和 type =  content-type的值
	 * @throws Exception
	 */
	public Map<String,Object> getBodyBytesAndType(String url) throws Exception {
		Builder b =new Request.Builder();
		b.url(url); 
		Request request = b.build();  
	    Response response = client.newCall(request).execute();
	    //判断是否响应成功(http状态为200~300,都返回true)
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	 
	    //获取响应头中的键值对 
	    Headers responseHeaders = response.headers(); 
	    String type = responseHeaders.get("Content-Type");//.split("/")[1];
	    byte[] by =response.body().bytes();
	    Map<String,Object> rs = new HashMap<String,Object>();
	    rs.put("val", by);
	    rs.put("type", type);
	    return rs; 
	}
	//3
	    /**
	     * 异步 获取body中的byte[] 
	     */
	    public void asyncGetBodyByte(String IMAGE_URL,Collection<Map<String,Object>> collection) {  
	       // client = new OkHttpClient();
	    	if(IMAGE_URL == null ||  "".equals(IMAGE_URL)){
	    		Map<String,Object> rs = new HashMap<String,Object>();
	    		rs.put("val", new byte[0]);
           	    rs.put("type", ""); 
                collection.add(rs); 
                return ;
	    	} 
	        final Request request = new Request.Builder()
	        		.get()
	                .url(IMAGE_URL)
	                .build();

	        client.newCall(request).enqueue(new Callback() {
	            @Override
	            public void onFailure(Call call, IOException e) {
	                e.printStackTrace(); 
	                Map<String,Object> rs = new HashMap<String,Object>();
	           	    rs.put("val", new byte[0]);
	           	    rs.put("type", ""); 
	                collection.add(rs); 
	            } 
	            @Override
	            public void onResponse(Call call, Response response) throws IOException {
	            	   Headers responseHeaders = response.headers();
//	           	    for (int i = 0; i < responseHeaders.size(); i++) {
//	           	      System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//	           	    }
	           	    String type = responseHeaders.get("Content-Type");
	           	    byte[] by =response.body().bytes();
	           	    Map<String,Object> rs = new HashMap<String,Object>();
	           	    rs.put("val", by);
	           	    rs.put("type", type); 
	           	    collection.add(rs);
	           	    // byte2image(by, "/Users/tenie/Desktop/foo."+type);
	           	    response.close();
	           	   
	            }
	            
	        });
	    }
	
	 //byte数组到图片
	  public static void byte2image(byte[] data,String path) throws IOException{
	    if(data.length<3||path.equals("")) return;
	    FileImageOutputStream imageOutput = null;
	    try{
	    imageOutput = new FileImageOutputStream(new File(path));
	    imageOutput.write(data, 0, data.length);
	  
	    System.out.println("Make Picture success,Please find image in " + path);
	    } catch(Exception ex) {
	      System.out.println("Exception: " + ex);
	      ex.printStackTrace();
	    }finally{
	    	if(imageOutput !=null)
	    	  imageOutput.close();
	    }
	  }
	 //image/gif
	  public static String typeChange(String type) {
		  String rs = "";
		  switch (type) {
		case "image/x-icon":
			rs = "ico";
			break;
		case "image/jpeg":
			rs = "jpg";
			break;
		case "image/png":
			rs = "png";
			break;
		case "image/gif":
			rs = "gif";
			break;
		default:
			break;
		}
		  
		  return rs;
	  }

	  public static void main(String[] args) throws Exception {
		
	 	OKHttpTool ok=new OKHttpTool();
		  //System.out.println(ok.run("http://tenie.net"));
		// ok.run();
	 	Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				int i = 0;
				while(i < 21) {
					
					try {
						Thread.sleep(1000);
						System.out.println(i++);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
	 		 
	 	 });
	 	thread.start();
	 	System.out.println("begin..");
	 	byte[] b = ok.getBodyBytes("http://cdn1.metarthunter.com/content/140303/zlatka-a-bunnu-15.jpg");
	 	 
	 	//			
		 System.out.println(b.length);
//		  Map<String, Object> rsMap =   new OKHttpTool().getBodyBytesAndType("https://www.tenie.net/lib/assets/img/codeMonkey.ico");
//		 System.out.println(rsMap.get("type")); 
		} 

}
