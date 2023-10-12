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


7. DefaultListableBeanFactory 类 （ Object bean = getBean(FACTORY_BEAN_PREFIX + beanName); 点进去 ）
        // 根据&+beanName来获取具体的对象
        Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);


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


13. AbstractAutowireCapableBeanFactory 类 （ return instantiateBean(beanName, mbd); 点进去 ）
        // No special handling: simply use no-arg constructor.
        // 使用默认无参构造函数创建对象，如果没有无参构造且存在多个有参构造且没有@AutoWired注解构造，会报错
        return instantiateBean(beanName, mbd);


14. AbstractAutowireCapableBeanFactory 类 （ beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this); 点进去 ）
        protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
        	try {
        		Object beanInstance;
        		if (System.getSecurityManager() != null) {
        			beanInstance = AccessController.doPrivileged(
        					(PrivilegedAction<Object>) () -> getInstantiationStrategy().instantiate(mbd, beanName, this),
        					getAccessControlContext());
        		}
        		else {
        			// 获取实例化策略并且进行实例化操作
        			beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this);
        		}
        		// 包装成BeanWrapper
        		BeanWrapper bw = new BeanWrapperImpl(beanInstance);
        		initBeanWrapper(bw);
        		return bw;
        	}
        	catch (Throwable ex) {
        		throw new BeanCreationException(
        				mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
        	}
        }


15. InstantiationStrategy 类 （ Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) 点进去 ）
        Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner)
        		throws BeansException;


16. SimpleInstantiationStrategy 类 （ return BeanUtils.instantiateClass(constructorToUse); 点进去 ）
        // 通过反射生成具体的实例化对象
        return BeanUtils.instantiateClass(constructorToUse);


17. BeanUtils 类 （ return ctor.newInstance(argsWithDefaultValues); 完成实例的创建）
        public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
        	ReflectionUtils.makeAccessible(ctor);
        	if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(ctor.getDeclaringClass())) {
        		return KotlinDelegate.instantiateClass(ctor, args);
        	}
        	else {
        		Class<?>[] parameterTypes = ctor.getParameterTypes();
        		Assert.isTrue(args.length <= parameterTypes.length, "Can't specify more arguments than constructor parameters");
        		Object[] argsWithDefaultValues = new Object[args.length];
        		//获得参数
        		for (int i = 0 ; i < args.length; i++) {
        			if (args[i] == null) {
        				Class<?> parameterType = parameterTypes[i];
        				argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
        			}
        			else {
        				argsWithDefaultValues[i] = args[i];
        			}
        		}
        		return ctor.newInstance(argsWithDefaultValues);
        }




















