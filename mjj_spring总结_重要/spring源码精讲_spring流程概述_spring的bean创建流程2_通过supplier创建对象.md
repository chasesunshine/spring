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


9-1. AbstractBeanFactory 类 （ sharedInstance = getSingleton(beanName, () -> 点进去）
	sharedInstance = getSingleton(beanName, () -> {
		try {
			// 为给定的合并后BeanDefinition(和参数)创建一个bean实例
			return createBean(beanName, mbd, args);
		}


9-1-1. DefaultSingletonBeanRegistry 类 （ 这里自己点进去看 ）
	// 从单例工厂中获取对象
	singletonObject = singletonFactory.getObject();


9-2. AbstractBeanFactory 类 （ return createBean(beanName, mbd, args); 点进去 ）
	sharedInstance = getSingleton(beanName, () -> {
		try {
			// 为给定的合并后BeanDefinition(和参数)创建一个bean实例
			return createBean(beanName, mbd, args);
		}


10-2. AbstractBeanFactory 类 （ protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) 点进去 ）
        protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        			throws BeanCreationException;


11-2. AbstractAutowireCapableBeanFactory 类 （ Object beanInstance = doCreateBean(beanName, mbdToUse, args); 点进去）
        try {
        	// 实际创建bean的调用
        	Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        	if (logger.isTraceEnabled()) {
        		logger.trace("Finished creating instance of bean '" + beanName + "'");
        	}
        	return beanInstance;
        }


12-2. AbstractAutowireCapableBeanFactory 类 （ instanceWrapper = createBeanInstance(beanName, mbd, args); 点进去 ）
        // 没有就创建实例
        if (instanceWrapper == null) {
        	// 根据执行bean使用对应的策略创建新的实例，如，工厂方法，构造函数主动注入、简单初始化
        	instanceWrapper = createBeanInstance(beanName, mbd, args);
        }


13-2. AbstractAutowireCapableBeanFactory 类 （ instanceWrapper = createBeanInstance(beanName, mbd, args); 点进去 ）
		// 没有就创建实例
		if (instanceWrapper == null) {
			// 根据执行bean使用对应的策略创建新的实例，如，工厂方法，构造函数主动注入、简单初始化
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}


14-2. AbstractAutowireCapableBeanFactory 类 （ return obtainFromSupplier(instanceSupplier, beanName); 点进去 ）
		// 判断当前beanDefinition中是否包含实例供应器，此处相当于一个回调方法，利用回调方法来创建bean
		Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
			if (instanceSupplier != null) {
			return obtainFromSupplier(instanceSupplier, beanName);
		}


15-2. AbstractAutowireCapableBeanFactory 类 （ instance = instanceSupplier.get(); 这里仔细看 ）
		// 调用supplier的方法
		instance = instanceSupplier.get();