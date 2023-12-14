# 注意点：
初始化与循环依赖：
    当实例化完成之后，要开始进行初始化赋值操作了，但是赋值的时候，值的类型有可能是引用类型，需要从 spring容器中 获取具体的某个对象来完成赋值操作，而此时，需要引用的对象可能被创建了，也可能没被创建，
    如果被创建了，那么直接获取即可，如果没有创建，在整个过程中就会涉及到对象的创建过程，而内部对象的创建过程中又会有其他的依赖，其他的依赖中可能包含当前的对象，而此时当前对象还没有创建完成，所以产生了循环依赖问题

# 分析：
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


13. AbstractAutowireCapableBeanFactory 类 （ PropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName); 点进去）
        //postProcessProperties:在工厂将给定的属性值应用到给定Bean之前，对它们进行后处理，不需要任何属性扫描符。该回调方法在未来的版本会被删掉。
        // -- 取而代之的是 postProcessPropertyValues 回调方法。
        // 让ibp对pvs增加对bw的Bean对象的propertyValue，或编辑pvs的proertyValue
        PropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);


14. InstantiationAwareBeanPostProcessor 类 （ 点进去 ）
        @Nullable
        default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
        		throws BeansException {

        	return null;
        }


15. AutowiredAnnotationBeanPostProcessor 类 （  metadata.inject(bean, beanName, pvs); 点进去 ）
        // 进行属性注入
        metadata.inject(bean, beanName, pvs);


16. InjectionMetadata 类 （ element.inject(target, beanName, pvs); 点进去 ）
		if (!elementsToIterate.isEmpty()) {
			for (InjectedElement element : elementsToIterate) {
				if (logger.isTraceEnabled()) {
					logger.trace("Processing injected element of bean '" + beanName + "': " + element);
				}
				element.inject(target, beanName, pvs);
			}
		}


17. InjectionMetadata 类  （ 点进去 ）
        protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs)
                throws Throwable {


18. AutowiredAnnotationBeanPostProcessor 类 （ value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter); 点进去 ）
        // 获取依赖的value值的工作  最终还是委托给beanFactory.resolveDependency()去完成的
        // 这个接口方法由AutowireCapableBeanFactory提供，它提供了从bean工厂里获取依赖值的能力
        value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);


19. AutowireCapableBeanFactory 类 （ 点进去 ）
        @Nullable
        Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;


20. DefaultListableBeanFactory 类 （ result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter); 点进去 ）
        // 解析出与descriptor所包装的对象匹配的候选Bean对象
        result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);


21（1）. DefaultListableBeanFactory 类 （ Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor); 点进去 ）
        //尝试与type匹配的唯一候选bean对象
        //查找与type匹配的候选bean对象,构建成Map，key=bean名,val=Bean对象
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);

21（2）. DefaultListableBeanFactory 类 ( 这地方 让instanceCandidate引用 descriptor对autowiredBeanName解析为该工厂的Bean实例 )
        //如果instanceCandidate是Class实例
        if (instanceCandidate instanceof Class) {
        	//让instanceCandidate引用 descriptor对autowiredBeanName解析为该工厂的Bean实例
        	instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
        }


22. DefaultListableBeanFactory 类 （ addCandidateEntry(result, candidate, descriptor, requiredType); 类 ）
        // 添加一个条目在result中:一个bean实例(如果可用)或仅一个已解析的类型
        addCandidateEntry(result, candidate, descriptor, requiredType);


23. DefaultListableBeanFactory 类 （ getType(candidateName) 点进去 ）
        // 将candidateName和其对应的Class对象绑定到candidates中
        candidates.put(candidateName, getType(candidateName));


24. AbstractBeanFactory 类 （ return getType(name, true); 点进去 ）
        /**
         * 确定具有给定名称的bean类型(为了确定其对象类型，默认让FactoryBean以初始化)
         * @param name the name of the bean to query
         * @return
         * @throws NoSuchBeanDefinitionException
         */
        @Override
        @Nullable
        public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        	return getType(name, true);
        }


25. AbstractBeanFactory 类 （ Object beanInstance = getSingleton(beanName, false); 点进去 ）
		// Check manually registered singletons.
		// 检查手动注册的单例,获取beanName注册的单例对象，但不会创建早期引用
		Object beanInstance = getSingleton(beanName, false);


26. DefaultSingletonBeanRegistry 类 （ 获取以beanName注册的(原始)单例对象 ）
        @Nullable
        protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        	Object singletonObject = this.singletonObjects.get(beanName);
        	if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        			synchronized (this.singletonObjects) {
        				singletonObject = this.earlySingletonObjects.get(beanName);
        				return singletonObject;
        			}
        		}
        	return singletonObject != null ? singletonObject:null;
        }




# 结论： 通过解析 populateBean() 方法源码 来验证 @Autowired 注解 具有 属性注入 的功能







