
##  网页图片爬取应用 ##

爬取一个(或多个)网页上的指定类型的图片; 

选取图片: 需要对html,jQuery选择器一些了解(不了解有简单默认值使用)

项目名称的起源, 有点不可描述, 最初的需求是为了可以批量下载保存某1024社区中的美图.所以称为crawl1024

## 使用 ##
    
    #构建应用(windows os)
    cd ./crawl
    mvn clean package 
    #启动应用 
    java -jar target/crawl1024.jar
    # 浏览器访问 http://localhost:8080
    
    # Mac或Linux 构建应用
    下载 http://phantomjs.org/download.html 
    # 配置phantomjs路径,和脚本的路径(可用默认值)
    vim ./crawl/src/main/resources/application.properties 
    cd ./crawl 
	mvn clean package 
    #启动应用 
    java -jar target/crawl1024.jar

**技术选型**  

1. 后端: 
	1. Spring-boot,
	2. okhttp3,
	3. jsoup,
	4. phantomjs(Headless browser程序)
2. 前端: 
	1. bootstrap,
	2. jQuery,
	3. vue


**相关细节**

1. 使用web技术,快速构建客户端ui,ux
2. 使用HeadLess browser 解析页面,应付动态页面,反爬虫的站点;
3. 可以单机使用, 也可以当服务器应用(使用了session隔离)
4. 因为有墙的关系, 单机使用可设置代理. 本人是把应用部署到境外服务器, 然后在墙内操作.
