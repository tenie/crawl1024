package net.tenie.crawl.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class MainServiceImpl implements MainService{
	
	OkHttpClient client = new OkHttpClient();
	
	/**
	 * 根据给出的url 获取响应体字符串
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String getUrlBody(String url) throws IOException {
	  Request request = new Request.Builder()
	      .url(url)
	      .build();
	  Response response = client.newCall(request).execute(); 
	  return response.body().string();//获取响应结果
	}
	
	/**
	 * 和上面方法一样的作用
	 * @throws Exception
	 */
	public String getUrlBody2(String url) throws Exception {
	    Request request = new Request.Builder()
	        .url(url)
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
	    return response.body().string(); 
	}
	
	 /**
	  * 
	  *  
	  */
	  private void asyncGet(String url) {
	        client = new OkHttpClient();
	        final Request request = new Request.Builder().get()
	                .url(url)
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
	            byte2image(b, url);
	            throw new IOException();
	            }
	        });
	    }
	  
	  /**
	   * byte[] 转换为图片保存在本地
	   * @param data 要做转换的byte[]
	   * @param path 保存的路径
	   */
	  public static void byte2image(byte[] data,String path){
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
	
}
