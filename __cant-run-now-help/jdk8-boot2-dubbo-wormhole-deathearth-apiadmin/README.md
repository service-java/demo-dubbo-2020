# README

## <a name="7">平台环境要求</a>
1. JDK1.8
2. Disconf配套环境【已移除配置】
3. Zookeeper
4. Tomcat容器等
5. Mysql数据库

> SpringBoot版本不需要tomcat,

## <a name="8">平台运行说明</a>

* 下载项目
* 处理以下几个核心配置

	* /gateway.sql 									
	【初始化数据库信息】
	* /resources/config.properties 					
	【修改zk、redis、mysql等的连接】
	* /com/kaistart/gateway/config/MgrConfig.java 	
	【修改不同环境的ip信息，这里这样写主要为了简化使用，可按需迁移到配置中心去】
	* gateway-web的 Context root 为 “/”
* 部署到tomcat容器进行启动, 记得将webcontext-root 改为 '/'

> 运行的时候，DEBUG模式查看日志，比较容易处理问题。

