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

8. PostProcessorRegistrationDelegate 类 （ registryProcessor.postProcessBeanDefinitionRegistry(registry); 点进去 ）
   // 直接执行BeanDefinitionRegistryPostProcessor接口中的postProcessBeanDefinitionRegistry方法
   registryProcessor.postProcessBeanDefinitionRegistry(registry);

9. BeanDefinitionRegistryPostProcessor 类 （ void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException; 点进去 ）
   void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

10. ConfigurationClassPostProcessor 类 （ processConfigBeanDefinitions(registry); 点进去 ）
    // 处理配置类的bean定义信息
    processConfigBeanDefinitions(registry);

11-1. ConfigurationClassPostProcessor 类 （ ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory) 点进去 ）
    // 判断当前BeanDefinition是否是一个配置类，并为BeanDefinition设置属性为lite或者full，此处设置属性值是为了后续进行调用
    // 如果Configuration配置proxyBeanMethods代理为true则为full
    // 如果加了@Bean、@Component、@ComponentScan、@Import、@ImportResource注解，则设置为lite
    // 如果配置类上被@Order注解标注，则设置BeanDefinition的order属性值
    else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
    // 添加到对应的集合对象中
    configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
    }

12-1. ConfigurationClassUtils 类 （ isConfigurationCandidate(metadata) 点进去 ）
    // 如果bean被@configuration注解标注，且被注解@Component，@ComponentScan、@Import、@ImportResource或者@Bean标记的方法，则将bean定义标记为lite
    else if (config != null || isConfigurationCandidate(metadata)) {
    beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
    }

12-1-1. ConfigurationClassUtils 类 （ 检查是否被注解@Component、@ComponentScan、@Import、@ImportResource标注 ）
    // Any of the typical annotations found?
    // 检查是否被注解@Component、@ComponentScan、@Import、@ImportResource标注
    for (String indicator : candidateIndicators) {
    if (metadata.isAnnotated(indicator)) {
    return true;
    }
    }

12-1-2. ConfigurationClassUtils 类 （ getOrder(metadata); 点进去 结束 ）
    // It's a full or lite configuration candidate... Let's determine the order value, if any.
    // bean定义是一个标记为full或lite的候选项，如果设置order则设置order属性值
    Integer order = getOrder(metadata);



11-2. ConfigurationClassPostProcessor 类 （ parser.parse(candidates); 点进去 ）
    // 解析带有@Controller、@Import、@ImportResource、@ComponentScan、@ComponentScans、@Bean的BeanDefinition
    parser.parse(candidates);

12-2. ConfigurationClassParser 类 （ parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName()); 点进去 ）
    // 注解类型
    if (bd instanceof AnnotatedBeanDefinition) {
        parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
    }

13-2. ConfigurationClassParser 类 （ processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER); 点进去 ）
    // 根据注解元数据和beanName解析配置文件，有注解元数据
    protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
        processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
    }

14-2. ConfigurationClassParser 类 （ sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter); 点进去 这个方法点进去要仔细看 ）
    do {
        // 解析各种注解
        sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
    }

14-2-1. ConfigurationClassParser 类 （ processMemberClasses(configClass, sourceClass, filter); 点进去 ）
    // Recursively process any member (nested) classes first
    // 递归处理内部类，因为内部类也是一个配置类，配置类上有@configuration注解，该注解继承@Component，if判断为true，调用processMemberClasses方法，递归解析配置类中的内部类
    processMemberClasses(configClass, sourceClass, filter);

14-2-1-1. ConfigurationClassParser 类 （ 这里需要好好看一下 ）


14-2-2. ConfigurationClassParser 类 （ this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName()); 点进去 ）
    // The config class is annotated with @ComponentScan -> perform the scan immediately
    // 解析@ComponentScan和@ComponentScans配置的扫描的包所包含的类
    // 比如 basePackages = com.mashibing, 那么在这一步会扫描出这个包及子包下的class，然后将其解析成BeanDefinition
    // (BeanDefinition可以理解为等价于BeanDefinitionHolder)
    Set<BeanDefinitionHolder> scannedBeanDefinitions =
    this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());

14-2-2.1. ComponentScanAnnotationParser 类 （ return scanner.doScan(StringUtils.toStringArray(basePackages)); 点进去 ）
    // 开始执行扫描，最终的扫描器是ClassPathBeanDefinitionScanner
    return scanner.doScan(StringUtils.toStringArray(basePackages));

14-2-2.2. ComponentScanAnnotationParser 类 （ Set<BeanDefinition> candidates = findCandidateComponents(basePackage); 点进去 ）
    // 扫描basePackage,将符合要求的bean定义全部找出来
    Set<BeanDefinition> candidates = findCandidateComponents(basePackage);

14-2-2.3. ClassPathScanningCandidateComponentProvider 类 （ return scanCandidateComponents(basePackage); 点进去 ）
    return scanCandidateComponents(basePackage);

14-2-2.3. ClassPathScanningCandidateComponentProvider 类 （ 这里需要好好看一下 ）


14-2-3. ConfigurationClassParser 类 （ processImports(configClass, sourceClass, getImports(sourceClass), filter, true); 点进去 ）
    // Process any @Import annotations
    // 处理@Import注解
    processImports(configClass, sourceClass, getImports(sourceClass), filter, true);

14-2-3-1. ConfigurationClassParser 类 （ 这里需要好好看一下 ）


14-2-4. ConfigurationClassParser 类 （ Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass); 点进去 ）
    // Process individual @Bean methods
    // 处理加了@Bean注解的方法，将@Bean方法转化为BeanMethod对象，保存再集合中
    Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);

14-2-4-1. ConfigurationClassParser 类 （ 这里需要好好看一下 ， 处理加了@Bean注解的方法，将@Bean方法转化为BeanMethod对象，保存再集合中 ）
    AnnotationMetadata asm =
    this.metadataReaderFactory.getMetadataReader(original.getClassName()).getAnnotationMetadata();
