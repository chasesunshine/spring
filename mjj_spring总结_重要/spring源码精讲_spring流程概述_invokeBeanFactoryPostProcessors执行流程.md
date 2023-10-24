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


4. AbstractApplicationContext 类 （ invokeBeanFactoryPostProcessors(beanFactory); 点进去 ）
        // Invoke factory processors registered as beans in the context.
		// 调用各种beanFactory处理器
		invokeBeanFactoryPostProcessors(beanFactory);


5. AbstractApplicationContext 类 （ PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors( 点进去 ）
       // 获取到当前应用程序上下文的beanFactoryPostProcessors变量的值，并且实例化调用执行所有已经注册的beanFactoryPostProcessor
       // 默认情况下，通过getBeanFactoryPostProcessors()来获取已经注册的BFPP，但是默认是空的，那么问题来了，如果你想扩展，怎么进行扩展工作？
       PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());


6. 自己看 public static void invokeBeanFactoryPostProcessors( ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) 里面的代码