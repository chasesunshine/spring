# 解析：
    参考 mjj_spring总结_重要/aop创建代理对象前的准备工作.jpg

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


12. AbstractAutowireCapableBeanFactory 类 （ exposedObject = initializeBean(beanName, exposedObject, mbd); 点进去）
        // 执行初始化逻辑
        exposedObject = initializeBean(beanName, exposedObject, mbd);


13. AbstractAutowireCapableBeanFactory 类 （ wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName); 点进去）
        // 将BeanPostProcessors应用到给定的现有Bean实例，调用它们的postProcessAfterInitialization方法。
        // 返回的Bean实例可能是原始Bean包装器
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);


14. AbstractAutowireCapableBeanFactory 类 （ Object current = processor.postProcessAfterInitialization(result, beanName); 点进去）
        //回调BeanPostProcessor#postProcessAfterInitialization来对现有的bean实例进行包装
        Object current = processor.postProcessAfterInitialization(result, beanName);


15. BeanPostProcessor 类 （ 点进去 ）
        @Nullable
        default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        	return bean;
        }


16. AbstractAutoProxyCreator 类 （ return wrapIfNecessary(bean, beanName, cacheKey); 点进去 ）
        // 如果它需要被代理，则需要封装指定的bean
        return wrapIfNecessary(bean, beanName, cacheKey);


17. AbstractAutoProxyCreator 类 （ Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null); 点进去 ）
		// Create proxy if we have advice.
		// 获取当前bean的Advices和Advisors
		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);


18. AbstractAutoProxyCreator 类 （ 点进去 ）
        @Nullable
        protected abstract Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, S
        		@Nullable TargetSource customTargetSource) throws BeansException;


19. AbstractAdvisorAutoProxyCreator 类 （ List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName); 点进去 ）
		// 找合适的增强器对象
		List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);


20. AbstractAdvisorAutoProxyCreator 类 （ List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName); 点进去 ）
		// 对获取到的所有Advisor进行判断，看其切面定义是否可以应用到当前bean，从而得到最终需要应用的Advisor
		List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);


21. AbstractAdvisorAutoProxyCreator 类 （ return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass); 点进去 ）
        // 从候选的通知器中找到合适正在创建的实例对象的通知器
        return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);



22. AopUtils 类 （ canApply(candidate, clazz, hasIntroductions) 点进去 ）
        // 真正的判断增强器是否合适当前类型
        if (canApply(candidate, clazz, hasIntroductions)) {
        	eligibleAdvisors.add(candidate);
        }



23. AopUtils 类 （ canApply(pca.getPointcut(), targetClass, hasIntroductions); 点进去 这地方需要细看 ）
        //这里从Advisor中获取Pointcut的实现类 这里是AspectJExpressionPointcut
        return canApply(pca.getPointcut(), targetClass, hasIntroductions);



* 注意：目前还没有创建代理对象， createProxy 方法才是 创建代理对象






