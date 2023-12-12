# Spring的自动装配 byName和byType的区别
    https://blog.csdn.net/jiguquan3839/article/details/89416203?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-3-89416203-blog-121715666.235%5Ev39%5Epc_relevant_3m_sort_dl_base1&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-3-89416203-blog-121715666.235%5Ev39%5Epc_relevant_3m_sort_dl_base1&utm_relevant_index=6

# byName
    <?xml version="1.0" encoding="UTF-8"?> 
        <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">
        
            <!--第一个UserDAO bean-->
            <bean name="userDAO" class="com.bjsxt.dao.impl.UserDAOImpl"> 
                <property name="daoId" value="1"></property> 
            </bean> 
            
            <!--第二个UserDAO bean-->
            <bean name="userDAO2" class="com.bjsxt.dao.impl.UserDAOImpl"> 
                <property name="daoId" value="2"></property> 
            </bean> 
            
            <!-- 这里的byName是按照属性名进行匹配 这里我们并没有注入UserDAO但是你的UserService属性名称是UserDAO 所以就相当于 你注入了UserDAO相当于在bean中添加<property name="userDAO" ref="userDAO"/> 一样 --> 
            
            <bean id="userService" class="com.bjsxt.service.UserService"  autowire="byName"></bean> 
            
            <!-- 需要注意的是在UserService类中，一定要有一个名为setUserDao的构造方法，其中setter方法名要与bean的id对应set+name，name首字母大写，否则无法成功注入 -->
        
        </beans> 
## 测试
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml"); 
        UserService service = (UserService)ctx.getBean("userService");
        System.out.println(service.getUserDAO());
        //打印出来是1 说明默认注入的是UserDAO 


# byType
            <?xml version="1.0" encoding="UTF-8"?> 
            <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">
            
                <!--第一个UserDAO bean-->
                <bean name="userDAO" class="com.bjsxt.dao.impl.UserDAOImpl"> 
                    <property name="daoId" value="1"></property> 
                </bean> 
            
                <!-- 第二个UserDAO bean,这里需要删除掉一个，类型相同的多个bean通过byType注入时会报错
                    <bean name="userDAO2" class="com.bjsxt.dao.impl.UserDAOImpl"> 
                     <property name="daoId" value="2"></property> 
                    </bean> 
                -->
            
               <!-- autowire修改为"byType"，重点是保证--> 
            
                <bean id="userService" class="com.bjsxt.service.UserService"  autowire="byType"></bean> 
            
               <!-- 需要注意的是在UserService类中，一定要有一个名为setUserDao的构造方法，被装配类中的setter方法的参数类型要与bean的class的类型一样，否则无法成功注入 -->
            
            </beans> 