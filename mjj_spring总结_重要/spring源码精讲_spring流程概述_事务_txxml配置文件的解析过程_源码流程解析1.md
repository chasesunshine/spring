# 解析：
    启动类：spring-debug/src/main/java/com/mashibing/tx/xml/TxTest.java
    解析文件：spring-debug/src/main/resources/tx.xml

# 源码解析流程：
    自己去看课程 spring事务配置文件的加载和对象创建 ( 看以下章节课程 )
    28-1 tx点xml配置项    ——    27-5 MethodInterceptor

# 总结注意点：
    Q: 这个地方目前有个疑问，为什么 bookService 对象 要被动态代理, 生成一个动态代理对象 ，使用 bookService 的原对象不行吗 ？
    A: 因为你需要对目标对象做增强   比如 日志 。事务 等的要求 
    Q: 做增强就一定需要生成代理对象吗
    A: 是的 不然你就得在原来对象中改业务代码了。那这耦合性不就增强了很多吗
    Q: 课程里面提过，不过我忘了，所以我还是想问一下，使用原对象而不适用代理对象会有什么问题 ， 就除了耦合性增强很多，还有其他问题吗
    A: 假设 你原来的方法只是单纯的 查询用户信息 。 现在有个需求要统计 方法执行的时间 。那么你要加代码。 明天有个需要要加事务处理。后天又要加 日志的需求。那你是不是要经常改这个方法呢 , 而且代码逻辑也越来越复杂了
    Q: 好的，这么说了解了，谢谢
    
    Q: 是不是  BookDao 和 BookService 都在 aop标签的 expression="execution(* com.mashibing.tx.xml.*.*.*(..)) 这个表达式的包扫描路径下，所以 生成了 两个 cglib 代理对象 @邓澎波 
    A: 可以这么理解的