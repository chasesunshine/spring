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


16. CommonAnnotationBeanPostProcessor 类 （ super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName); 点进去 ）
        @Override
        public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
            // 处理@PostConstruct和@PreDestroy注解
            super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
            //找出beanType所有被@Resource标记的字段和方法封装到InjectionMetadata中
            InjectionMetadata metadata = findResourceMetadata(beanName, beanType, null);
            metadata.checkConfigMembers(beanDefinition);
        }


17. InitDestroyAnnotationBeanPostProcessor 类 （ LifecycleMetadata metadata = findLifecycleMetadata(beanType); 点进去 ）
        @Override
        public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
            // 调用方法获取生命周期元数据并保存
            LifecycleMetadata metadata = findLifecycleMetadata(beanType);
            // 验证相关方法
            metadata.checkConfigMembers(beanDefinition);
        }


18. InitDestroyAnnotationBeanPostProcessor 类 （ metadata = buildLifecycleMetadata(clazz); 点进去 ）
        // 构建生命周期元数据
		metadata = buildLifecycleMetadata(clazz);


19-1. InitDestroyAnnotationBeanPostProcessor 类
        // 实例化后的回调方法（@PostConstruct）
		List<LifecycleElement> initMethods = new ArrayList<>();


19-2. InitDestroyAnnotationBeanPostProcessor 类
        // 销毁前的回调方法（@PreDestroy）
		List<LifecycleElement> destroyMethods = new ArrayList<>();


19-3. InitDestroyAnnotationBeanPostProcessor 类
       // 当前方法的注解中包含initAnnotationType注解时（@PostConstruct）
       if (this.initAnnotationType != null && method.isAnnotationPresent(this.initAnnotationType)) {
            // 如果有，把它封装成LifecycleElement对象，存储起来
            LifecycleElement element = new LifecycleElement(method);
            // 将创建好的元素添加到集合中
            currInitMethods.add(element);
            if (logger.isTraceEnabled()) {
                logger.trace("Found init method on class [" + clazz.getName() + "]: " + method);
            }
       });


19-3. InitDestroyAnnotationBeanPostProcessor 类
        // 当前方法的注解中包含destroyAnnotationType注解（@PreDestroy）
        if (this.destroyAnnotationType != null && method.isAnnotationPresent(this.destroyAnnotationType)) {
        	// 如果有，把它封装成LifecycleElement对象，存储起来
        	currDestroyMethods.add(new LifecycleElement(method));
        	if (logger.isTraceEnabled()) {
        		logger.trace("Found destroy method on class [" + clazz.getName() + "]: " + method);
        	}
        }


20. AbstractAutowireCapableBeanFactory 类 （ 这里表示的是 以上 19-1、19-2、19-3、19-4 的 初始化方法和销毁方法是在这里执行的）
        if (mbd == null || !mbd.isSynthetic()) {
    		// 将BeanPostProcessors应用到给定的现有Bean实例，调用它们的postProcessBeforeInitialization初始化方法。
    		// 返回的Bean实例可能是原始Bean包装器
    		wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    	}


21. InitDestroyAnnotationBeanPostProcessor 类 （ 这里表示的是 以上 19-1、19-2、19-3、19-4 的 初始化方法和销毁方法是在这里执行的）
		/**
		 * 调用前面注册的初始化方法集合checkedInitMethods的每一个方法
		 * @param target
		 * @param beanName
		 * @throws Throwable
		 */
		public void invokeInitMethods(Object target, String beanName) throws Throwable {
			Collection<LifecycleElement> checkedInitMethods = this.checkedInitMethods;
			Collection<LifecycleElement> initMethodsToIterate =
					(checkedInitMethods != null ? checkedInitMethods : this.initMethods);
			if (!initMethodsToIterate.isEmpty()) {
				for (LifecycleElement element : initMethodsToIterate) {
					if (logger.isTraceEnabled()) {
						logger.trace("Invoking init method on bean '" + beanName + "': " + element.getMethod());
					}
					element.invoke(target);
				}
			}
		}

		public void invokeDestroyMethods(Object target, String beanName) throws Throwable {
			Collection<LifecycleElement> checkedDestroyMethods = this.checkedDestroyMethods;
			Collection<LifecycleElement> destroyMethodsToUse =
					(checkedDestroyMethods != null ? checkedDestroyMethods : this.destroyMethods);
			if (!destroyMethodsToUse.isEmpty()) {
				for (LifecycleElement element : destroyMethodsToUse) {
					if (logger.isTraceEnabled()) {
						logger.trace("Invoking destroy method on bean '" + beanName + "': " + element.getMethod());
					}
					element.invoke(target);
				}
			}
		}




备注：
    以上 19-1、19-2、19-3、19-4
    表示的是 spring 生命周期 创建 bean对象 的整体流程中， 初始化和销毁 bean对象 的过程，
    通过扫描 bean对象 中带有 @PostConstruct 和 @PreDestroy 注解的方法 来代表 初始化和销毁方法，
    在 spring bean 生命周期，执行 beanpostprocessor.before 的时候 执行 初始化和销毁方法（以上 20、21）



