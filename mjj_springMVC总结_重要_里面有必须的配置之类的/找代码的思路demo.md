Q: spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.java
    /**
         * Look up a handler method for the given request.
         */
        @Override
        protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
            // 获取访问的路径，一般类似于request.getServletPath()，返回不含contextPath的访问路径
            String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
            request.setAttribute(LOOKUP_PATH, lookupPath);
            // 获得读锁
            this.mappingRegistry.acquireReadLock();
            try {
                // 获取HandlerMethod作为handler对象，这里涉及到路径匹配的优先级
                // 优先级: 精确匹配>最长路径匹配>扩展名匹配
                HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
                // handlerMethod内部包含有bean对象，其实指的是对应的controller
                return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);
            }
            finally {
                //  释放读锁
                this.mappingRegistry.releaseReadLock();
            }
        }
    中： this.mappingRegistry.acquireReadLock(); 这段代码 mappingRegistry 中的 数据是什么时候完成赋值的？

A: 1. 先找到 private final MappingRegistry mappingRegistry = new MappingRegistry();

   2. new MappingRegistry(); 点进去

   3. 找到 private final Map<T, MappingRegistration<T>> registry = new HashMap<>();

   4. 找到 registry 中对应的 this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directUrls, name)); 中 put 代码的地方
       这段代码对应的方法是 public void register(T mapping, Object handler, Method method) {

   5. 这段代码引用的地方是 spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.java
        	/**
        	 * Register the given mapping.
        	 * <p>This method may be invoked at runtime after initialization has completed.
        	 * @param mapping the mapping for the handler method
        	 * @param handler the handler
        	 * @param method the method
        	 */
        	public void registerMapping(T mapping, Object handler, Method method) {
        		if (logger.isTraceEnabled()) {
        			logger.trace("Register \"" + mapping + "\" to " + method.toGenericString());
        		}
        		this.mappingRegistry.register(mapping, handler, method);
        	}
       中： this.mappingRegistry.register(mapping, handler, method); 这一段代码

   6. public void registerMapping(T mapping, Object handler, Method method) 这段代码再往上引用

   7. 找到 spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.java
        	@Override
        	public void registerMapping(RequestMappingInfo mapping, Object handler, Method method) {
        		super.registerMapping(mapping, handler, method);
        		updateConsumesCondition(mapping, method);
        	}

   8. public void registerMapping(RequestMappingInfo mapping, Object handler, Method method) 这段代码再往上引用

   9. 找到 spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.java
            @Override
        	public void registerMapping(RequestMappingInfo mapping, Object handler, Method method) {
        		super.registerMapping(mapping, handler, method);
        		updateConsumesCondition(mapping, method);
        	}

   10. 这时候 找这个方法对应的类的父类 ， 根据找到的 父类 WebApplicationObjectSupport 联想到需要找到 setApplicationContext()方法
            @Override
            public void registerMapping(RequestMappingInfo mapping, Object handler, Method method) {
                super.registerMapping(mapping, handler, method);
                updateConsumesCondition(mapping, method);
            }

   11. 继续翻找到 public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {
            找到 implements InitializingBean ， 他在哪进行调用的，invokeInitMethod 的时候会调用，调用这个接口之后会实现 一个方法 afterPropertiesSet

   12. 找到 spring-webmvc/src/main/java/org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.java
        	/**
        	 * Detects handler methods at initialization.
        	 * @see #initHandlerMethods
        	 */
        	@Override
        	public void afterPropertiesSet() {
        		// 初始化处理器的方法们
        		initHandlerMethods();
        	}

   13. initHandlerMethods(); 点进去
            /**
        	 * Scan beans in the ApplicationContext, detect and register handler methods.
        	 * @see #getCandidateBeanNames()
        	 * @see #processCandidateBean
        	 * @see #handlerMethodsInitialized
        	 */
        	protected void initHandlerMethods() {
        		// 遍历 Bean ，逐个处理
        		for (String beanName : getCandidateBeanNames()) {
        			// 排除目标代理类，AOP 相关，可查看注释
        			if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
        				// 处理 Bean
        				processCandidateBean(beanName);
        			}
        		}
        		// 初始化处理器的方法们，目前是空方法，暂无具体的实现
        		handlerMethodsInitialized(getHandlerMethods());
        	}

   14. processCandidateBean(beanName); 点进去
        	protected void processCandidateBean(String beanName) {
        		// 获得 Bean 对应的 Class 对象
        		Class<?> beanType = null;
        		try {
        			beanType = obtainApplicationContext().getType(beanName);
        		}
        		catch (Throwable ex) {
        			// An unresolvable bean type, probably from a lazy bean - let's ignore it.
        			if (logger.isTraceEnabled()) {
        				logger.trace("Could not resolve type for bean '" + beanName + "'", ex);
        			}
        		}
        		// 判断 Bean 是否为处理器（例如有 @Controller 或者 @RequestMapping 注解）
        		if (beanType != null && isHandler(beanType)) {
        			// 扫描处理器方法
        			detectHandlerMethods(beanName);
        		}
        	}

   15. detectHandlerMethods(beanName); 点进去
        			// 将符合要求的method注册起来，也就是保存到三个map中
        			methods.forEach((method, mapping) -> {
        				Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
        				registerHandlerMethod(handler, invocableMethod, mapping);
        			});

   16. registerHandlerMethod(handler, invocableMethod, mapping); 点进去 （ 342行 ）
        	/**
        	 * Register a handler method and its unique mapping. Invoked at startup for
        	 * each detected handler method.
        	 * @param handler the bean name of the handler or the handler instance
        	 * @param method the method to register
        	 * @param mapping the mapping conditions associated with the handler method
        	 * @throws IllegalStateException if another method was already registered
        	 * under the same mapping
        	 */
        	protected void registerHandlerMethod(Object handler, Method method, T mapping) {
        		this.mappingRegistry.register(mapping, handler, method);
        	}

   17. 这时候就找到了 this.mappingRegistry.register(mapping, handler, method);


最终，在这个地方打断点，然后debug ， 以上就是 找代码的思路









