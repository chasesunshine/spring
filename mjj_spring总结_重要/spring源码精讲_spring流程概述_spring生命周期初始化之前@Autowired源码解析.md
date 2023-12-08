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


4. AbstractApplicationContext 类 （ finishBeanFactoryInitialization(beanFactory); 点进去 ）
        // Instantiate all remaining (non-lazy-init) singletons.
        // 初始化剩下的单实例（非懒加载的）
        finishBeanFactoryInitialization(beanFactory);


5. AbstractApplicationContext 类 （ beanFactory.preInstantiateSingletons(); 点进去 ）
        // Instantiate all remaining (non-lazy-init) singletons.
        // 实例化剩下的单例对象
        beanFactory.preInstantiateSingletons();


6. ConfigurableListableBeanFactory 类 （ void preInstantiateSingletons() throws BeansException; 点进去 ）
        void preInstantiateSingletons() throws BeansException;


7. DefaultListableBeanFactory 类 （ getBean(beanName); 点进去 ）
        // 如果beanName对应的bean不是FactoryBean，只是普通的bean，通过beanName获取bean实例
        getBean(beanName);


8. AbstractBeanFactory 类 （ return doGetBean(name, null, null, false); 点进去 ）
        @Override
        public Object getBean(String name) throws BeansException {
        	// 此方法是实际获取bean的方法，也是触发依赖注入的方法
        	return doGetBean(name, null, null, false);
        }


9. AbstractBeanFactory 类 （ prototypeInstance = createBean(beanName, mbd, args); 点进去 ）
        // 原型模式的bean对象创建
        else if (mbd.isPrototype()) {
        	// It's a prototype -> create a new instance.
        	// 它是一个原型 -> 创建一个新实例
        	// 定义prototype实例
        	Object prototypeInstance = null;
        	try {
        		// 创建Prototype对象前的准备工作，默认实现将beanName添加到prototypesCurrentlyInCreation中
        		beforePrototypeCreation(beanName);
        		// 为mbd(和参数)创建一个bean实例
        		prototypeInstance = createBean(beanName, mbd, args);
        	}
        	finally {
        		// 创建完prototype实例后的回调，默认是将beanName从prototypesCurrentlyInCreation移除
        		afterPrototypeCreation(beanName);
        	}
        	// 从beanInstance中获取公开的Bean对象，主要处理beanInstance是FactoryBean对象的情况，如果不是
        	// FactoryBean会直接返回beanInstance实例
        	bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
        }


10. AbstractBeanFactory 类 （ protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) 点进去 ）
        protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        			throws BeanCreationException;


11. AbstractAutowireCapableBeanFactory 类 （ Object beanInstance = doCreateBean(beanName, mbdToUse, args); 点进去）
        try {
        	// 实际创建bean的调用
        	Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        	if (logger.isTraceEnabled()) {
        		logger.trace("Finished creating instance of bean '" + beanName + "'");
        	}
        	return beanInstance;
        }


12. AbstractAutowireCapableBeanFactory 类 （ instanceWrapper = createBeanInstance(beanName, mbd, args); 点进去 ）
        // 没有就创建实例
        if (instanceWrapper == null) {
        	// 根据执行bean使用对应的策略创建新的实例，如，工厂方法，构造函数主动注入、简单初始化
        	instanceWrapper = createBeanInstance(beanName, mbd, args);
        }


13. AbstractAutowireCapableBeanFactory 类 （ applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName); 点进去 ）
	   // MergedBeanDefinitionPostProcessor后置处理器修改合并bean的定义
	   applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);


14. AbstractAutowireCapableBeanFactory 类 （ bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName); 点进去 ）
        bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName);


15. MergedBeanDefinitionPostProcessor 类 （ void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName); 点进去 ）
        void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);


16. AutowiredAnnotationBeanPostProcessor 类 （ InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null); 点进去 ）
        /**
         * 处理合并的bean定义信息
         * 1、解析@Autowired等注解然后转换
         * 2、把注解信息转换为InjectionMetadata然后缓存到上面的injectionMetadataCache里面
         * @param beanDefinition the merged bean definition for the bean
         * @param beanType the actual type of the managed bean instance
         * @param beanName the name of the bean
         */
        @Override
        public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
            // 解析注解并缓存
            InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
            metadata.checkConfigMembers(beanDefinition);
        }


17.  AutowiredAnnotationBeanPostProcessor 类 （ metadata = buildAutowiringMetadata(clazz); 点进去 ）
        // 构建自动装配的属性和方法元数据
        metadata = buildAutowiringMetadata(clazz);
        this.injectionMetadataCache.put(cacheKey, metadata);


18. AutowiredAnnotationBeanPostProcessor 类 （ MergedAnnotation<?> ann = findAutowiredAnnotation(field); 点进去 ）
        // 遍历类中的每个属性，判断属性是否包含指定的属性(通过 findAutowiredAnnotation 方法)
        // 如果存在则保存，这里注意，属性保存的类型是 AutowiredFieldElement
        ReflectionUtils.doWithLocalFields(targetClass, field -> {
        	MergedAnnotation<?> ann = findAutowiredAnnotation(field);
        	if (ann != null) {
        		//Autowired注解不支持静态方法
        		if (Modifier.isStatic(field.getModifiers())) {
        			if (logger.isInfoEnabled()) {
        				logger.info("Autowired annotation is not supported on static fields: " + field);
        			}
        			return;
        		}
        		//查看是否是required的
        		boolean required = determineRequiredStatus(ann);
        		currElements.add(new AutowiredFieldElement(field, required));
        	}
        });


19. AutowiredAnnotationBeanPostProcessor 类 （ 这地方就是 解析 @Autowired 的地方 ）
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {



20. AutowiredAnnotationBeanPostProcessor 类 （ this.autowiredAnnotationTypes.add(Autowired.class); 和 this.autowiredAnnotationTypes.add(Value.class); 是把 @Autowired 和 @Value 提前初始化到 set集合 以便后续使用 ）
        /**
         * 构造方法，完成对应注解的注入
         *
         * Create a new {@code AutowiredAnnotationBeanPostProcessor} for Spring's
         * standard {@link Autowired @Autowired} and {@link Value @Value} annotations.
         * <p>Also supports JSR-330's {@link javax.inject.Inject @Inject} annotation,
         * if available.
         */
        @SuppressWarnings("unchecked")
        public AutowiredAnnotationBeanPostProcessor() {
        	this.autowiredAnnotationTypes.add(Autowired.class);
        	this.autowiredAnnotationTypes.add(Value.class);
        	try {
        		this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
        				ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
        		logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
        	}
        	catch (ClassNotFoundException ex) {
        		// JSR-330 API not available - simply skip.
        	}
        }


21. AutowiredAnnotationBeanPostProcessor 类 （ 这地方 是从 容器中 获取对应的 value ， 然后 给属性赋值 ，完成 @Autowired 和 @Value 的操作 ）
        /**
         * 完成属性的注入
         * @param bean
         * @param beanName
         * @param pvs
         * @throws Throwable
         */
        @Override
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            ...

            try {
            	// 获取依赖的value值的工作  最终还是委托给beanFactory.resolveDependency()去完成的
            	// 这个接口方法由AutowireCapableBeanFactory提供，它提供了从bean工厂里获取依赖值的能力
            	value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
            }

            ...

            if (value != null) {
            	// 通过反射，给属性赋值
            	ReflectionUtils.makeAccessible(field);
            	field.set(bean, value);
            }
        }