1 到 21-1 ： 目的： 此处获取xml文件的document对象，这个解析过程是由documentLoader完成的,从String[] -string-Resource[]- resource,最终开始将resource读取成一个document文档，根据文档的节点信息封装成一个个的BeanDefinition对象
1 到      ： 完成具体的解析过程

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


4. AbstractApplicationContext 类 （ obtainFreshBeanFactory(); 点进去 ）
        // Tell the subclass to refresh the internal bean factory.
        // 创建容器对象：DefaultListableBeanFactory
        // 加载xml配置文件的属性值到当前工厂中，最重要的就是BeanDefinition
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();


5. AbstractApplicationContext 类 （ refreshBeanFactory(); 点进去 ）
        // 初始化BeanFactory,并进行XML文件读取，并将得到的BeanFactory记录在当前实体的属性中
		refreshBeanFactory();


6. AbstractApplicationContext 类 （ protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException; 点进去 ）
	    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;


7. AbstractRefreshableApplicationContext 类 （ loadBeanDefinitions(beanFactory); 点进去）
        // 创建DefaultListableBeanFactory对象
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        // 为了序列化指定id，可以从id反序列化到beanFactory对象
        beanFactory.setSerializationId(getId());
        // 定制beanFactory，设置相关属性，包括是否允许覆盖同名称的不同定义的对象以及循环依赖
        customizeBeanFactory(beanFactory);
        // 初始化documentReader,并进行XML文件读取及解析,默认命名空间的解析，自定义标签的解析
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;


8. AbstractRefreshableApplicationContext 类 （ protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) 点进去）
	    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
			throws BeansException, IOException;


9. AbstractXmlApplicationContext 类 （ loadBeanDefinitions(beanDefinitionReader); 点进去）
        // Allow a subclass to provide custom initialization of the reader,
		// then proceed with actually loading the bean definitions.
		//  初始化beanDefinitionReader对象，此处设置配置文件是否要进行验证
		initBeanDefinitionReader(beanDefinitionReader);
		// 开始完成beanDefinition的加载
		loadBeanDefinitions(beanDefinitionReader);


10. AbstractXmlApplicationContext 类 （ reader.loadBeanDefinitions(configLocations); 点进去 ）
		if (configLocations != null) {
			reader.loadBeanDefinitions(configLocations);
		}


11. AbstractBeanDefinitionReader 类 （ count += loadBeanDefinitions(location); 点进去 ）
        for (String location : locations) {
			count += loadBeanDefinitions(location);
		}


12. AbstractBeanDefinitionReader 类 （ return loadBeanDefinitions(location, null); 点进去 ）
	    public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
	    	return loadBeanDefinitions(location, null);
	    }


13. AbstractBeanDefinitionReader 类 （ int count = loadBeanDefinitions(resources); 点进去 ）
		// 调用DefaultResourceLoader的getResource完成具体的Resource定位
		Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
		int count = loadBeanDefinitions(resources);


14. AbstractBeanDefinitionReader 类 （ count += loadBeanDefinitions(resource); 点进去 ）
	    public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
	    	Assert.notNull(resources, "Resource array must not be null");
	    	int count = 0;
	    	for (Resource resource : resources) {
	    		count += loadBeanDefinitions(resource);
	    	}
	    	return count;
	    }


15. BeanDefinitionReader 类 （ int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException; 点进去 ）
	    int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;


16. XmlBeanDefinitionReader 类 （ return loadBeanDefinitions(new EncodedResource(resource)); 点进去）
	    @Override
	    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
	    	return loadBeanDefinitions(new EncodedResource(resource));
	    }


17. XmlBeanDefinitionReader 类 （ return doLoadBeanDefinitions(inputSource, encodedResource.getResource()); 点进去 ）
		// 从encodedResource中获取已经封装的Resource对象并再次从Resource中获取其中的inputStream
		try (InputStream inputStream = encodedResource.getResource().getInputStream()) {
			InputSource inputSource = new InputSource(inputStream);
			if (encodedResource.getEncoding() != null) {
				inputSource.setEncoding(encodedResource.getEncoding());
			}
			// 逻辑处理的核心步骤
			return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
		}


18-1. XmlBeanDefinitionReader 类 （ Document doc = doLoadDocument(inputSource, resource); 点进去 ）
		// 此处获取xml文件的document对象，这个解析过程是由documentLoader完成的,从String[] -string-Resource[]- resource,最终开始将resource读取成一个document文档，根据文档的节点信息封装成一个个的BeanDefinition对象
		Document doc = doLoadDocument(inputSource, resource);
		int count = registerBeanDefinitions(doc, resource);


19-1. XmlBeanDefinitionReader 类 （ return this.documentLoader.loadDocument(inputSource, getEntityResolver(), this.errorHandler, 点进去 ）
	    protected Document doLoadDocument(InputSource inputSource, Resource resource) throws Exception {
	    	return this.documentLoader.loadDocument(inputSource, getEntityResolver(), this.errorHandler,
	    			getValidationModeForResource(resource), isNamespaceAware());
	    }


20-1. DocumentLoader 类 （ Document loadDocument( 点进去 ）
	    Document loadDocument(
			InputSource inputSource, EntityResolver entityResolver,
			ErrorHandler errorHandler, int validationMode, boolean namespaceAware)
			throws Exception;


21-1. DefaultDocumentLoader 类 （ return builder.parse(inputSource); 这一步就是 xml文件的最后解析 成果 ）
	    public Document loadDocument(InputSource inputSource, EntityResolver entityResolver,
	    		ErrorHandler errorHandler, int validationMode, boolean namespaceAware) throws Exception {

	    	DocumentBuilderFactory factory = createDocumentBuilderFactory(validationMode, namespaceAware);
	    	if (logger.isTraceEnabled()) {
	    		logger.trace("Using JAXP provider [" + factory.getClass().getName() + "]");
	    	}
	    	DocumentBuilder builder = createDocumentBuilder(factory, entityResolver, errorHandler);
	    	return builder.parse(inputSource);
	    }


18-2. XmlBeanDefinitionReader 类 （ int count = registerBeanDefinitions(doc, resource); 点进去 ）
		// 此处获取xml文件的document对象，这个解析过程是由documentLoader完成的,从String[] -string-Resource[]- resource,最终开始将resource读取成一个document文档，根据文档的节点信息封装成一个个的BeanDefinition对象
		Document doc = doLoadDocument(inputSource, resource);
		int count = registerBeanDefinitions(doc, resource);



19-2. XmlBeanDefinitionReader 类 （ documentReader.registerBeanDefinitions(doc, createReaderContext(resource)); 点进去 ）
		// 完成具体的解析过程
		documentReader.registerBeanDefinitions(doc, createReaderContext(resource));


20-2. BeanDefinitionDocumentReader 类 （ void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) 点进去 ）
		void registerBeanDefinitions(Document doc, XmlReaderContext readerContext)
			throws BeanDefinitionStoreException;


21-2. DefaultBeanDefinitionDocumentReader 类 （ doRegisterBeanDefinitions(doc.getDocumentElement()); 点进去 ）
		public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
			this.readerContext = readerContext;
			doRegisterBeanDefinitions(doc.getDocumentElement());
		}


22-2. DefaultBeanDefinitionDocumentReader 类 （ parseBeanDefinitions(root, this.delegate); 点进去 ）
		preProcessXml(root);
		parseBeanDefinitions(root, this.delegate);
		postProcessXml(root);

23-2. DefaultBeanDefinitionDocumentReader 类 （ parseDefaultElement(ele, delegate); 点进去 ）
		if (delegate.isDefaultNamespace(ele)) {
			parseDefaultElement(ele, delegate);
		}


24-2. DefaultBeanDefinitionDocumentReader 类 （ processBeanDefinition(ele, delegate); 点进去 ）
		else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
			processBeanDefinition(ele, delegate);
		}


25-2. DefaultBeanDefinitionDocumentReader 类 （ BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele); 点进去 ）
		// beanDefinitionHolder是beanDefinition对象的封装类，封装了BeanDefinition，bean的名字和别名，用它来完成向IOC容器的注册
		// 得到这个beanDefinitionHolder就意味着beandefinition是通过BeanDefinitionParserDelegate对xml元素的信息按照spring的bean规则进行
		// 解析得到的
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);


26-2. BeanDefinitionParserDelegate 类 （ return parseBeanDefinitionElement(ele, null); 点进去 ）
		public BeanDefinitionHolder parseBeanDefinitionElement(Element ele) {
			return parseBeanDefinitionElement(ele, null);
		}


27-2. BeanDefinitionParserDelegate 类 （ AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(ele, beanName, containingBean); 点进去 ）
		// 对bean元素的详细解析
		AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(ele, beanName, containingBean);


28-2. BeanDefinitionParserDelegate 类 （ 这地方是 解析 xml 文件中的 各个 node节点 也就是 bean标签的解析 ， parseConstructorArgElements(ele, bd); 这个需要点进去仔细看 ）
		try {
			// 创建装在bean信息的AbstractBeanDefinition对象，实际的实现是GenericBeanDefinition
			AbstractBeanDefinition bd = createBeanDefinition(className, parent);

			// 解析bean标签的各种其他属性
			parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
			// 设置description信息
			bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));

			// 解析元数据
			parseMetaElements(ele, bd);
			// 解析lookup-method属性
			parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
			// 解析replaced-method属性
			parseReplacedMethodSubElements(ele, bd.getMethodOverrides());

			// 解析构造函数参数
			parseConstructorArgElements(ele, bd);
			// 解析property子元素
			parsePropertyElements(ele, bd);
			// 解析qualifier子元素
			parseQualifierElements(ele, bd);

			bd.setResource(this.readerContext.getResource());
			bd.setSource(extractSource(ele));

			return bd;
		}


25-3. DefaultBeanDefinitionDocumentReader 类 （ BeanDefinitionReaderUtils.registerBeanDefinition( 点进去 ）
		// Register the final decorated instance.
		// 向ioc容器注册解析得到的beandefinition的地方
		BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());


26-3. BeanDefinitionReaderUtils 类 （ registry.registerBeanDefinition( 点进去 ）
		// Register bean definition under primary name.
		// 使用beanName做唯一标识注册
		String beanName = definitionHolder.getBeanName();
		registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());


27-3. BeanDefinitionRegistry 类 （ void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) 点进去 ）
		void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException;


28-3. DefaultListableBeanFactory 类 （ 这里就是 beanDefinition 的处理完成结果了 ）
		this.beanDefinitionMap.put(beanName, beanDefinition);


// xml文件自定义标签完成解析工作
23-4. DefaultBeanDefinitionDocumentReader 类 （ delegate.parseCustomElement(ele); 点进去 ）
		else {
			delegate.parseCustomElement(ele);
		}


24-4. BeanDefinitionParserDelegate 类 （ return parseCustomElement(ele, null); 点进去 ）
		public BeanDefinition parseCustomElement(Element ele) {
			return parseCustomElement(ele, null);
		}


25-4. BeanDefinitionParserDelegate 类 （ return handler.parse(ele,  点进去 ）
		public BeanDefinition parseCustomElement(Element ele, @Nullable BeanDefinition containingBd) {
				// 获取对应的命名空间
				String namespaceUri = getNamespaceURI(ele);
				if (namespaceUri == null) {
				return null;
			}
			// 根据命名空间找到对应的NamespaceHandlerspring
			NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
			if (handler == null) {
				error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
				return null;
			}
			// 调用自定义的NamespaceHandler进行解析
			return handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
		}


26-4. NamespaceHandler 类 （ BeanDefinition parse(Element element, ParserContext parserContext); 点进去 ）
		BeanDefinition parse(Element element, ParserContext parserContext);


27-4. NamespaceHandlerSupport 类 （ return (parser != null ? parser.parse(element, parserContext) : null); 点进去 ）
		public BeanDefinition parse(Element element, ParserContext parserContext) {
			// 获取元素的解析器
			BeanDefinitionParser parser = findParserForElement(element, parserContext);
			return (parser != null ? parser.parse(element, parserContext) : null);
		}


28-4. BeanDefinitionParser 类 （ BeanDefinition parse(Element element, ParserContext parserContext); 点进去 ）
		BeanDefinition parse(Element element, ParserContext parserContext);


29-4. AbstractBeanDefinitionParser类 （ registerBeanDefinition(holder, parserContext.getRegistry()); 是 BeanDefinition 注册的最终结果 ）
		// 将AbstractBeanDefinition转换为BeanDefinitionHolder并注册
		BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, aliases);
		registerBeanDefinition(holder, parserContext.getRegistry());
