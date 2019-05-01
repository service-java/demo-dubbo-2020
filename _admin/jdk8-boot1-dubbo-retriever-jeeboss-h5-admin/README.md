# jeeboss
- jeeboss使用了spring boot和dubbo构建一个分布式服务系统。封装了各种常用操作，适合用于快速构建分布式服务系统
- 使用maven管理各个模块
- 包含了基本的权限管理系统
- 使用metrics进行服务统计
- 各模块介绍：
- - jeeboss-common：公共模块封装了常用操作(job、缓存、消息、分布式锁)
- - jeeboss-webapp-common：web公共模块(全局拦截器、监控、jwt)
- - jeeboss-api：boss后台接口定义
- - jeeboss-service：boss后台接口实现
- - jeeboss-webapp：boss后台服务
- - jeeboss-task：定时任务相关
- - jeeboss-h5：h5页面


## 模块详细介绍
### jeeboss-common
- quartz定时任务高可用、job管理、注解形式支持
- redis、RabbitMq消息消费者注解支持
- redis注解形式订阅
- 注解缓存支持。缓存存储方式有本地缓存(Ehcache)和远程缓存(redis)。远程缓存支持简单对象和复杂对象
- 分布式锁支持(redis实现)
- 配置中心支持(zookeeper实现)
- dubbo服务调用链
- excle导出

### jeeboss-webapp-common
- spring-boot方式启动
- 服务端cors跨域支持
- JWT身份认证
- 分布式会话
- 服务调用量和服务调用耗时监控
- 服务调用链

### jeeboss-webapp
- 基础权限系统
- 注解权限支持
- 会话管理
- 服务监控
- 定时任务管理
- 字典管理。arttemplate快速调用方式。（更多使用方式参考 https://github.com/aui/art-template）
- - 获取字典值：{{getDictLabel(type,value,defaultValue)}}
- - 获取字典列表：{{getDicts(type)}}。获取直接循环获取{{each getDicts(type) as dict}}{{/each}}
### 启动方式和部分功能预览
#### 启动方式
- 拉取项目导入eclipse.项目依赖redis、zookeeper
- 导入数据库脚本
- 如需上传服务，需要在配置文件配置又拍云相关配置
- 可以使用maven assembly方式打包启动.启动脚本均需传入启动端口、环境参数
- dubbo service服务启动
- - 修改dev/application.properties配置文件，指定redis、mysql、zookeeper(多环境配置环境只需启动时指定jvm参数，如下)
- - 使用com.zksite.common.container.SpringServiceContainer类启动服务,并指定jvm参数：Dspring.profiles.active=dev
- jeeboss-webapp使用spirng boot方式启动。启动类：com.zksite.web.common.Application 分别指定环境配置文件和tomcat端口，jvm参数：-Dspring.profiles.active=dev -Dtomcat.server.port=8088
- jeeboss-h5修改jeeboss-h5/src/main/webapp/assets/js/common/common.js里的domain，指定服务地址。如果h5放到tomcat运行，需要把项目名更改为ROOT(有些路径写死.囧)
- //账号/密码:admin/1235678
- //演示地址：http://jeeboss.zksite.com admin/admin
- 如有问题或有更好的建议，欢迎加入QQ群一起讨论，群号474925675

#### job管理
![job管理](http://ypstatic.zksite.com/e3/18/c3e4949f55fe48c9a9f8fd8717a5b753.png "job管理")
#### 会话管理
![会话管理](http://ypstatic.zksite.com/91/24/0a2e7dba337443e7a96871f5dbe3fc38.png "会话管理")
#### 服务监控
![请求量监控](http://ypstatic.zksite.com/df/fe/14dc3ad9777d4ff6a7f9afc2dec73444.png "请求量监控")
![请求耗时监控](http://ypstatic.zksite.com/6d/ea/b6c3e5e9a5d84cfb89244d082c3cd67e.png "请求耗时监控")


