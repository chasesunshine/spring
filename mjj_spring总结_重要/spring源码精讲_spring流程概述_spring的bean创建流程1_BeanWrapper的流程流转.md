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


11-1. AbstractAutowireCapableBeanFactory 类 （ return doResolveBeanClass(mbd, typesToMatch); 点进去）
		// 进行详细的处理解析过程
		return doResolveBeanClass(mbd, typesToMatch);


12-1. AbstractBeanFactory 类 （ return mbd.resolveBeanClass(beanClassLoader); 点进去 ）
		/ Resolve regularly, caching the result in the BeanDefinition...
		// 定期解析，将结果缓存在BeanDefinition中...
		// 使用classLoader加载当前BeanDefinitiond对象所配置的Bean类名的Class对象（每次调用都会重新加载,可通过
		// AbstractBeanDefinition#getBeanClass 获取缓存）
		return mbd.resolveBeanClass(beanClassLoader);


13-1. AbstractBeanDefinition 类 （ Class<?> resolvedClass = ClassUtils.forName(className, classLoader); 点进去 ）
		// 获取当前bean对应的Class对象
		Class<?> resolvedClass = ClassUtils.forName(className, classLoader);


14-1. ClassUtils 类 （ 这里 返回与给定的字符串名称相关联类或接口的Class对象。 ）
		// 返回与给定的字符串名称相关联类或接口的Class对象。
		return Class.forName(name, false, clToUse);


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


13-2-1. AbstractAutowireCapableBeanFactory 类 （ Supplier<?> instanceSupplier = mbd.getInstanceSupplier(); 这地方是注意点 ）
		// 判断当前beanDefinition中是否包含实例供应器，此处相当于一个回调方法，利用回调方法来创建bean
		Supplier<?> instanceSupplier = mbd.getInstanceSupplier();


13-2-2. AbstractAutowireCapableBeanFactory 类 （ .instantiate(mbd, beanName, this); 点进去 ）
		// 获取实例化策略并且进行实例化操作
		beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this);


14-2. InstantiationStrategy 类 （ Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) 点进去 ）
		Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner)
			throws BeansException;


15-2. SimpleInstantiationStrategy 类 （ return instantiateWithMethodInjection(bd, beanName, owner); 点进去 ）
		// Must generate CGLIB subclass.
		// 必须生成cglib子类
		return instantiateWithMethodInjection(bd, beanName, owner);


16-2. SimpleInstantiationStrategy 类 （ 点 它的实现 ）
		protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
			throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
		}


17-2. CglibSubclassingInstantiationStrategy 类 （ return instantiateWithMethodInjection(bd, beanName, owner, null); 点进去 ）
		// 子类重写SimpleInstantiationStrategy中的instantiateWithMethodInjection方法
		@Override
			protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
			return instantiateWithMethodInjection(bd, beanName, owner, null);
		}


18-2. CglibSubclassingInstantiationStrategy 类 （ .instantiate(ctor, args); 点进去 ） 
		@Override
		protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
		@Nullable Constructor<?> ctor, Object... args) {
			
			// Must generate CGLIB subclass...
			// 必须生成一个cglib的子类
			return new CglibSubclassCreator(bd, owner).instantiate(ctor, args);
		}

19-2. CglibSubclassingInstantiationStrategy 类 （ instance = BeanUtils.instantiateClass(subclass); 点进去 ）
		// 如果构造器等于空，那么直接通过反射来实例化对象
		if (ctor == null) {
			instance = BeanUtils.instantiateClass(subclass);
		}


20-2. BeanUtils 类 （ return instantiateClass(clazz.getDeclaredConstructor()); 点进去 ）
		try {
			return instantiateClass(clazz.getDeclaredConstructor());
		}


21-2. BeanUtils 类 （ 这里就是 通过构造方法 创建实例对象 ）
		return ctor.newInstance(argsWithDefaultValues);