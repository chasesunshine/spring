# 解析：
    1、哪些类需要进行相关的切入:expression,pointcut
    2、额外的逻辑处理，有几个通知消息或者说有哪些逻辑可以被执行before,after，afterThrowing,afterReturing ,，around: advisor---》 advice
    3、额外的处理逻辑的类是哪个，也就是哪个切面aspect

以下是针对 advisor 的源码解析，目的是 创建AspectJAroundAdvice对象并 set到 AspectJPointcutAdvisor#0 这个对象的 有参构造 里面
然后将 advisor add到 List<Advisor> advisors = new ArrayList<>(); 这个list里面 （具体参考 mjj_spring总结_重要/aop通知讲解.jpg ）

# 源码解析流程：
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


12. AbstractAutowireCapableBeanFactory 类 （ populateBean(beanName, mbd, instanceWrapper); 点进去）
        // 对bean的属性进行填充，将各个属性值注入，其中，可能存在依赖于其他bean的属性，则会递归初始化依赖的bean
        populateBean(beanName, mbd, instanceWrapper);


* 这地方 已经 创建完毕 person 对象 ， 但是发现要 填充 address 属性 ， 所以 后续 要进行 address 对象的 创建和属性填充


13. AbstractAutowireCapableBeanFactory 类 （ applyPropertyValues(beanName, mbd, bw, pvs); 点进去 ）
		//如果pvs不为null
		if (pvs != null) {
			//应用给定的属性值，解决任何在这个bean工厂运行时其他bean的引用。必须使用深拷贝，所以我们 不会永久地修改这个属性
			applyPropertyValues(beanName, mbd, bw, pvs);
		}


14. AbstractAutowireCapableBeanFactory 类 （ Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue); 点进去 ）
        //交由valueResolver根据pv解析出originalValue所封装的对象
        Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);


15. BeanDefinitionValueResolver 类 （ return resolveReference(argName, ref); 点进去 ）
        //解析出对应ref所封装的Bean元信息(即Bean名,Bean类型)的Bean对象:
        return resolveReference(argName, ref);


16. BeanDefinitionValueResolver 类 （ bean = this.beanFactory.getBean(resolvedName); 点进去 ）
        //获取resolvedName的Bean对象
        bean = this.beanFactory.getBean(resolvedName);


17. AbstractBeanFactory 类 （ return doGetBean(name, null, null, false); 点进去 ）
        @Override
        public Object getBean(String name) throws BeansException {
        	// 此方法是实际获取bean的方法，也是触发依赖注入的方法
        	return doGetBean(name, null, null, false);
        }


18. AbstractBeanFactory 类 （ getSingleton(beanName, () -> { 点进去 ）
        // Create bean instance.
        // 创建bean的实例对象
        if (mbd.isSingleton()) {
        	// 返回以beanName的(原始)单例对象，如果尚未注册，则使用singletonFactory创建并注册一个对象:
        	sharedInstance = getSingleton(beanName, () -> {
        		try {
        			// 为给定的合并后BeanDefinition(和参数)创建一个bean实例
        			return createBean(beanName, mbd, args);
        		}
        		catch (BeansException ex) {
        			// Explicitly remove instance from singleton cache: It might have been put there
        			// eagerly by the creation process, to allow for circular reference resolution.
        			// Also remove any beans that received a temporary reference to the bean.
        			// 显示地从单例缓存中删除实例：它可能是由创建过程急切地放在那里，以允许循环引用解析。还要删除
        			// 接收到该Bean临时引用的任何Bean
        			// 销毁给定的bean。如果找到相应的一次性Bean实例，则委托给destoryBean
        			destroySingleton(beanName);
        			// 重新抛出ex
        			throw ex;
        		}
        	});
        	// 从beanInstance中获取公开的Bean对象，主要处理beanInstance是FactoryBean对象的情况，如果不是
        	// FactoryBean会直接返回beanInstance实例
        	bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
        }


19. DefaultSingletonBeanRegistry 类 （ singletonObject = singletonFactory.getObject(); 点进去 ）
        try {
        	// 从单例工厂中获取对象
        	singletonObject = singletonFactory.getObject();
        	// 生成了新的单例对象的标记为true，表示生成了新的单例对象
        	newSingleton = true;
        }


20. ObjectFactory 类 （ 	T getObject() throws BeansException; 点进去 ）
        T getObject() throws BeansException;


21. AbstractBeanFactory 类 （ prototypeInstance = createBean(beanName, mbd, args); 点进去 ）
        // Create bean instance.
        // 创建bean的实例对象
        if (mbd.isSingleton()) {
        	// 返回以beanName的(原始)单例对象，如果尚未注册，则使用singletonFactory创建并注册一个对象:
        	sharedInstance = getSingleton(beanName, () -> {
        		try {
        			// 为给定的合并后BeanDefinition(和参数)创建一个bean实例
        			return createBean(beanName, mbd, args);
        		}
        		catch (BeansException ex) {
        			// Explicitly remove instance from singleton cache: It might have been put there
        			// eagerly by the creation process, to allow for circular reference resolution.
        			// Also remove any beans that received a temporary reference to the bean.
        			// 显示地从单例缓存中删除实例：它可能是由创建过程急切地放在那里，以允许循环引用解析。还要删除
        			// 接收到该Bean临时引用的任何Bean
        			// 销毁给定的bean。如果找到相应的一次性Bean实例，则委托给destoryBean
        			destroySingleton(beanName);
        			// 重新抛出ex
        			throw ex;
        		}
        	});
        	// 从beanInstance中获取公开的Bean对象，主要处理beanInstance是FactoryBean对象的情况，如果不是
        	// FactoryBean会直接返回beanInstance实例
        	bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
        }


22. AbstractBeanFactory 类 （ protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) 点进去 ）
        protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        			throws BeanCreationException;


23. AbstractAutowireCapableBeanFactory 类 （ Object bean = resolveBeforeInstantiation(beanName, mbdToUse); 点进去 ）
        // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
        // 给BeanPostProcessors一个机会来返回代理来替代真正的实例，应用实例化前的前置处理器,用户自定义动态代理的方式，针对于当前的被代理类需要经过标准的代理流程来创建对象
        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);


24. AbstractAutowireCapableBeanFactory 类 （ bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName); 点进去 ）
		if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
			// Make sure bean class is actually resolved at this point.
			// 确认beanclass确实在此处进行处理
			// 判断当前mbd是否是合成的，只有在实现aop的时候synthetic的值才为true，并且是否实现了InstantiationAwareBeanPostProcessor接口
			if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
				// 获取类型
				Class<?> targetType = determineTargetType(beanName, mbd);
				if (targetType != null) {
					bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
					if (bean != null) {
						bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
					}
				}
			}
			// 是否解析了
			mbd.beforeInstantiationResolved = (bean != null);
		}


25. AbstractAutowireCapableBeanFactory 类 （ Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName); 点进去 ）
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof InstantiationAwareBeanPostProcessor) {
				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
				Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
				if (result != null) {
					return result;
				}
			}
		}


26. InstantiationAwareBeanPostProcessor 类 （ 点进去 ）
        @Nullable
        default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        	return null;
        }


27. AbstractAutoProxyCreator 类 （ shouldSkip(beanClass, beanName) 点进去 ）
        if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
        	// 要跳过的直接设置FALSE
        	this.advisedBeans.put(cacheKey, Boolean.FALSE);
        	return null;
        }


28. AbstractAutoProxyCreator 类 （ 点进去 ）
        protected boolean shouldSkip(Class<?> beanClass, String beanName) {
        	return AutoProxyUtils.isOriginalInstance(beanName, beanClass);
        }


29. AspectJAwareAdvisorAutoProxyCreator 类 （ List<Advisor> candidateAdvisors = findCandidateAdvisors(); 点进去 ）
		List<Advisor> candidateAdvisors = findCandidateAdvisors();



30. AbstractAdvisorAutoProxyCreator 类 （ return this.advisorRetrievalHelper.findAdvisorBeans(); 点进去 ）
        protected List<Advisor> findCandidateAdvisors() {
        	Assert.state(this.advisorRetrievalHelper != null, "No BeanFactoryAdvisorRetrievalHelper available");
        	// 获取所有的增强处理
        	return this.advisorRetrievalHelper.findAdvisorBeans();
        }


31. BeanFactoryAdvisorRetrievalHelper 类 （ 这地方 是针对 advisor 的源码解析，目的是 创建AspectJAroundAdvice对象并 set到 AspectJPointcutAdvisor#0 这个对象的 有参构造 里面 ）
        List<Advisor> advisors = new ArrayList<>();
        ...
        try {
        	// 将当前bean添加到结果中
        	advisors.add(this.beanFactory.getBean(name, Advisor.class));
        }




总结:
1、创建AspectJPointcutAdvisor#0-4,先使用其带参的构造方法进行对象的创建，但是想使用带参数的构造方法必须要把参数对象准备好，因此要准备创建内置包含的对象AspectJAroundAdvice
2、创建AspectJAroundAdvice，也需要使用带参的构造方法进行创建，也需要提前准备好具体的参数对象，包含三个参数:
	1、MethodLocatingFactoryBean
	2、AspectJExpressionPointcut
	3、SimpleBeanFactoryAwareAspectInstanceFactory
3、分别创建上述的三个对象,上述三个对象的创建过程都是调用无参的构造方法，直接反射生成即可,







