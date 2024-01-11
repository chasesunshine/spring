# 解析：（注解层面的解析）
    启动类：spring-debug/src/main/java/com/mashibing/tx/annotation/TransactionTest.java
    解析文件：spring-debug/src/main/java/com/mashibing/tx/annotation/config/TransactionConfig.java 

# 源码解惑1：（关于 invokeBeanFactoryPostProcessors(beanFactory) 中如何解析 @Configration ）
1. spring-debug/src/main/java/com/mashibing/tx/annotation/TransactionTest.java （ applicationContext.refresh(); 点进去 ）
        applicationContext.refresh();


2. AbstractApplicationContext 类 （ invokeBeanFactoryPostProcessors(beanFactory); 点进去 ）
        // Invoke factory processors registered as beans in the context.
        // 调用各种beanFactory处理器
        invokeBeanFactoryPostProcessors(beanFactory);

*  invokeBeanFactoryPostProcessors(beanFactory);  ——> 调用执行BFPP，可以修改或者引入其他的beanDefinition但是需要注意，BFPP针对的操作对象是BeanFactory , ConfigurationClassPostProcessor用来完成对相关注解的解析工作


3. AbstractApplicationContext 类 （ PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors( 点进去 ）
       // 获取到当前应用程序上下文的beanFactoryPostProcessors变量的值，并且实例化调用执行所有已经注册的beanFactoryPostProcessor
       // 默认情况下，通过getBeanFactoryPostProcessors()来获取已经注册的BFPP，但是默认是空的，那么问题来了，如果你想扩展，怎么进行扩展工作？
       PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());


4. PostProcessorRegistrationDelegate 类 （ invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry); 点进去 ）
        // 遍历currentRegistryProcessors，执行postProcessBeanDefinitionRegistry方法
        invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);


5. PostProcessorRegistrationDelegate 类 （ postProcessor.postProcessBeanDefinitionRegistry(registry); 点进去 ）
        //调用 postProcessor 的 postProcessBeanDefinitionRegistry以使得postProcess往registry注册BeanDefinition对象
        postProcessor.postProcessBeanDefinitionRegistry(registry);


6. BeanDefinitionRegistryPostProcessor 类 （ void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException; 点进去 ）
        void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;


7. ConfigurationClassPostProcessor 类 （ processConfigBeanDefinitions(registry); 点进去 ）
    // 处理配置类的bean定义信息
    processConfigBeanDefinitions(registry);


8. ConfigurationClassPostProcessor 类
        // 遍历所有要处理的beanDefinition的名称,筛选对应的beanDefinition（被注解修饰的）
		for (String beanName : candidateNames) {
			// 获取指定名称的BeanDefinition对象
			BeanDefinition beanDef = registry.getBeanDefinition(beanName);
			// 如果beanDefinition中的configurationClass属性不等于空，那么意味着已经处理过，输出日志信息
			if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
				}
			}
			// 判断当前BeanDefinition是否是一个配置类，并为BeanDefinition设置属性为lite或者full，此处设置属性值是为了后续进行调用
			// 如果Configuration配置proxyBeanMethods代理为true则为full
			// 如果加了@Bean、@Component、@ComponentScan、@Import、@ImportResource注解，则设置为lite
			// 如果配置类上被@Order注解标注，则设置BeanDefinition的order属性值
			else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
				// 添加到对应的集合对象中
				configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
			}
		}

* 总结： 第八步 for循环 ： 开始筛选一些合适的bean对象，这地方会开始进行扫描 ， 同时判断是不是 Configration 指定的一个配置类，如果是的话就可以完成解析工作了
        所以for循环处理完成之后 ， configCandidates 里面最后只有一个 被 @Configuration 注解标识的 beanName = "transactionConfig" 的一个 ArrayList


# 源码解惑2：（关于 invokeBeanFactoryPostProcessors(beanFactory) 中如何 解析 @Bean ）
* 源码解惑1 中的代码继续执行

9. ConfigurationClassPostProcessor 类 （ parser.parse(candidates); 点进去 ）
        // 解析带有@Controller、@Import、@ImportResource、@ComponentScan、@ComponentScans、@Bean的BeanDefinition
        parser.parse(candidates);


10. ConfigurationClassParser 类 （ parse( 点进去 ）
    // 注解类型
    if (bd instanceof AnnotatedBeanDefinition) {
        parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
    }


11. ConfigurationClassParser 类 （ processConfigurationClass( 点进去 ）
    // 根据注解元数据和beanName解析配置文件，有注解元数据
    protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
    }


12. ConfigurationClassParser 类 （ sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter); 点进去 这个方法点进去要仔细看 ）
    do {
        // 解析各种注解
        sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
    }


13. ConfigurationClassParser 类
        // Process individual @Bean methods
		// 处理加了@Bean注解的方法，将@Bean方法转化为BeanMethod对象，保存再集合中
		Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
		for (MethodMetadata methodMetadata : beanMethods) {
			configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
		}

* 总结： 第十三步 Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass); 这一步是 解析 @Configration 下 @Bean
        解析 spring-debug/src/main/java/com/mashibing/tx/annotation/config/TransactionConfig.java 中 带有 @Bean 的
        解析下来有五个 如图： mjj_spring总结_重要/png/img_2.png


# 源码解惑3：（关于 invokeBeanFactoryPostProcessors(beanFactory) 中 TransactionConfig 在 beanDefinitionMap 中 从 原始对象 被替换成了 代理对象 的原因 ）
1. spring-debug/src/main/java/com/mashibing/tx/annotation/TransactionTest.java （ applicationContext.refresh(); 点进去 ）
        applicationContext.refresh();


2. AbstractApplicationContext 类 （ invokeBeanFactoryPostProcessors(beanFactory); 点进去 ）
        // Invoke factory processors registered as beans in the context.
        // 调用各种beanFactory处理器
        invokeBeanFactoryPostProcessors(beanFactory);

*  invokeBeanFactoryPostProcessors(beanFactory);  ——> 调用执行BFPP，可以修改或者引入其他的beanDefinition但是需要注意，BFPP针对的操作对象是BeanFactory , ConfigurationClassPostProcessor用来完成对相关注解的解析工作


3. AbstractApplicationContext 类 （ PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors( 点进去 ）
       // 获取到当前应用程序上下文的beanFactoryPostProcessors变量的值，并且实例化调用执行所有已经注册的beanFactoryPostProcessor
       // 默认情况下，通过getBeanFactoryPostProcessors()来获取已经注册的BFPP，但是默认是空的，那么问题来了，如果你想扩展，怎么进行扩展工作？
       PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());


4. PostProcessorRegistrationDelegate 类 （ invokeBeanFactoryPostProcessors(registryProcessors, beanFactory); 点进去 ）
        // Now, invoke the postProcessBeanFactory callback of all processors handled so far.
        // 调用所有BeanDefinitionRegistryPostProcessor的postProcessBeanFactory方法
        invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);


5. PostProcessorRegistrationDelegate 类 （ postProcessor.postProcessBeanFactory(beanFactory); 点进去 ）
        //回调 BeanFactoryPostProcessor 的 postProcessBeanFactory 方法，使得每个postProcessor对象都可以对
        // beanFactory进行调整
        postProcessor.postProcessBeanFactory(beanFactory);


6. BeanFactoryPostProcessor 类 （ 点进去 ）
        void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;


7. ConfigurationClassPostProcessor 类 （ enhanceConfigurationClasses(beanFactory); 点进去 ）
        enhanceConfigurationClasses(beanFactory);


8. ConfigurationClassPostProcessor 类
    ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
		for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
			AbstractBeanDefinition beanDef = entry.getValue();
			// If a @Configuration class gets proxied, always proxy the target class
			beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
			// Set enhanced subclass of the user-specified bean class
			Class<?> configClass = beanDef.getBeanClass();
			Class<?> enhancedClass = enhancer.enhance(configClass, this.beanClassLoader);
			if (configClass != enhancedClass) {
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("Replacing bean definition '%s' existing class '%s' with " +
							"enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
				}
				beanDef.setBeanClass(enhancedClass);
			}
		}

* 总结： 第八步 ， TransactionConfig 在 beanDefinitionMap 中 从 原始对象 被替换成了 代理对象 ， 详见： mjj_spring总结_重要/png/img_3.png


# 源码解惑3：（ 问： 所有加了 @Configration 的类 都会 在 beanDefinitionMap 中 被替换成 代理类 ？ ）
答： 是的 ， 所有加了 @Configration 的类 都会 在 beanDefinitionMap 中 被替换成 代理类


# 源码解惑4：（ 问： 为什么所有加了 @Configration 的类 都会 在 beanDefinitionMap 中 被替换成 代理类 ， 代理的意义是什么 ？ ）
答： 就是为了保证我们创建的对象 都保证是 单例 ，
    其实就是 会通过 proxy.invokeSuper 调用父类方法来创建我们的对象，这时候会直接根据我们当前的 beanfactory 来获取 对象，而不会再去 new 一个对象，这时候就可以保证对象的 单例
    详见： mjj_spring总结_重要/png/img_4.png









