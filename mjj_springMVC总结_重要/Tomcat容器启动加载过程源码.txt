先启动 Tomcat容器 ，再启动 spring 容器 ， 再启动 springMVC容器


以下先是加载 spring 容器
点开 spring-mymvc/src/main/webapp/WEB-INF/web.xml
<listener>
	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
这一行 ContextLoaderListener 类点进去 找到以下方法 

1. spring-web/src/main/java/org/springframework/web/context/ContextLoaderListener.java （ initWebApplicationContext(event.getServletContext()); 点进去 ）
    	/**
    	 * Initialize the root web application context.
    	 */
    	@Override
    	public void contextInitialized(ServletContextEvent event) {
    		initWebApplicationContext(event.getServletContext());
    	}


2. spring-web/src/main/java/org/springframework/web/context/ContextLoader.java （ this.context = createWebApplicationContext(servletContext); 点进去 ）
        // Store context in local instance variable, to guarantee that
        // it is available on ServletContext shutdown.
        if (this.context == null) {
        	// 初始化context，第一次执行的时候获取到一个root webApplicationcontext
        	this.context = createWebApplicationContext(servletContext);
        }


3. spring-web/src/main/java/org/springframework/web/context/ContextLoader.java （ Class<?> contextClass = determineContextClass(sc); 点进去 ）
		// 获取contextClass的Class对象
		Class<?> contextClass = determineContextClass(sc);


4. spring-web/src/main/java/org/springframework/web/context/ContextLoader.java （ 反射获取类信息  ）
        return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());


5. 回到 spring-web/src/main/java/org/springframework/web/context/ContextLoader.java 执行下去 （ this.context = createWebApplicationContext(servletContext); ）


6. spring-web/src/main/java/org/springframework/web/context/ContextLoader.java （ configureAndRefreshWebApplicationContext(cwac, servletContext); 点进去 ）
        configureAndRefreshWebApplicationContext(cwac, servletContext);


7. spring-web/src/main/java/org/springframework/web/context/ContextLoader.java （ wac.refresh(); 点进去 ）
        wac.refresh();


8. spring-context/src/main/java/org/springframework/context/ConfigurableApplicationContext.java （ 	void refresh() throws BeansException, IllegalStateException; 点进去 ）
        void refresh() throws BeansException, IllegalStateException;


9. spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java （ 这地方开始 spring 容器的启动 ）



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



以上 Tomcat容器 ，spring 容器 ， springMVC容器 都准备好了
接下来就是开始 url 的访问了



