package net.tenie.crawl.tools;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OKHttpTool {
	   
	    static String host = "127.0.0.1";
	    static int port = 1080;
	 //   static String url = "http://1212.ip138.com/ic.asp";
	    static String url = "http://t66y.com";
	OkHttpClient client = new OkHttpClient();
	 static{
    	 System.setProperty("http.proxySet", "true");
         System.setProperty("http.proxyHost", host);
         System.setProperty("http.proxyPort", port + "");
    }
	//1
	String run(String url) throws IOException {
	  Request request = new Request.Builder()
	      .url(url)
	      .build();
	 
	  Response response = client.newCall(request).execute();
	 
	  return response.body().string();//获取响应结果
	}
	//2
	public void run() throws Exception {
	    Request request = new Request.Builder()
	        .url("http://cdn1.metarthunter.com/content/140303/zlatka-a-bunnu-15.jpg")
	        .build();
	 
	    Response response = client.newCall(request).execute();
	    //判断是否响应成功(http状态为200~300,都返回true)
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	 
	    //获取响应头中的键值对
	   
	    Headers responseHeaders = response.headers();
	    for (int i = 0; i < responseHeaders.size(); i++) {
	      System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
	    }
	 
	    //System.out.println(response.body().string());
	}
	//3
	/**
	     * 异步get,直接调用
	     */
	    private void asyncGet(String IMAGE_URL) {
	    	
	    	
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

	            byte[] b = response.body().bytes();
	            System.out.println(b.length);
	            byte2image(b, IMAGE_URL);
	           // throw new IOException();
	            }
	        });
	    }
	public static void main(String[] args) throws Exception {
		OKHttpTool ok=new OKHttpTool();
	// System.out.println(ok.run("http://tenie.net"));
	//ok.run();
	ok.asyncGet("http://cdn1.metarthunter.com/content/140303/zlatka-a-bunnu-15.jpg");
	}
	//byte数组到图片
	  public static void byte2image(byte[] data,String path){
	  //  if(data.length<3||path.equals("")) return;
	    try{
	    FileImageOutputStream imageOutput = new FileImageOutputStream(new File("d:/1027.jpg"));
	    imageOutput.write(data, 0, data.length);
	    imageOutput.close();
	    System.out.println("Make Picture success,Please find image in " + path);
	    } catch(Exception ex) {
	      System.out.println("Exception: " + ex);
	      ex.printStackTrace();
	    }
	  }

	 

}
