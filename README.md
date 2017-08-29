
##  网页图片爬取应用 ##

一个网页上的指定类型的图片; 

如何选取图片,需要对html,jQuery选择器有所了解;

项目名称的起源, 有点不可描述, 最初的需求是为了可以批量下载保存某1024社区中的美图.所以称为crawl1024

## 使用 ##
    
    #构建应用
    cd ./crawl
    mvn clean package
    cd target
    #启动应用 
    java -jar crawl1024.jar
    # 浏览器访问 http://localhost:8080

**技术选型**
	
1. 后端: Spring-boot,okhttp3,jsoup
2. 前端: bootstrap,jQuery,vue


**相关细节**

1. 使用web技术,快速构建客户端ui,ux
2. 可以单机使用, 也可以当服务器应用(使用了session隔离)
3. 因为有墙的关系, 单机使用可设置代理. 本人是把应用部署到境外服务器, 然后在墙内操作.
