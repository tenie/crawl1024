package net.tenie.entity;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 1. 在开始下载图片时确认好要下载几个图片,初始化index值,
 * 
 * 2.把下载的图片放入队列中,
 * 3. 判断index 是不是0; 不是从队列中获取一个图片bin, index--;
 * 
 * 方案2:
 * 1. 在开始下载前初始化, 队列大小,
 * 2. 下载的图片 bin 填入队列, 
 * 3. 队列满了以后 调用图片输出方法
 * 
 * @author tenie
 *
 */
public class BinData {
	private static ArrayBlockingQueue queue;
	private static int index;
	
}
