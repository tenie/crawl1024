package net.tenie.crawl.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFile {
	

	/**
	 * 只能压缩一个文件夹
	 * @param pathString
	 * @return
	 * @throws IOException
	 */
	public static boolean zipAction(ZipOutputStream zipOut , InputStream input,String fileName) throws IOException{
		BufferedInputStream bin=null;
		try {  
	       bin = new BufferedInputStream(input);
	       zipOut.putNextEntry(new ZipEntry(fileName));
	       int temp = 0; 
           byte[] b=new byte[1024];
           while((temp = bin.read(b)) != -1){  
               zipOut.write(b, 0, temp);  
           }    
           return true;
	   } catch (Exception e) {  
	      e.printStackTrace();   
	   }finally{ 
		  if(bin!=null)
			 bin.close();  
	   }
	   return false;    
	}
	
	public static void main(String[] args) throws Exception {
		//onefileToZip("D:\\Office 2010 Toolkit（office2010激活工具）.exe");
		onedirToZip("D:\\dmslog - 副本 - 副本");
		System.out.println(".........");
	    DeleteFile.deleteAllFilesOfDir(new File("D:\\dmslog - 副本 - 副本"));
		//压缩一个文件夹
		//fileToZip("D:/dmslog"); 
		//压缩一个文件目录
//		ZipFile zipFile = new ZipFile();
// 		zipFile.zip("D:/zipfile.zip", new File("D:/dmslog")); 
		
//		System.out.println("o3333333k");
//		  ZipOutputStream zipOut =null;
//		  InputStream input = null; 
		  String pathString ="D:\\Office 2010 Toolkit（office2010激活工具）.exe";
//		  File file = new File(pathString);// 要被压缩的文件夹   
//	      File zipFile = new File(pathString+".zip");
//	      input = new FileInputStream(file); 
//	      zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
//	      
//	      zipAction(zipOut,input,"Office 2010 Toolkit（office2010激活工具）.exe");
	 
	      System.out.println("1o3333333k");
	}
	
	
	
	/**
	 * 只能压缩一个文件
	 * @param pathString
	 * @return
	 * @throws IOException
	 */
	public static boolean onefileToZip(String fileName) throws IOException{
		  ZipOutputStream zipOut =null;
		  InputStream input = null;  
		  BufferedInputStream bin=null;
		  
		  try { 
		         File file = new File(fileName);// 要被压缩的文件
		         File zipFile = new File(fileName+".zip");  
		    
		         zipOut = new ZipOutputStream(new FileOutputStream(zipFile));  
		         if(file.isFile()){   
		                 input = new FileInputStream(file); 
		                 bin = new BufferedInputStream(input);
		                 String filestr = file.getName() + File.separator + file.getName(); 
		                 zipOut.putNextEntry(new ZipEntry(new String(filestr.getBytes(),"utf-8")));  
		                 int temp = 0; 
		                 byte[] b=new byte[1024];
		                 while((temp = bin.read(b)) != -1){  
		                     zipOut.write(b, 0, temp);  
		                 }  
		             }  
		         
		         return true;
		  } catch (Exception e) {  
		      e.printStackTrace();   
		  }finally{
			  if(input!=null)
			     input.close(); 
			  if(bin!=null)
				 bin.close(); 
			  if(zipOut!=null)
			     zipOut.close();
			
		  }  
		return false;
	}
	
	/**
	 * 只能压缩一个目录中的文件.不包括目录
	 * @param pathString
	 * @return
	 * @throws IOException
	 */
	public static boolean onedirToZip(String pathString) throws IOException{
		  ZipOutputStream zipOut =null;
		  InputStream input = null;  
		  BufferedInputStream bin=null;
		  File file = new File(pathString);// 要被压缩的文件夹   
		  try { 
//		          File file = new File(pathString);// 要被压缩的文件夹   
		         File zipFile = new File(pathString+".zip");  
		         zipOut = new ZipOutputStream(new FileOutputStream(zipFile));  
		         if(file.isDirectory()){  
		             File[] files = file.listFiles();  
		             for(int i = 0; i < files.length; ++i){
		            	 if(files[i].isDirectory()) continue;
		                 input = new FileInputStream(files[i]); 
		                 bin = new BufferedInputStream(input);
		                 String filestr = file.getName() + File.separator + files[i].getName();
		                 // filestr =new String((filestr).getBytes("gb2312"),"iso-8859-1");
		                 zipOut.putNextEntry(new ZipEntry(new String(filestr.getBytes(),"utf-8")));  
		                 int temp = 0; 
		                 byte[] b=new byte[1024];
		                 while((temp = bin.read(b)) != -1){  
		                     zipOut.write(b, 0, temp);  
		                 }
		                  
		             }  
		            
		         } 	
		         return true;
		  } catch (Exception e) {  
		      e.printStackTrace();   
		  }finally{
			  if(input!=null)
			     input.close(); 
			  if(bin!=null)
				 bin.close(); 
			  if(zipOut!=null)
			     zipOut.close();
			   
		  }  
		return false;
	}
	
	/** 
	 * 程序实现了ZIP压缩。共分为2部分 ： 压缩（compression）与解压（decompression） 
	 * <p> 
	 * 大致功能包括用了多态，递归等JAVA核心技术，可以对单个文件和任意级联文件夹进行压缩和解压。 需在代码中自定义源输入路径和目标输出路径。 
	 * <p> 
	 * 在本段代码中，实现的是压缩部分；解压部分见本包中Decompression部分。 
	 *  
	 * @author HAN 
	 *  
	 */  
	 //压缩文件夹(可以多级)
	  private void zip(String zipFileName, File inputFile) throws Exception {  
	        System.out.println("压缩中...");  
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(  
	                zipFileName));  
	        BufferedOutputStream bo = new BufferedOutputStream(out);  
	        zip(out, inputFile, inputFile.getName(), bo);  
	        bo.close();  
	        out.close(); // 输出流关闭  
	        System.out.println("压缩完成");  
	    }  
	  private int k = 1; // 定义递归次数变量  
	  private void zip(ZipOutputStream out, File f, String base,  
	            BufferedOutputStream bo) throws Exception { // 方法重载  
	        if (f.isDirectory()) {  
	            File[] fl = f.listFiles();  
	            if (fl.length == 0) {  
	                out.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base  
	                System.out.println(base + "/");  
	            }  
	            for (int i = 0; i < fl.length; i++) {  
	                zip(out, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹  
	            }  
	            System.out.println("第" + k + "次递归");  
	            k++;  
	        } else {  
	            out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base  
	            System.out.println(base);  
	            FileInputStream in = new FileInputStream(f);  
	            BufferedInputStream bi = new BufferedInputStream(in);  
	            int b;  
	            while ((b = bi.read()) != -1) {  
	                bo.write(b); // 将字节流写入当前zip目录  
	                bo.flush();  //加上flush,缓存区的东西输出,不然会有问题
	            }  
	            bi.close();  
	            in.close(); // 输入流关闭  
	        }  
	    } 
	     
	
	
	

}
