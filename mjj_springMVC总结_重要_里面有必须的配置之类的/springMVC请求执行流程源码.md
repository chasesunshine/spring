SpringMVC 发送 get、post 请求，执行流程的源码


点开 spring-mymvc/src/main/webapp/WEB-INF/web.xml
<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
点进 DispatcherServlet 这个类 ， 往上找到他的父类 FrameworkServlet ， 找到以下方法 service(HttpServletRequest request, HttpServletResponse response)
以下是执行请求

1. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ 这地方开始 处理其他类型的请求 ，接下来会走到 doGet() 方法里面 ）
        // 处理其他类型的请求
        super.service(request, response);


2. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ processRequest(request, response); 点进去 ）
        @Override
        protected final void doGet(HttpServletRequest request, HttpServletResponse response)
        		throws ServletException, IOException {

        	processRequest(request, response);
        }


3. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ doService(request, response); 点进去 ）
        // 执行真正的逻辑
        doService(request, response);


4. spring-webmvc/src/main/java/org/springframework/web/servlet/FrameworkServlet.java （ 点进去 ）
	    protected abstract void doService(HttpServletRequest request, HttpServletResponse response)
			    throws Exception;


5. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java （ doDispatch(request, response); 点进去 ）
        // 执行请求的分发
        doDispatch(request, response);


6-1. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java （ 这地方自己看执行流程 ）
        // Determine handler for the current request.
        // 获得请求对应的HandlerExecutionChain对象（HandlerMethod和HandlerInterceptor拦截器们）
        mappedHandler = getHandler(processedRequest);


6-2. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java
        // Determine handler adapter for the current request.
        // 获得当前handler对应的HandlerAdapter对象
        HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());


6-3. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java
        // 执行响应的Interceptor的preHandler
        // 注意：该方法如果有一个拦截器的前置处理返回false，则开始倒序触发所有的拦截器的 已完成处理
        if (!mappedHandler.applyPreHandle(processedRequest, response)) {
        	return;
        }


6-4. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java
        // Actually invoke the handler.
        // 真正的调用handler方法，也就是执行对应的方法，并返回视图
        mv = ha.handle(processedRequest, response, mappedHandler.getHandler());


6-5. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java
        // 当view为空时，根据request设置默认的view
        applyDefaultViewName(processedRequest, mv);


6-6. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java
        // 执行响应的interceptor的postHandler方法
        mappedHandler.applyPostHandle(processedRequest, response, mv);


6-7. spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java
        // 处理返回结果，包括处理异常、渲染页面、触发Interceptor的afterCompletion
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);