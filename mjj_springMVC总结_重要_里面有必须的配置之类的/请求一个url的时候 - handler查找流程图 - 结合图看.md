# 流程

# 前端页面
    spring-mymvc/src/main/webapp/fileupload.jsp

# 调用的后端页面
    spring-mymvc/src/main/java/com/mashibing/controller/fileupload/FileUploadController.java

# 前置流程
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


之后进行以下流程


## spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java （这地方对应图中 - 是否是一个上床请求）
        // 检测请求是否为上传请求，如果是则通过multipartResolver将其封装成MultipartHttpServletRequest对象
        processedRequest = checkMultipart(request);

## spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java （这地方对应图中 - 查找对应的处理器handler(controller)）
        // Determine handler for the current request.
        // 获得请求对应的HandlerExecutionChain对象（HandlerMethod和HandlerInterceptor拦截器们）
        mappedHandler = getHandler(processedRequest);

    ### spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java
        HandlerExecutionChain handler = mapping.getHandler(request);

    ### spring-webmvc/src/main/java/org/springframework/web/servlet/HandlerMapping.java
        HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

    ### spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMapping.java
        // 获得处理器（HandlerMethod或者HandlerExecutionChain），该方法是抽象方法，由子类实现
        Object handler = getHandlerInternal(request);

    ### spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMapping.java
    	protected abstract Object getHandlerInternal(HttpServletRequest request) throws Exception;

    ### spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.java 和 spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractUrlHandlerMapping.java
