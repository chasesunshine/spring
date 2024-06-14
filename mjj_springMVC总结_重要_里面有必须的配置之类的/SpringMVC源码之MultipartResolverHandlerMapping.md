# http请求 识别到 url 有三种方式 （源码的两种处理映射方式）
    参考 spring-mymvc/src/main/java/com/mashibing/controller/testController
    ## BeanNameUrlHandlerMapping
        匹配 url 的请求
        1. 实现 Controller接口
            spring-mymvc/src/main/java/com/mashibing/controller/testController/Test01.java
            import org.springframework.web.servlet.mvc.Controller;
        2. 实现 HttpRequestHandler 接口
            spring-mymvc/src/main/java/com/mashibing/controller/testController/Test02.java
            import org.springframework.web.HttpRequestHandler;

    ## RequestMappingHandlerMapping
        处理 @Controller 这个注解