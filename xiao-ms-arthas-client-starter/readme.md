# Arthas springboot starter

# 改造说明
1. 微服务环境+容器环境下，在某些情况下，我们需要在线定位问题、分析性能瓶颈，
   借助于阿里开源的arthas以及起对应Tunner Server的服务端和spring-boot-admin方案进行在线连接。
   更多信息参考[官方说明](https://github.com/alibaba/arthas)
2. 源arthas-springboot组件是在服务启动后自动启动arthas agent，我们的需求是在需要的时候启动；
   可以通过配置更改触发启动，也可以通过jmxmbean管理触发
3. 当前改造方案仅使用jmxmbean方式实现，参考[社区原文](https://github.com/alibaba/arthas/issues/1736#issuecomment-908202502)
4. 改造后的代码，参考``com.xiaogj.arthas.spring.jmx.ArthasMbeanImpl``实现即可
# 使用说明
1. 此方案需要客户端结合``spring actuator``和``springboot-admin``的服务端配合
2. 客户端，通过集成actuator开放jmx Mbean，服务端对开放的mbean进行操作，开放的mbean名称: ``ArthasMbean``
3. ArthasMbean提供的方法：启动Arthas、停止Artahs、设置Tunnel Server地址等
# 客户端使用说明
1. 引入pom文件
  ```xml
   <dependency>
      <groupId>com.xiao.ms.arthas</groupId>
      <artifactId>xiao-ms-arthas-client-starter</artifactId>
      <version>1.0.0-SNAPSHOT</version>
   </dependency>
  ```
2. 配置文件
   application.yml
  ```yaml
  arthas:
    # tunnel 服务端配置
    tunnel-server: ws://172.16.30.208:9898/ws
    app-name: applicationName
    telnet-port: 3658
    ip: 0.0.0.0
    #agent-id: ${spring.application.name}@${random.value}
    # 容器部署清下，使用IP+appname作为agentId
    agent-id: ${spring.application.name}@${spring.cloud.client.ip-address}
  ``` 
3. arthas更多功能和命令[使用参考](https://arthas.aliyun.com/doc/)
# [博客文档地址](https://blog.csdn.net/xiaoll880214/article/details/120295070?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link&utm_relevant_index=1)