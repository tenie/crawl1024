package net.tenie.crawl.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestProxy {
    static String host = "127.0.0.1";
    static int port = 1080;
 //   static String url = "http://1212.ip138.com/ic.asp";
    static String url = "http://t66y.com";
    public static void main(String[] args) throws Exception {
        four();
    }
    static void one() throws MalformedURLException, IOException {
        // 没有这句话是不行的
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port + "");
        URLConnection connection = new URL(url).openConnection();
        show(connection.getInputStream());
    }
    static void two() throws MalformedURLException, IOException {
        SocketAddress addr = new InetSocketAddress(host, port);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
        // 下面这个网址会告诉你你的ip地址
        URLConnection connection = new URL(url).openConnection(proxy);
        show(connection.getInputStream());
    }
    // 使用socket也是一样
    static void three() throws IOException {
        SocketChannel sc = SocketChannel
                .open(new InetSocketAddress(host, port));
        sc.write(Charset.forName("utf8")
                .encode("GET " + url + " HTTP/1.1\r\n\r\n"));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (sc.read(buffer) != -1) {
            buffer.flip();
            System.out.println(Charset.forName("utf8").decode(buffer));
            buffer.clear();
        }
        sc.close();
    }
    static void four() throws IOException {
        // 以下地址是代理服务器的地址
        Socket socket = new Socket(host, port);
        // 写与的内容就是遵循HTTP请求协议格式的内容，请求百度
        socket.getOutputStream().write(
                new String("GET " + url + " HTTP/1.1\r\n\r\n").getBytes());
        show(socket.getInputStream());
        socket.close();
    }
    static void show(InputStream in) throws IOException {
        Scanner cin = new Scanner(in);
        StringBuilder builder = new StringBuilder();
        while (cin.hasNext()) {
            builder.append(cin.nextLine());
        }
        cin.close();
//        Pattern pattern = Pattern
//                .compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
//        Matcher matcher = pattern.matcher(builder.toString());
//        matcher.find();
//        System.out.println(matcher.group());
        System.out.println(builder.toString());
    }
}
