SpringMVC想要正常运行的话，必须是需要初始化这九大组件的


开始加载 springMVC
点开 spring-mymvc/src/main/webapp/WEB-INF/web.xml
<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
点进 DispatcherServlet 这个类 ， 往上找到他的父类 HttpServletBean ， 找到以下方法 init()

1. spring-webmvc/src/main/java/org/springframework/web/servlet/HttpServletBean.java （ initServletBean(); 点进去 ）
        // Let subclasses do whatever initialization they like.
        // 模板方法，子类初始化的入口方法，查看FrameworkServlet#initServletBean方法
        initServletBean();


2. spring-webmvc/src/main/java/org/springframework/web/servlet/HttpServletBean.java （ 直接点进去 ）
        protected void initServletBean() throws ServletException {
        }


3. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ this.webApplicationContext = initWebApplicationContext(); 点进去 ）
        // 创建或刷新WebApplicationContext实例并对servlet功能所使用的变量进行初始化
        this.webApplicationContext = initWebApplicationContext();


4. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ wac = createWebApplicationContext(rootContext); 点进去 ）
        // 当前面两种方式都无效的情况下会创建一个webApplicationContext对象，一般情况下都是使用这样的方式
        if (wac == null) {
        	// No context instance is defined for this servlet -> create a local one
        	wac = createWebApplicationContext(rootContext);
        }


5. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ return createWebApplicationContext((ApplicationContext) parent); 点进去 ）
        protected WebApplicationContext createWebApplicationContext(@Nullable WebApplicationContext parent) {
        	return createWebApplicationContext((ApplicationContext) parent);
        }


6. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ 配置和初始化wac ）
		// 配置和初始化wac
		configureAndRefreshWebApplicationContext(wac);


7. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ 这地方是 添加监听器sourceFilteringListener到wac中 ）
		// 添加监听器sourceFilteringListener到wac中,实际监听的是ContextRefreshListener所监听的事件，监听ContextRefreshedEvent事件，
		// 当接收到消息之后会调用onApplicationEvent方法，调用onRefresh方法，并将refreshEventReceived标志设置为true，表示已经refresh过
		wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));


8. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ wac.refresh(); 点进去 ）
		// 刷新wac,从而初始化wac
		wac.refresh();


9. spring-context/src/main/java/org/springframework/context/ConfigurableApplicationContext.java （ 点进去 ）
        void refresh() throws BeansException, IllegalStateException;


10. spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java （ finishRefresh(); 点进去 ）
        // Last step: publish corresponding event.
        // 完成刷新过程，通知生命周期处理器lifecycleProcessor刷新过程，同时发出ContextRefreshEvent通知别人
        finishRefresh();


11. spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java （ publishEvent(new ContextRefreshedEvent(this)); 点进去 ）
		// Publish the final event.
		// 发布最终事件
		// 新建ContextRefreshedEvent事件对象，将其发布到所有监听器。
		publishEvent(new ContextRefreshedEvent(this));


12. spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java （ publishEvent(event, null); 点进去 ）
        /**
         * 将给定事件发布到所有监听器
         *
         * Publish the given event to all listeners.
         * <p>Note: Listeners get initialized after the MessageSource, to be able
         * to access it within listener implementations. Thus, MessageSource
         * implementations cannot publish events.
         * @param event the event to publish (may be application-specific or a
         * standard framework event)
         */
        @Override
        public void publishEvent(ApplicationEvent event) {
        	publishEvent(event, null);
        }


13. spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java （ getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType); 点进去 ）
		// Multicast right now if possible - or lazily once the multicaster is initialized
		// 如果可能的话，现在就进行组播——或者在组播初始化后延迟
		// earlyApplicationEvents：在多播程序设置之前发布的ApplicationEvent
		// 如果earlyApplicationEvents不为 null，这种情况只在上下文的多播器还没有初始化的情况下才会成立，会将applicationEvent
		// 添加到earlyApplicationEvents保存起来，待多博器初始化后才继续进行多播到适当的监听器
		if (this.earlyApplicationEvents != null) {
			//将applicationEvent添加到 earlyApplicationEvents
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
			// 多播applicationEvent到适当的监听器
			getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
		}


14. spring-context/src/main/java/org/springframework/context/event/ApplicationEventMulticaster.java （ 点进去 ）
        /**
         * 将应用程序事件广播到对应的监听器上
         *
         * Multicast the given application event to appropriate listeners.
         * <p>If the {@code eventType} is {@code null}, a default type is built
         * based on the {@code event} instance.
         * @param event the event to multicast
         * @param eventType the type of event (can be {@code null})
         * @since 4.2
         */
        void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType);


15. spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java （ invokeListener(listener, event); 点进去 ）
        //回调listener的onApplicationEvent方法，传入event
        invokeListener(listener, event);


16. spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java （ doInvokeListener(listener, event); 点进去 ）
        // 回调listener的onApplicationEvent方法，传入event
        doInvokeListener(listener, event);


17. spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java （ listener.onApplicationEvent(event); 点进去 ）
        //回调listener的onApplicationEvent方法，传入event:contextrefreshListener:onapplicaitonEvent:FrameworkServlet.this.onApplicationEvent()
        listener.onApplicationEvent(event);


18. spring-context/src/main/java/org/springframework/context/ApplicationListener.java （ 点进去 ）
	    void onApplicationEvent(E event);


19. spring-context/src/main/java/org/springframework/context/event/SourceFilteringListener.java （ onApplicationEventInternal(event); 点进去 和第七步作关联 ）
        @Override
        public void onApplicationEvent(ApplicationEvent event) {
        	if (event.getSource() == this.source) {
        		onApplicationEventInternal(event);
        	}
        }


20. spring-context/src/main/java/org/springframework/context/event/SourceFilteringListener.java （ this.delegate.onApplicationEvent(event); 点进去 ）
        protected void onApplicationEventInternal(ApplicationEvent event) {
        	if (this.delegate == null) {
        		throw new IllegalStateException(
        				"Must specify a delegate object or override the onApplicationEventInternal method");
        	}
        	this.delegate.onApplicationEvent(event);
        }


21. spring-context/src/main/java/org/springframework/context/ApplicationListener.java （ 点进去 ）
	    void onApplicationEvent(E event);


22. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ FrameworkServlet.this.onApplicationEvent(event); 点进去 ）
		@Override
		public void onApplicationEvent(ContextRefreshedEvent event) {
			FrameworkServlet.this.onApplicationEvent(event);
		}


23. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ onRefresh(event.getApplicationContext()); 点进去 ）
		synchronized (this.onRefreshMonitor) {
			// 处理事件中的 ApplicationContext 对象，空实现，子类DispatcherServlet会实现
			onRefresh(event.getApplicationContext());
		}


24. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ 点进去 ）
        protected void onRefresh(ApplicationContext context) {
        	// For subclasses: do nothing by default.
        }


25. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java （ initStrategies(context); 点进去 ）
        @Override
        protected void onRefresh(ApplicationContext context) {
        	initStrategies(context);
        }


26. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java （ SpringMVC九大组件 初始化开始 ）
        /**
         * Initialize the strategy objects that this servlet uses.
         * <p>May be overridden in subclasses in order to initialize further strategy objects.
         */
        protected void initStrategies(ApplicationContext context) {
        	// 初始化 MultipartResolver:主要用来处理文件上传.如果定义过当前类型的bean对象，那么直接获取，如果没有的话，可以为null
        	initMultipartResolver(context);
        	// 初始化 LocaleResolver:主要用来处理国际化配置,基于URL参数的配置(AcceptHeaderLocaleResolver)，基于session的配置(SessionLocaleResolver)，基于cookie的配置(CookieLocaleResolver)
        	initLocaleResolver(context);
        	// 初始化 ThemeResolver:主要用来设置主题Theme
        	initThemeResolver(context);
        	// 初始化 HandlerMapping:映射器，用来将对应的request跟controller进行对应
        	initHandlerMappings(context);
        	// 初始化 HandlerAdapter:处理适配器，主要包含Http请求处理器适配器，简单控制器处理器适配器，注解方法处理器适配器
        	initHandlerAdapters(context);
        	// 初始化 HandlerExceptionResolver:基于HandlerExceptionResolver接口的异常处理
        	initHandlerExceptionResolvers(context);
        	// 初始化 RequestToViewNameTranslator:当controller处理器方法没有返回一个View对象或逻辑视图名称，并且在该方法中没有直接往response的输出流里面写数据的时候，spring将会采用约定好的方式提供一个逻辑视图名称
        	initRequestToViewNameTranslator(context);
        	// 初始化 ViewResolver: 将ModelAndView选择合适的视图进行渲染的处理器
        	initViewResolvers(context);
        	// 初始化 FlashMapManager: 提供请求存储属性，可供其他请求使用
        	initFlashMapManager(context);
        }







