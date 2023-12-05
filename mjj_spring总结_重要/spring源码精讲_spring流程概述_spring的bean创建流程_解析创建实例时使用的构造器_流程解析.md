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


13-1. AbstractAutowireCapableBeanFactory 类 （ 这个字段比较关键 ， 因为 按照构造器创建对象的方式很多，比如 无参构造创建 或者 有参构造创建 的方式，为了 防止重复创建同一个bean ， 所以 用这个字段来进行 标记 ）
        // Shortcut when re-creating the same bean...
        // 标记下，防止重复创建同一个bean
        boolean resolved = false;


13-2. AbstractAutowireCapableBeanFactory 类 （ return autowireConstructor(beanName, mbd, null, null); 点进去 ）
        // 有构造参数的或者工厂方法
        if (resolved) {
            // 构造器有参数
            if (autowireNecessary) {
                // 构造函数自动注入
                return autowireConstructor(beanName, mbd, null, null);
            }
            else {
                // 使用默认构造函数构造
                return instantiateBean(beanName, mbd);
            }
        }


13-2-1. AbstractAutowireCapableBeanFactory 类 （ .autowireConstructor(beanName, mbd, ctors, explicitArgs); 点进去 ）
        return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);、


13-2-1-1. ConstructorResolver 类 （ 获取到 所有public 构造器 后续的流程需要用的 ）
        // 使用public的构造器或者所有构造器
        candidates = (mbd.isNonPublicAccessAllowed() ?
        		beanClass.getDeclaredConstructors() : beanClass.getConstructors());


13-2-2-1. ConstructorResolver 类 （ argsHolderToUse.storeCache(mbd, constructorToUse); 点进去 ）
        /**
         * 没有传入参与构造函数参数列表的参数时，对构造函数缓存到BeanDefinition中
         * 	1、缓存BeanDefinition进行实例化时使用的构造函数
         * 	2、缓存BeanDefinition代表的Bean的构造函数已解析完标识
         * 	3、缓存参与构造函数参数列表值的参数列表
         */
        if (explicitArgs == null && argsHolderToUse != null) {
            // 将解析的构造函数加入缓存
            argsHolderToUse.storeCache(mbd, constructorToUse);
        }


13-2-2-2. ConstructorResolver 类 （ mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod; 这地方就是把 spring 第一次创建一个bean对象时使用的 构造方法 缓存到 mbd.resolvedConstructorOrFactoryMethod 中，将ArgumentsHolder所得到的参数值属性缓存到mbd对应的属性中 ）
        /**
		 * 将ArgumentsHolder所得到的参数值属性缓存到mbd对应的属性中
		 * @param mbd
		 * @param constructorOrFactoryMethod
		 */
		public void storeCache(RootBeanDefinition mbd, Executable constructorOrFactoryMethod) {
			// 使用mbd的构造函数通用锁【{@link RootBeanDefinition#constructorArgumentLock}】加锁以保证线程安全
			synchronized (mbd.constructorArgumentLock) {
				// 让mbd的已解析的构造函数或工厂方法【{@link RootBeanDefinition#resolvedConstructorOrFactoryMethod}】引用constructorOrFactoryMethod
				mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
				// 将mdb的构造函数参数已解析标记【{@link RootBeanDefinition#constructorArgumentsResolved}】设置为true
				mbd.constructorArgumentsResolved = true;
				// 如果resolveNecessary为true，表示参数还需要进一步解析
				if (this.resolveNecessary) {
					// 让mbd的缓存部分准备好的构造函数参数值属性【{@link RootBeanDefinition#preparedConstructorArguments}】引用preparedArguments
					mbd.preparedConstructorArguments = this.preparedArguments;
				}
				else {
					// 让mbd的缓存完全解析的构造函数参数属性【{@link RootBeanDefinition#resolvedConstructorArguments}】引用arguments
					mbd.resolvedConstructorArguments = this.arguments;
				}
			}
		}


13-3. AbstractAutowireCapableBeanFactory 类 （ determineConstructorsFromBeanPostProcessors(beanClass, beanName); 点进去 ）
        // Candidate constructors for autowiring?
		// 从bean后置处理器中为自动装配寻找构造方法, 有且仅有一个有参构造或者有且仅有@Autowired注解构造
		Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);


13-3-1. AbstractAutowireCapableBeanFactory 类 （ ibp.determineCandidateConstructors(beanClass, beanName); 点进去 ）
        Constructor<?>[] ctors = ibp.determineCandidateConstructors(beanClass, beanName);


13-3-2. SmartInstantiationAwareBeanPostProcessor 类 （ 点进去 ）
        @Nullable
        default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
        		throws BeansException {

        	return null;
        }


13-3-3. AutowiredAnnotationBeanPostProcessor 类 （ 这地方自己去看代码 ，主要目的是 - 从bean后置处理器中为自动装配寻找构造方法, 有且仅有一个有参构造或者有且仅有@Autowired注解构造 ）
    /**
	 * 获取构造器集合
	 * 		如果有多个Autowired，required为true，不管有没有默认构造方法，会报异常
	 * 		如果只有一个Autowired，required为false，没有默认构造方法，会报警告
	 * 		其他情况都可以，但是以有Autowired的构造方法优先，然后才是默认构造方法
	 *
	 *
	 * @param beanClass
	 * @param beanName
	 * @return
	 * @throws BeanCreationException
	 */
	@Override
	@Nullable
	public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName)
			throws BeanCreationException {

			...

			}


13-4. AbstractAutowireCapableBeanFactory 类 （ return instantiateBean(beanName, mbd); 点进去 ）
		// No special handling: simply use no-arg constructor.
		// 使用默认无参构造函数创建对象，如果没有无参构造且存在多个有参构造且没有@AutoWired注解构造，会报错
		return instantiateBean(beanName, mbd);


13-4-1. AbstractAutowireCapableBeanFactory 类 （  .instantiate(mbd, beanName, this); 点进去 ）
        // 获取实例化策略并且进行实例化操作
        beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this);


13-4-2. InstantiationStrategy 类 （ Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) 点进去 ）
        Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner)
        		throws BeansException;


13-4-3-1. SimpleInstantiationStrategy 类 （ 查看bd对象里使用否含有构造方法 ）
        // 查看bd对象里使用否含有构造方法
        constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;


13-4-3-2. SimpleInstantiationStrategy 类 （ 获取默认的无参构造器 ）
        else {
            // 获取默认的无参构造器
            constructorToUse = clazz.getDeclaredConstructor();
        }


13-4-3-3. SimpleInstantiationStrategy 类 （ 获取到构造器之后将构造器赋值给bd中的属性 ）
        // 获取到构造器之后将构造器赋值给bd中的属性
        bd.resolvedConstructorOrFactoryMethod = constructorToUse;