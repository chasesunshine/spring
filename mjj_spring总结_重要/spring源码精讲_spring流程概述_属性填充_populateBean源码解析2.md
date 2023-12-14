# 目的：
    针对 java/com/mashibing/populateBean/Person.java 这个 Person 对象 填充 Address 属性的解析

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


18. AbstractBeanFactory 类 （ prototypeInstance = createBean(beanName, mbd, args); 点进去 ）
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


19. AbstractBeanFactory 类 （ protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) 点进去 ）
        protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        			throws BeanCreationException;


20. AbstractAutowireCapableBeanFactory 类 （ Object beanInstance = doCreateBean(beanName, mbdToUse, args); 点进去）
        try {
        	// 实际创建bean的调用
        	Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        	if (logger.isTraceEnabled()) {
        		logger.trace("Finished creating instance of bean '" + beanName + "'");
        	}
        	return beanInstance;
        }


21. AbstractAutowireCapableBeanFactory 类 （ populateBean(beanName, mbd, instanceWrapper); 点进去）
        // 对bean的属性进行填充，将各个属性值注入，其中，可能存在依赖于其他bean的属性，则会递归初始化依赖的bean
        populateBean(beanName, mbd, instanceWrapper);


* 21步和12步就形成了闭环 ， 就是为了 填充 person 的 address对象



* 以下是 address 对象 属性值处理 的 过程
15-1. BeanDefinitionValueResolver 类 （ Object valueObject = evaluate(typedStringValue); 点进去 ，这地方因为是 address 的属性 的值是 String 类型 ，所以 走的是 value instanceof TypedStringValue ）
        // 对TypedStringValue进行解析
        else if (value instanceof TypedStringValue) {
        	// Convert value to target type here.
        	// 在此处将value转换为目标类型，将value强转为TypedStringValue对象
        	TypedStringValue typedStringValue = (TypedStringValue) value;
        	//在typedStringValue封装的value可解析成表达式的情况下,将typedStringValue封装的value评估为表达式并解析出表达式的值
        	Object valueObject = evaluate(typedStringValue);
        	try {
        		//在typedStringValue中解析目标类型
        		Class<?> resolvedTargetType = resolveTargetType(typedStringValue);
        		//如果resolvedTargetType不为null
        		if (resolvedTargetType != null) {
        			//使用typeConverter将值转换为所需的类型
        			return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
        		}
        		else {
        			//返回并解析出来表达式的值
        			return valueObject;
        		}
        	}
        	//捕捉在解析目标类型或转换类型过程中抛出的异常
        	catch (Throwable ex) {
        		// Improve the message by showing the context.
        		throw new BeanCreationException(
        				this.beanDefinition.getResourceDescription(), this.beanName,
        				"Error converting typed String value for " + argName, ex);
        	}
        }


16-1. BeanDefinitionValueResolver 类 （ Object result = doEvaluate(value.getValue()); 点进去 ）
		//如有必要(value可解析成表达式的情况下)，将value封装的value评估为表达式并解析出表达式的值
		Object result = doEvaluate(value.getValue());


17-1. BeanDefinitionValueResolver 类 （ return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition); 点进去 ）
		//评估value,如果value是可解析表达式，会对其进行解析，否则直接返回value
		return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition);


18-1. AbstractBeanFactory 类 （ return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope)); 点进去 ）
		// 评估value作为表达式（如果适用）；否则按原样返回值
		return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));


19-1. BeanExpressionResolver 类 （ 点进去 ）
        @Nullable
        Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws BeansException;


20-1. StandardBeanExpressionResolver类 （ 这地方就是 获取 populateBean.xml 中 address 对象 处理的 province、city、town 属性 对应的 值 ）
        return expr.getValue(sec);



* 以下是 address 对象 属性填充 的 过程
14-2. AbstractAutowireCapableBeanFactory 类 （ bw.setPropertyValues(new MutablePropertyValues(deepCopy)); 点进去 ）
        //按原样使用deepCopy构造一个新的MutablePropertyValues对象然后设置到bw中以对bw的属性值更新
  		bw.setPropertyValues(new MutablePropertyValues(deepCopy));


15-2. AbstractNestablePropertyAccessor 类 （ nestedPa.setPropertyValue(tokens, pv); 点进去 ）
		nestedPa.setPropertyValue(tokens, pv);


16-2. AbstractNestablePropertyAccessor 类 （ processLocalProperty(tokens, pv); 点进去 ）
		processLocalProperty(tokens, pv);


17-2. AbstractNestablePropertyAccessor 类 （ ph.setValue(valueToApply); 点进去 ）
		ph.setValue(valueToApply);


18-2. AbstractNestablePropertyAccessor 类 （ public abstract void setValue(@Nullable Object value) throws Exception; 点进去 ）
		public abstract void setValue(@Nullable Object value) throws Exception;


19-2. BeanWrapperImpl 类 （ writeMethod.invoke(getWrappedInstance(), value); 点进去 ）
		writeMethod.invoke(getWrappedInstance(), value);


20-2. Method 类 （ return ma.invoke(obj, args); 点进去  这里是源码了）
		return ma.invoke(obj, args);


21-2. MethodAccessor 类 （ 点进去 ）
		public Object invoke(Object obj, Object[] args)
			throws IllegalArgumentException, InvocationTargetException;


22-2. NativeMethodAccessorImpl 类 （ 这地方就是对属性的填充 ）
		return invoke0(method, obj, args);