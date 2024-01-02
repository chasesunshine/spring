# 解析：
    spring-debug/src/main/java/com/mashibing/aop/xml/TestAop.java

    MyCalculator是这样的
        public class MyCalculator /*implements Calculator */{

    参考 aop动态代理和拦截器.jpg 、 Spring_AOP_expose-proxy理解.md

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


17. AbstractAutoProxyCreator 类 （ createProxy( 点进去 ）
        // 根据获取到的Advices和Advisors为当前bean生成代理对象
        Object proxy = createProxy(
        		bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));


18. AbstractAutoProxyCreator 类 （ proxyFactory.getProxy( 点进去 ）
		// 真正创建代理对象
		return proxyFactory.getProxy(getProxyClassLoader());


19. ProxyFactory 类 （ createAopProxy() 点进去 ）
        public Object getProxy(@Nullable ClassLoader classLoader) {
        	// createAopProxy() 用来创建我们的代理工厂
        	return createAopProxy().getProxy(classLoader);
        }


20. ProxyCreatorSupport 类 （ .createAopProxy(this); 点进去 ）
        // 通过AopProxyFactory获得AopProxy，这个AopProxyFactory是在初始化函数中定义的，使用的是DefaultAopProxyFactory
        return getAopProxyFactory().createAopProxy(this);


21. AopProxyFactory 类 （ 点进去 ）
	    AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;


22. DefaultAopProxyFactory 类 （ 配置了使用Cglib进行动态代理或者目标类没有接口,那么使用Cglib的方式创建代理对象 ）
        // 配置了使用Cglib进行动态代理或者目标类没有接口,那么使用Cglib的方式创建代理对象
        return new ObjenesisCglibAopProxy(config);


23. 回到 19 步 ProxyFactory 类 （ .getProxy(classLoader); 点进去 ）


24. AopProxy 类 （ 点进去 ）
	    Object getProxy(@Nullable ClassLoader classLoader);


25. CglibAopProxy 类 （ return createProxyClassAndInstance(enhancer, callbacks); 点进去 ）
        // Generate the proxy class and create a proxy instance.
        // 通过 Enhancer 生成代理对象，并设置回调
        return createProxyClassAndInstance(enhancer, callbacks);


26. CglibAopProxy 类 （ 点进去 ）
        protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {



27. ObjenesisCglibAopProxy 类 （ 这个地方创建 class对象，后面就是创建代理对象 ）
		Class<?> proxyClass = enhancer.createClass();


28. spring-debug/src/main/java/com/mashibing/aop/xml/TestAop.java 类 （ 创建完成 ， 然后从容器中获取 MyCalculator对象 ）
        MyCalculator bean = ac.getBean(MyCalculator.class);


总结：
    以上debug调试下来 ， 从 beanFactory 的 singletonObjects 中 可以找到 "myCalculator" ->{MyCalculator$$EnhancerBySpringCGLIB$Sb069e19d@2163} "super.toString0" ，生成了 CGLIB 代理对象


