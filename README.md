# JAVA FINAL

这是我2025大二下学期的java编程基础课的大作业



## Quick Start

```bash
cd Java-final-homework/src/main/java/com/sportslens/ai/SportsLensAiApplication.java
```

environment:

*   Java 17
*   Maven 3.x
*   PostgreSQL 

1.  **数据库配置**: 在本地 PostgreSQL 中创建一个名为 `sportslens` 的数据库。
2.  **应用配置**: 打开 `src/main/resources/application.properties` 文件。
    *   修改 `spring.datasource.username` 和 `spring.datasource.password` 以匹配你的数据库凭据。
    *   在 `app.deepseek.api-key` 处填入你自己的 DeepSeek API 密钥。



**构建**: 在项目根目录下，运行 Maven 命令进行构建：

```bash
mvn clean install
```

**运行**: 构建成功后，可以通过以下命令启动应用：

```bash
java -jar target/sportslens-ai-0.0.1-SNAPSHOT.jar
```

或者直接在 IDE 中运行 `SportsLensAiApplication` 的 `main` 方法。

在`application.properties`中将数据库和api信息换成你的

```java
 spring.datasource.url=jdbc:postgresql://localhost:5432/sportslens
 spring.datasource.username=YOUR-USERNAME
 spring.datasource.password=YOUR-PASSWORD
      
 app.deepseek.api-key=YOUR-API-KEY
```



应用启动后，可以通过浏览器访问 `http://localhost:8080` 来使用该平台。

