# 解析：
    启动类：spring-debug/src/main/java/com/mashibing/tx/xml/TxTest.java
    解析文件：spring-debug/src/main/resources/tx.xml

# 代码
spring-debug/src/main/java/com/mashibing/tx/xml/TxTest.java
    package com.mashibing.tx.xml;

    import com.mashibing.tx.xml.service.BookService;
    import com.mashibing.tx.xml.service.BookService;
    import org.springframework.cglib.core.DebuggingClassWriter;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;

    import java.sql.SQLException;

    // 把xml配置的方式准备对象的过程画一个流程图出来
    public class TxTest {
        public static void main(String[] args) throws SQLException {
            System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,"d:\\code");
            ApplicationContext context = new ClassPathXmlApplicationContext("tx.xml");
            BookService bookService = context.getBean("bookService", BookService.class);
            bookService.checkout("zhangsan",1);
        }
    }


spring-debug/src/main/resources/tx.xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:context="http://www.springframework.org/schema/context"
           xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">
        <context:property-placeholder location="classpath:dbconfig.properties"></context:property-placeholder>
        <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
            <property name="username" value="${jdbc.username}"></property>
            <property name="password" value="${jdbc.password}"></property>
            <property name="url" value="${jdbc.url}"></property>
            <property name="driverClassName" value="${jdbc.driverClassName}"></property>
        </bean>
        <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate" >
            <constructor-arg name="dataSource" ref="dataSource"></constructor-arg>
        </bean>
        <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
            <property name="dataSource" ref="dataSource"></property>
        </bean>
        <bean id="bookService" class="com.mashibing.tx.xml.service.BookService">
            <property name="bookDao" ref="bookDao"></property>
        </bean>
        <bean id="bookDao" class="com.mashibing.tx.xml.dao.BookDao">
            <property name="jdbcTemplate" ref="jdbcTemplate"></property>
        </bean>

        <aop:config>
            <aop:pointcut id="txPoint" expression="execution(* com.mashibing.tx.xml.*.*.*(..))"/>
            <aop:advisor advice-ref="myAdvice" pointcut-ref="txPoint"></aop:advisor>
        </aop:config>
        <tx:advice id="myAdvice" transaction-manager="transactionManager">
            <tx:attributes>
                <tx:method name="checkout" propagation="REQUIRED" />
                <tx:method name="updateStock" propagation="REQUIRED" />
                <!--<tx:method name="updateStock" propagation="REQUIRES_NEW" />-->
            </tx:attributes>
        </tx:advice>
        <!--<bean class="com.mashibing.MyBeanFactoryPostProcessorBySelf"></bean>-->
    </beans>


spring-debug/src/main/java/com/mashibing/tx/xml/service/BookService.java
    package com.mashibing.tx.xml.service;

    import com.mashibing.tx.xml.dao.BookDao;

    public class BookService {

        BookDao bookDao;

        public BookDao getBookDao() {
            return bookDao;
        }

        public void setBookDao(BookDao bookDao) {
            this.bookDao = bookDao;
        }

        /**
         * 结账：传入哪个用户买了哪本书
         * @param username
         * @param id
         */
        public void checkout(String username,int id){

            try {
                bookDao.updateStock(id);
            } catch (Exception e) {
                System.out.println("测试---------------------------------------------");
                e.printStackTrace();
            }
    //        for (int i = 1 ;i>=0 ;i--)
    //            System.out.println(10/i);
    //        int price = bookDao.getPrice(id);
    //        bookDao.updateBalance(username,price);
        }
    }


spring-debug/src/main/java/com/mashibing/tx/xml/dao/BookDao.java
    package com.mashibing.tx.xml.dao;

    import org.springframework.jdbc.core.JdbcTemplate;

    public class BookDao {

        JdbcTemplate jdbcTemplate;

        public JdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }

        public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        /**
         * 减去某个用户的余额
         * @param userName
         * @param price
         */
        public void updateBalance(String userName,int price){
            String sql = "update account set balance=balance-? where username=?";
            jdbcTemplate.update(sql,price,userName);
        }

        /**
         * 按照图书的id来获取图书的价格
         * @param id
         * @return
         */
        public int getPrice(int id){
            String sql = "select price from book where id=?";
            return jdbcTemplate.queryForObject(sql,Integer.class,id);
        }

        /**
         * 减库存，减去某本书的库存
         * @param id
         */
        public void updateStock(int id){
            String sql = "update book_stock set stock=stock-1 where id=?";
            jdbcTemplate.update(sql,id);
    //        for (int i = 1 ;i>=0 ;i--)
    //            System.out.println(10/i);
        }
    }

# 数据表
CREATE TABLE `book_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `stock` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `world`.`book_stock` (`id`, `stock`) VALUES (1, '1000');
INSERT INTO `world`.`book_stock` (`id`, `stock`) VALUES (2, '1000');
INSERT INTO `world`.`book_stock` (`id`, `stock`) VALUES (3, '1000');
INSERT INTO `world`.`book_stock` (`id`, `stock`) VALUES (4, '1000');


# 源码解析流程：
## 开启事务和连接
1. spring-debug/src/main/java/com/mashibing/tx/xml/TxTest.java （ bookService.checkout("zhangsan",1); 点进去 ）
        bookService.checkout("zhangsan",1);


2. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ .proceed(); 点进去 ）
        // We need to create a method invocation...
        // 通过cglibMethodInvocation来启动advice通知
        retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();


3. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ return super.proceed(); 点进去 ）
        return super.proceed();


4. spring-aop/src/main/java/org/springframework/aop/framework/ReflectiveMethodInvocation.java （ .invoke(this); 点进去 ）
        // It's an interceptor, so we just invoke it: The pointcut will have
        // been evaluated statically before this object was constructed.
        // 普通拦截器，直接调用拦截器，将this作为参数传递以保证当前实例中调用链的执行
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);


5. spring-aop/src/main/java/org/springframework/aop/interceptor/ExposeInvocationInterceptor.java （ return mi.proceed(); 点进去 ）
        return mi.proceed();


6. 重复3、4 步


7. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionInterceptor.java （ invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed); 点进去 ）
        /**
         * 以事务的方式调用目标方法
         * 在这埋了一个钩子函数 用来回调目标方法的
         */
        return invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed);


8. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java （ TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification); 点进去 ）
        // Standard transaction demarcation with getTransaction and commit/rollback calls.
        // 创建TransactionInfo
        TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);


9. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java （ status = tm.getTransaction(txAttr); 点进去 ）
        // 获取TransactionStatus事务状态信息
        status = tm.getTransaction(txAttr);


10. spring-tx/src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java （ Object transaction = doGetTransaction(); 点进去 ）
		// 获取事务
		Object transaction = doGetTransaction();


11. spring-jdbc/src/main/java/org/springframework/jdbc/datasource/DataSourceTransactionManager.java （ 这地方debug看一下逻辑 ）
        // 返回事务对象
		return txObject;


12. spring-tx/src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java （ return startTransaction(def, transaction, debugEnabled, suspendedResources); 点进去 ）
        return startTransaction(def, transaction, debugEnabled, suspendedResources);


13. spring-tx/src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java （ doBegin(transaction, definition); 点进去 ）
        // 开启事务和连接
        doBegin(transaction, definition);


14. spring-jdbc/src/main/java/org/springframework/jdbc/datasource/DataSourceTransactionManager.java （ 通过数据源获取一个数据库连接对象 ）
        // 通过数据源获取一个数据库连接对象
        Connection newCon = obtainDataSource().getConnection();


15. 回到 spring-aop/src/main/java/org/springframework/aop/interceptor/ExposeInvocationInterceptor.java 的 return mi.proceed(); 这里
        @Override
        public Object invoke(MethodInvocation mi) throws Throwable {
        	MethodInvocation oldInvocation = invocation.get();
        	invocation.set(mi);
        	try {
        		return mi.proceed();
        	}
        	finally {
        		invocation.set(oldInvocation);
        	}
        }


16. 数据库连接成功，开始事务的开启，以及业务的处理


17. 重复3、4 步


18. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionInterceptor.java （ invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed); 点进去 ）
        /**
         * 以事务的方式调用目标方法
         * 在这埋了一个钩子函数 用来回调目标方法的
         */
        return invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed);


19. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java （ retVal = invocation.proceedWithInvocation(); 点进去 ）
        // This is an around advice: Invoke the next interceptor in the chain.
        // This will normally result in a target object being invoked.
        // 执行被增强方法,调用具体的处理逻辑
        retVal = invocation.proceedWithInvocation();


20. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ return super.proceed(); 点进去 ）
        return super.proceed();


21. spring-aop/src/main/java/org/springframework/aop/framework/ReflectiveMethodInvocation.java （ return invokeJoinpoint(); 点进去 ）
        // We start with an index of -1 and increment early.
        // 从索引为-1的拦截器开始调用，并按序递增，如果拦截器链中的拦截器迭代调用完毕，开始调用target的函数，这个函数是通过反射机制完成的
        // 具体实现在AopUtils.invokeJoinpointUsingReflection方法中
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
        	return invokeJoinpoint();
        }


22. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ return this.methodProxy.invoke(this.target, this.arguments); 点进去 ）
        return this.methodProxy.invoke(this.target, this.arguments);


23. spring-core/src/main/java/org/springframework/cglib/proxy/MethodProxy.java （ 点进去 ）
        return fci.f1.invoke(fci.i1, obj, args);


24. spring-debug/src/main/java/com/mashibing/tx/xml/service/BookService.java （ bookDao.updateStock(id); 点进去  ）
        bookDao.updateStock(id);


25. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ .proceed(); 点进去 ）
        // We need to create a method invocation...
        // 通过cglibMethodInvocation来启动advice通知
        retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();


26. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ return super.proceed(); 点进去 ）
        return super.proceed();


27. spring-aop/src/main/java/org/springframework/aop/framework/ReflectiveMethodInvocation.java （ .invoke(this); 点进去 ）
        // It's an interceptor, so we just invoke it: The pointcut will have
        // been evaluated statically before this object was constructed.
        // 普通拦截器，直接调用拦截器，将this作为参数传递以保证当前实例中调用链的执行
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);


28. spring-aop/src/main/java/org/springframework/aop/interceptor/ExposeInvocationInterceptor.java （ return mi.proceed(); 点进去 ）
        return mi.proceed();


29. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ return super.proceed(); 点进去 ）
        return super.proceed();


30. spring-aop/src/main/java/org/springframework/aop/framework/ReflectiveMethodInvocation.java （ .invoke(this); 点进去 ）
        // It's an interceptor, so we just invoke it: The pointcut will have
        // been evaluated statically before this object was constructed.
        // 普通拦截器，直接调用拦截器，将this作为参数传递以保证当前实例中调用链的执行
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);


31. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionInterceptor.java （ invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed); 点进去 ）
        /**
         * 以事务的方式调用目标方法
         * 在这埋了一个钩子函数 用来回调目标方法的
         */
        return invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed);


32. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java （ retVal = invocation.proceedWithInvocation(); 点进去 ）
        // This is an around advice: Invoke the next interceptor in the chain.
        // This will normally result in a target object being invoked.
        // 执行被增强方法,调用具体的处理逻辑
        retVal = invocation.proceedWithInvocation();


33. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ return super.proceed(); 点进去 ）
        return super.proceed();


34. spring-aop/src/main/java/org/springframework/aop/framework/ReflectiveMethodInvocation.java （ return invokeJoinpoint(); 点进去 ）
        // We start with an index of -1 and increment early.
        // 从索引为-1的拦截器开始调用，并按序递增，如果拦截器链中的拦截器迭代调用完毕，开始调用target的函数，这个函数是通过反射机制完成的
        // 具体实现在AopUtils.invokeJoinpointUsingReflection方法中
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
        	return invokeJoinpoint();
        }


35. spring-aop/src/main/java/org/springframework/aop/framework/CglibAopProxy.java （ return this.methodProxy.invoke(this.target, this.arguments); 点进去 ）
        return this.methodProxy.invoke(this.target, this.arguments);


36. spring-core/src/main/java/org/springframework/cglib/proxy/MethodProxy.java （ 点进去 ）
        return fci.f1.invoke(fci.i1, obj, args);


37. spring-debug/src/main/java/com/mashibing/tx/xml/dao/BookDao.java （ jdbcTemplate.update(sql,id); 这地方执行，但是不提交事务，因为 propagation="REQUIRED" ， 这是内层事务，和外层事务共用一个事务 ）
        jdbcTemplate.update(sql,id);


37. debug回到上层的逻辑流程


38. 回到 spring-debug/src/main/java/com/mashibing/tx/xml/service/BookService.java （ 往下执行 ）


39. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java ( commitTransactionAfterReturning(txInfo); 点进去 )
        //成功后提交，会进行资源储量，连接释放，恢复挂起事务等操作
        commitTransactionAfterReturning(txInfo);


40. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java （ .commit(txInfo.getTransactionStatus());点进去 ）
        txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());


41. spring-tx/src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java （ processCommit(defStatus); 点进去 ）
        // 处理事务提交
        processCommit(defStatus);


42. spring-tx/src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java （ 执行这个 else 逻辑 ，然后 debug下去 ）
        else if (isFailEarlyOnGlobalRollbackOnly())


43. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java ( commitTransactionAfterReturning(txInfo); 点进去 )
        //成功后提交，会进行资源储量，连接释放，恢复挂起事务等操作
        commitTransactionAfterReturning(txInfo);


44. spring-tx/src/main/java/org/springframework/transaction/interceptor/TransactionAspectSupport.java （ .commit(txInfo.getTransactionStatus());点进去 ）
        txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());


45. spring-tx/src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java （ processCommit(defStatus); 点进去 ）
        // 处理事务提交
        processCommit(defStatus);


46. spring-tx/src/main/java/org/springframework/transaction/support/AbstractPlatformTransactionManager.java （ 走到 else if 里面 当前状态是新事务 ， doCommit(status); 点进去 ）
        // 如果是独立的事务则直接提交
        doCommit(status);


47. spring-jdbc/src/main/java/org/springframework/jdbc/datasource/DataSourceTransactionManager.java （ 这地方就是 内外层事物的一起提交，可以观察到数据库表数据的变化 ）
        // JDBC连接提交
        con.commit();




