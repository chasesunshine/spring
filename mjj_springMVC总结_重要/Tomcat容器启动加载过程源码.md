先启动 Tomcat容器 ，再启动 spring 容器 ， 再启动 springMVC容器
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
