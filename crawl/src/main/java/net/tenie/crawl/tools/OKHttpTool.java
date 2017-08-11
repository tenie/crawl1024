package net.tenie.crawl.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.stream.FileImageOutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class OKHttpTool {
	   
	    static String host ;
	    static String port ;
//	    static String url = "http://1212.ip138.com/ic.asp";
//	    static String url = "http://t66y.com";
	OkHttpClient client = new OkHttpClient();
//	 static{
//    	 System.setProperty("http.proxySet", "true");
//         System.setProperty("http.proxyHost", host);
//         System.setProperty("http.proxyPort", port + "");
//    }
	//1
	OKHttpTool(String host,String port){
	   System.setProperty("http.proxySet", "true");
       System.setProperty("http.proxyHost", host);
       System.setProperty("http.proxyPort", port );
	}
	OKHttpTool(){}
	
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
	    private void asyncGetBodyByte(String IMAGE_URL) {  
	       // client = new OkHttpClient();
	        final Request request = new Request.Builder().get()
	                .url(IMAGE_URL)
	                .build();

	        client.newCall(request).enqueue(new Callback() {
	            @Override
	            public void onFailure(Call call, IOException e) {
	                e.printStackTrace(); 
	            }

	            @Override
	            public void onResponse(Call call, Response response) throws IOException {
	            	   Headers responseHeaders = response.headers();
//	           	    for (int i = 0; i < responseHeaders.size(); i++) {
//	           	      System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//	           	    }
	           	    String type = responseHeaders.get("Content-Type").split("/")[1];
	           	    byte[] by =response.body().bytes();
	           	    System.out.println(by.length);
	           	    byte2image(by, "/Users/tenie/Desktop/foo."+type);
	           	    response.close();
	           	   
	            }
	            
	        });
	    }
	
	 //byte数组到图片
	  private static void byte2image(byte[] data,String path){
	    if(data.length<3||path.equals("")) return;
	    try{
	    FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
	    imageOutput.write(data, 0, data.length);
	    imageOutput.close();
	    System.out.println("Make Picture success,Please find image in " + path);
	    } catch(Exception ex) {
	      System.out.println("Exception: " + ex);
	      ex.printStackTrace();
	    }
	  }

	  public static void main(String[] args) throws Exception {
	//		OKHttpTool ok=new OKHttpTool();
		  //System.out.println(ok.run("http://tenie.net"));
		// ok.run();
		// ok.asyncGet("https://www.tenie.net/lib/img/JGT_meitu_3.jpg");
		  Map<String,Object> m = new HashMap();
		  String[] str = {"asd","dasd"};
		  m.put("val1", str);
		  m.put("val2", "hehe");
		 // Object o = m.get("val1");
		  String[] str2 =(String[]) m.get("val1");//new String[2];
		  System.out.println(Arrays.toString(str2)+"!");
		  
		} 

}
