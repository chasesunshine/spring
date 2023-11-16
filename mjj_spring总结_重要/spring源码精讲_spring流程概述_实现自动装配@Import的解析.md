# springboot 实现自动装配 @Import解析
1. 启动类 （ @SpringBootApplication 点进去 ）
   @SpringBootApplication

2. public @interface SpringBootApplication （ @EnableAutoConfiguration 点进去 ）
   @EnableAutoConfiguration

3. public @interface EnableAutoConfiguration （ AutoConfigurationImportSelector.class 点进去 ）
   @Import({AutoConfigurationImportSelector.class})

4. AutoConfigurationImportSelector 类 （ this.getAutoConfigurationEntry( 点进去 ）
   public String[] selectImports(AnnotationMetadata annotationMetadata) {
   if (!this.isEnabled(annotationMetadata)) {
     return NO_IMPORTS;
   } else {
     AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader.loadMetadata(this.beanClassLoader);
     AutoConfigurationEntry autoConfigurationEntry = this.getAutoConfigurationEntry(autoConfigurationMetadata, annotationMetadata);
     return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
   }
   }

5. AutoConfigurationImportSelector 类 （this.getCandidateConfigurations(annotationMetadata, attributes); 点进去 ）
   protected AutoConfigurationEntry getAutoConfigurationEntry(AutoConfigurationMetadata autoConfigurationMetadata, AnnotationMetadata annotationMetadata) {
   if (!this.isEnabled(annotationMetadata)) {
   return EMPTY_ENTRY;
   } else {
   AnnotationAttributes attributes = this.getAttributes(annotationMetadata);
   List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);
   configurations = this.removeDuplicates(configurations);
   Set<String> exclusions = this.getExclusions(annotationMetadata, attributes);
   this.checkExcludedClasses(configurations, exclusions);
   configurations.removeAll(exclusions);
   configurations = this.filter(configurations, autoConfigurationMetadata);
   this.fireAutoConfigurationImportEvents(configurations, exclusions);
   return new AutoConfigurationEntry(configurations, exclusions);
   }
   }

6. AutoConfigurationImportSelector 类 （ SpringFactoriesLoader.loadFactoryNames( 点进去 ）
   protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
   List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());
   Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you are using a custom packaging, make sure that file is correct.");
   return configurations;
   }

7. SpringFactoriesLoader 类 （ return (List)loadSpringFactories(classLoader) 点进去 ）
   public static List<String> loadFactoryNames(Class<?> factoryClass, @Nullable ClassLoader classLoader) {
   String factoryClassName = factoryClass.getName();
   return (List)loadSpringFactories(classLoader).getOrDefault(factoryClassName, Collections.emptyList());
   }

8. SpringFactoriesLoader 类 （ 从这可以看到 加载的是 "META-INF/spring.factories" 里面的信息 ，找到 spring.factories 文件 ）
   Enumeration<URL> urls = classLoader != null ? classLoader.getResources("META-INF/spring.factories") : ClassLoader.getSystemResources("META-INF/spring.factories");

9. spring.factories 文件 （ 这就是 自动装配的结果 ）
  # Auto Configure
  org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
  org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
  org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
  




# 源码层面 @Import 的解析 
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

4. AbstractApplicationContext 类 （ obtainFreshBeanFactory(); 点进去 ）
   // Tell the subclass to refresh the internal bean factory.
   // 创建容器对象：DefaultListableBeanFactory
   // 加载xml配置文件的属性值到当前工厂中，最重要的就是BeanDefinition
   ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

5. AbstractApplicationContext 类 （ refreshBeanFactory(); 点进去 ）
   // 初始化BeanFactory,并进行XML文件读取，并将得到的BeanFactory记录在当前实体的属性中
   refreshBeanFactory();

6. AbstractApplicationContext 类 （ invokeBeanFactoryPostProcessors(beanFactory); 点进去 ）
   // Invoke factory processors registered as beans in the context.
   // 调用各种beanFactory处理器
   invokeBeanFactoryPostProcessors(beanFactory);

7. AbstractApplicationContext 类 （ PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors( 点进去 ）
   // 获取到当前应用程序上下文的beanFactoryPostProcessors变量的值，并且实例化调用执行所有已经注册的beanFactoryPostProcessor
   // 默认情况下，通过getBeanFactoryPostProcessors()来获取已经注册的BFPP，但是默认是空的，那么问题来了，如果你想扩展，怎么进行扩展工作？
   PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

8. PostProcessorRegistrationDelegate 类 （ invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry); 点进去 ）
   // 遍历currentRegistryProcessors，执行postProcessBeanDefinitionRegistry方法
   invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);

9. PostProcessorRegistrationDelegate 类 （ postProcessor.postProcessBeanDefinitionRegistry(registry); 点进去 ）
   private static void invokeBeanDefinitionRegistryPostProcessors(
   Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

        //遍历 postProcessors
        for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
            //调用 postProcessor 的 postProcessBeanDefinitionRegistry以使得postProcess往registry注册BeanDefinition对象
            postProcessor.postProcessBeanDefinitionRegistry(registry);
        }
   }

10. BeanDefinitionRegistryPostProcessor 类 （ void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException; 点进去 ）
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

11. ConfigurationClassPostProcessor 类 （ processConfigBeanDefinitions(registry); 点进去 ）
	/**
	* 定位、加载、解析、注册相关注解
	*
	* Derive further bean definitions from the configuration classes in the registry.
	*/
	  @Override
	  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
	    // 根据对应的registry对象生成hashcode值，此对象只会操作一次，如果之前处理过则抛出异常
	    int registryId = System.identityHashCode(registry);
	    if (this.registriesPostProcessed.contains(registryId)) {
	    throw new IllegalStateException(
	    "postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
	    }
	    if (this.factoriesPostProcessed.contains(registryId)) {
	    throw new IllegalStateException(
	    "postProcessBeanFactory already called on this post-processor against " + registry);
	    }
	    // 将马上要进行处理的registry对象的id值放到已经处理的集合对象中
	    this.registriesPostProcessed.add(registryId);
  
	    // 处理配置类的bean定义信息
	    processConfigBeanDefinitions(registry);
	  }

12. ConfigurationClassPostProcessor 类 （ parser.parse(candidates); 点进去 ）
	// 解析带有@Controller、@Import、@ImportResource、@ComponentScan、@ComponentScans、@Bean的BeanDefinition
	parser.parse(candidates);

13. ConfigurationClassParser 类 （ parse( 点进去 ）
	// 注解类型
	if (bd instanceof AnnotatedBeanDefinition) {
	  parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
	}

14. ConfigurationClassParser 类 （ processConfigurationClass( 点进去 ）
	// 根据注解元数据和beanName解析配置文件，有注解元数据
	protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
	  processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
	}

15. ConfigurationClassParser 类 （ doProcessConfigurationClass(configClass, sourceClass, filter); 点进去 ）
	do {
	  // 解析各种注解
	  sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
	}

16-1. ConfigurationClassParser 类 （ getImports(sourceClass) 点进去 ）
	// Process any @Import annotations
	// 处理@Import注解
	processImports(configClass, sourceClass, getImports(sourceClass), filter, true);

17-1. ConfigurationClassParser 类 （ collectImports(sourceClass, imports, visited); 点进去 ）
	private Set<SourceClass> getImports(SourceClass sourceClass) throws IOException {
	// 创建集合，存储包含@Import注解的类
	Set<SourceClass> imports = new LinkedHashSet<>();
	// 创建集合，为了实现递归调用
	Set<SourceClass> visited = new LinkedHashSet<>();
	// 收集@Import注解的类
	collectImports(sourceClass, imports, visited);
	return imports;
	}

18-1. ConfigurationClassParser 类 （ 这里就是 获取Import注解的值 ）
	private void collectImports(SourceClass sourceClass, Set<SourceClass> imports, Set<SourceClass> visited)
	throws IOException {
	
			if (visited.add(sourceClass)) {
				for (SourceClass annotation : sourceClass.getAnnotations()) {
					String annName = annotation.getMetadata().getClassName();
					// 递归处理其他可能包含@Import的注解类
					if (!annName.equals(Import.class.getName())) {
						collectImports(annotation, imports, visited);
					}
				}
				// 获取Import注解的值
				imports.addAll(sourceClass.getAnnotationAttributes(Import.class.getName(), "value"));
			}
		}

16-2. ConfigurationClassParser 类 （ processImports( 点进去 ）
    1. （ 这里是 获取引入的类，然后使用递归方式将这些类中同样添加了@Import注解引用的类  ）
        // 获取引入的类，然后使用递归方式将这些类中同样添加了@Import注解引用的类
        String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
        Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
        // 递归处理，被Import进来的类也有可能@Import注解
        processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);

    2. 这里是 （ 这里完成之后 才能找到 getCandidateConfigurations( 这个方法的调用 ） 
        // Candidate class is an ImportBeanDefinitionRegistrar ->
	    // delegate to it to register additional bean definitions
	    // 候选类是ImportBeanDefinitionRegistrar  -> 委托给当前注册器注册其他bean
 	    Class<?> candidateClass = candidate.loadClass();
	    ImportBeanDefinitionRegistrar registrar =
	    		ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
	    				this.environment, this.resourceLoader, this.registry);
	    /**
	     * 放到当前configClass的importBeanDefinitionRegistrars中
	     * 在ConfigurationClassPostProcessor处理configClass时会随之一起处理
	     */
	    configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());


# 如何调用 AutoConfigurationImportSelector 类 中 protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) 方法
16-2-1. ConfigurationClassParser 类 （ this.deferredImportSelectorHandler.handle( 点进去 ）
    if (selector instanceof DeferredImportSelector) {
        this.deferredImportSelectorHandler.handle(
        configClass, (DeferredImportSelector) selector);
    }

16-2-2. ConfigurationClassParser 类 （ handler.processGroupImports(); 点进去 ）
    handler.processGroupImports();

16-2-3. ConfigurationClassParser 类 （ grouping.getImports() 点进去 ）
    grouping.getImports().forEach(entry -> {
        ConfigurationClass configurationClass = this.configurationClasses.get(
        entry.getMetadata());

16-2-4. ConfigurationClassParser 类 （ this.group.process( 点进去 ）
    this.group.process(deferredImport.getConfigurationClass().getMetadata(),
        deferredImport.getImportSelector());

16-2-5. DeferredImportSelector 类 （ void process(AnnotationMetadata metadata, DeferredImportSelector selector); 点进去 ）
    void process(AnnotationMetadata metadata, DeferredImportSelector selector);

16-2-6. AutoConfigurationImportSelector 类 （ ((AutoConfigurationImportSelector)deferredImportSelector).getAutoConfigurationEntry( 点进去 ）
    AutoConfigurationEntry autoConfigurationEntry = ((AutoConfigurationImportSelector)deferredImportSelector).getAutoConfigurationEntry(this.getAutoConfigurationMetadata(), annotationMetadata);

16-2-7. AutoConfigurationImportSelector 类 （ this.getCandidateConfigurations(annotationMetadata, attributes); 点进去 ）
    List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);

16-2-8. AutoConfigurationImportSelector 类 （ this.getSpringFactoriesLoaderFactoryClass() 这里就是 处理 @EnableAutoConfiguration 注解 ）
    List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());

16-2-9. AutoConfigurationImportSelector 类 （ SpringFactoriesLoader.loadFactoryNames( 点进去 ）
    List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());

16-2-10. SpringFactoriesLoader 类 （ loadSpringFactories 点进去 ）
    public static List<String> loadFactoryNames(Class<?> factoryClass, @Nullable ClassLoader classLoader) {
        String factoryClassName = factoryClass.getName();
        return (List)loadSpringFactories(classLoader).getOrDefault(factoryClassName, Collections.emptyList());
    }

16-2-11. SpringFactoriesLoader 类 （ 这里就是加载 META-INF/spring.factories ）
    Enumeration<URL> urls = classLoader != null ? classLoader.getResources("META-INF/spring.factories") : ClassLoader.getSystemResources("META-INF/spring.factories");
## 测试
    详见  mjj_spring总结_重要/spring源码精讲_spring流程概述_@Import的解析_测试.png
## 如何回答自动装配流程
    当我们在执行spring里面的 BeanFactoryPostProcessor 的时候，里面有一个核心处理类 ，ConfigurationClassPostProcessor ，这个类主要用来处理我们相关的一些注解的解析工作
    这些解析工作包含@Component、@PropertySource、@Import 等，在我们解析 @Import 的时候 ，我们会从我们的启动类开始一步一步查找，这时候会发现里面会识别到一个 AutoConfigurationImportSelector 类，
    这个类在进行解析的时候，会有一个延迟加载的属性，在延迟加载的时候，会通过 .getImports() 方法，这个 .getImports() 里面会获取到 一个 AutoConfigurationEntry 对象 ，
    在获取这个 AutoConfigurationEntry 对象 的时候 ，会调用里面的 .getCandidateConfigurations(annotationMetadata, attributes) 这个方法 ，此时就能把 配置文件里面所对应的属性值
    都加载进来 来完成整体的 自动装配 这个环节