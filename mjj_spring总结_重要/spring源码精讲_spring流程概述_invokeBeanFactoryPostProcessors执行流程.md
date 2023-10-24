# 源码流程 
1. ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("xxx.xml");


2. ClassPathXmlApplicationContext 类 （ this(new String[] {configLocation}, true, null); 点进去 ）
        public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        		this(new String[] {configLocation}, true, null);
        }


3. ClassPathXmlApplicationContext 类 （ refresh(); 点进去 ）
        public ClassPathXmlApplicationContext(
        	String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
        	throws BeansException {
        // 调用父类构造方法，进行相关的对象创建等操作,包含属性的赋值操作
        super(parent);
        setConfigLocations(configLocations);
        if (refresh) {
        	refresh();
        }


4. AbstractApplicationContext 类 （ invokeBeanFactoryPostProcessors(beanFactory); 点进去 ）
        // Invoke factory processors registered as beans in the context.
		// 调用各种beanFactory处理器
		invokeBeanFactoryPostProcessors(beanFactory);


5. AbstractApplicationContext 类 （ PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors( 点进去 ）
       // 获取到当前应用程序上下文的beanFactoryPostProcessors变量的值，并且实例化调用执行所有已经注册的beanFactoryPostProcessor
       // 默认情况下，通过getBeanFactoryPostProcessors()来获取已经注册的BFPP，但是默认是空的，那么问题来了，如果你想扩展，怎么进行扩展工作？
       PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());


6. 自己看 public static void invokeBeanFactoryPostProcessors( ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) 里面的代码



# 测试流程
1.spring-debug/src/main/resources/applicationContext.xml
        <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:msb="http://www.mashibing.com/schema/user"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context  http://www.springframework.org/schema/context/spring-context.xsd
        http://www.mashibing.com/schema/user http://www.mashibing.com/schema/user.xsd">
    
            <context:property-placeholder location="classpath:db.properties" ></context:property-placeholder>
            <bean class="com.mashibing.selfbdrpp.MyBeanDefinitionRegistryPostProcessor"></bean>
        </beans>
      
 
2. spring-debug/src/main/java/com/mashibing/Test.java（执行代码）
       MyClassPathXmlApplicationContext ac = new MyClassPathXmlApplicationContext("applicationContext.xml");


3. spring-debug/src/main/java/com/mashibing/selfbdrpp/MyBeanDefinitionRegistryPostProcessor.java
       public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
        @Override
       public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
       System.out.println("执行postProcessBeanDefinitionRegistry---MyBeanDefinitionRegistryPostProcessor");
       BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(MySelfBeanDefinitionRegistryPostProcessor.class);
       builder.addPropertyValue("name","zhangsan");
       registry.registerBeanDefinition("msb",builder.getBeanDefinition());
       }
        
       @Override
       public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
       System.out.println("执行postProcessBeanFactory---MyBeanDefinitionRegistryPostProcessor");
       BeanDefinition msb = beanFactory.getBeanDefinition("msb");
       msb.getPropertyValues().getPropertyValue("name").setConvertedValue("lisi");
       System.out.println("===============");
       }
        
       @Override
       public int getOrder() {
       return 0;
       }
       }


4. spring-debug/src/main/java/com/mashibing/selfbdrpp/MySelfBeanDefinitionRegistryPostProcessor.java
       public class MySelfBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
    
       private String name;
    
       public String getName() {
       return name;
       }
    
       public void setName(String name) {
       this.name = name;
       }
    
       @Override
       public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
       System.out.println("调用执行postProcessBeanDefinitionRegistry--MySelfBeanDefinitionRegistryPostProcessor");
       }
    
       @Override
       public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
       System.out.println("调用执行postProcessBeanFactory--MySelfBeanDefinitionRegistryPostProcessor");
       }
    
       @Override
       public int getOrder() {
       return 0;
       }
       }


5. 执行差异对比
     mjj_spring总结_重要/png/img.png （前）
     mjj_spring总结_重要/png/img1.png （后）