# 解析：
    spring-debug/src/main/java/com/mashibing/proxy/cglib

# 源码解析流程：
1. spring-debug/src/main/java/com/mashibing/proxy/cglib/MyTest.java 类 （ Enhancer enhancer = new Enhancer(); 点进去 ）
        Enhancer enhancer = new Enhancer();


2. spring-core/src/main/java/org/springframework/cglib/proxy/Enhancer.java 类 （ (EnhancerKey) KeyFactory.create(EnhancerKey.class, KeyFactory.HASH_ASM_TYPE, null); 点进去 ）
        // 使用key工厂创建出对应class的代理类，后面的KeyFactory_HASH_ASM_TYPE即代理类中创建HashCode方法的策略
        private static final EnhancerKey KEY_FACTORY =
        		(EnhancerKey) KeyFactory.create(EnhancerKey.class, KeyFactory.HASH_ASM_TYPE, null);


3. spring-core/src/main/java/org/springframework/cglib/proxy/Enhancer.java 类 （ EnhancerKey 是一个内部接口，后续的时候需要用到 EnhancerKey 这个对象，但是 EnhancerKey 是一个 内部接口，无法直接new生成对象，此时需要一个子类实现，所以创建一个代理，这个代理过程就是为了创建 EnhancerKey 内部接口的子类实现 ）
        public interface EnhancerKey {

        	public Object newInstance(String type,
        			String[] interfaces,
        			WeakCacheKey<CallbackFilter> filter,
        			Type[] callbackTypes,
        			boolean useFactory,
        			boolean interceptDuringConstruction,
        			Long serialVersionUID);
        }


4. spring-core/src/main/java/org/springframework/cglib/core/KeyFactory.java 类 （ return create(keyInterface.getClassLoader(), keyInterface, first, next); 点进去 ）
        public static KeyFactory create(Class keyInterface, KeyFactoryCustomizer first, List<KeyFactoryCustomizer> next) {
        	return create(keyInterface.getClassLoader(), keyInterface, first, next);
        }


5. spring-core/src/main/java/org/springframework/cglib/core/KeyFactory.java 类 （ return gen.create(); 点进去 ）
        // 生成enhancerKey的代理类
        return gen.create();


6. spring-core/src/main/java/org/springframework/cglib/core/KeyFactory.java 类 （ return (KeyFactory) super.create(keyInterface.getName()); 点进去 ）
        public KeyFactory create() {
        	// 设置了该生成器生成代理类的名字前缀，即我们的接口名Enhancer.enhancerKey
        	setNamePrefix(keyInterface.getName());
        	return (KeyFactory) super.create(keyInterface.getName());
        }


7. spring-core/src/main/java/org/springframework/cglib/core/AbstractClassGenerator.java 类 （ Object obj = data.get( 点进去 ）
        // 在刚创建的data(ClassLoaderData)中调用get方法 并将当前生成器，
        // 以及是否使用缓存的标识穿进去 系统参数 System.getProperty("cglib.useCache", "true")
        // 返回的是生成好的代理类的class信息
        Object obj = data.get(this, getUseCache());


8. spring-core/src/main/java/org/springframework/cglib/core/AbstractClassGenerator.java 类 （ Object cachedValue = generatedClasses.get(gen); 点进去 ）
        // 传入代理类生成器 并根据代理类生成器获取值返回
        Object cachedValue = generatedClasses.get(gen);


9. LoadingCache.class 类 （ this.createEntry(key, cacheKey, v); 点进去 ）
        public V get(K key) {
            KK cacheKey = this.keyMapper.apply(key);
            Object v = this.map.get(cacheKey);
            return v != null && !(v instanceof FutureTask) ? v : this.createEntry(key, cacheKey, v);
        }


10. spring-core/src/main/java/org/springframework/cglib/core/AbstractClassGenerator.java 类 （ 理解 ClassLoaderData 的创建作用 ）
        // 当前类加载器对应的缓存  缓存key为类加载器，缓存的value为ClassLoaderData，可以理解为一个缓存对象，只不过此缓存对象中包含的是具体的业务逻辑处理过程，有两个function的函数式接口，一个是返回gen.key,对应的名称叫GET_KEY,还有一个是为了创建具体的class，名字叫做load
        Map<ClassLoader, ClassLoaderData> cache = CACHE;


11. LoadingCache.class 类 （ return LoadingCache.this.loader.apply(key); 点进去 , 这地方正好触发函数式接口 ）
        task = new FutureTask(new Callable<V>() {
            public V call() throws Exception {
                return LoadingCache.this.loader.apply(key);
            }
        });


12. spring-core/src/main/java/org/springframework/cglib/core/AbstractClassGenerator.java 类 （ Class klass = gen.generate(ClassLoaderData.this); 点进去 ）
        // 新建一个回调函数，这个回调函数的作用在于缓存中没获取到值时，调用传入的生成的生成代理类并返回
        Function<AbstractClassGenerator, Object> load =
        		new Function<AbstractClassGenerator, Object>() {
        			public Object apply(AbstractClassGenerator gen) {
        				Class klass = gen.generate(ClassLoaderData.this);
        				return gen.wrapCachedClass(klass);
        			}
        		};


13. spring-core/src/main/java/org/springframework/cglib/core/AbstractClassGenerator.java 类 （ byte[] b = strategy.generate(this); 点进去 ）
        // 生成字节码
        byte[] b = strategy.generate(this);


14. DefaultGeneratorStrategy.class 类 （ DebuggingClassWriter cw = this.getClassVisitor(); 点进去 ）
        DebuggingClassWriter cw = this.getClassVisitor();


15. DefaultGeneratorStrategy.class 类 （ 这地方自己debug看 ）
        return new DebuggingClassWriter(2);


16. DefaultGeneratorStrategy.class 类 （ this.transform(cg).generateClass(cw); 点进去 ）
        this.transform(cg).generateClass(cw);


17. spring-core/src/main/java/org/springframework/cglib/core/KeyFactory.java 类 （ 这地方自己debug看，就是创建class对象的过程 ）
        // 3.写入newInstance方法
        EmitUtils.factory_method(ce, ReflectUtils.getSignature(newInstance));


18. spring-debug/src/main/java/com/mashibing/proxy/cglib/MyTest.java 类 （ 回到test类 ， MyCalculator myCalculator = (MyCalculator) enhancer.create(); 点进去 ）
        //创建代理对象
        MyCalculator myCalculator = (MyCalculator) enhancer.create();


19. spring-core/src/main/java/org/springframework/cglib/proxy/Enhancer.java 类 （ return createHelper(); 点进去 ）
        return createHelper();


20. spring-core/src/main/java/org/springframework/cglib/proxy/Enhancer.java 类 （ Object result = super.create(key); 点进去 ）
        // 调用父类即AbstractClassGenerator的创建代理类
        Object result = super.create(key);


21. LoadingCache.class 类 （ task.run(); 点进去 ，这地方是 线程调用 ）
        if (result == null) {
            creator = true;
            task.run();


22. spring-core/src/main/java/org/springframework/cglib/core/AbstractClassGenerator.java 类 （ 回到 return nextInstance(obj); ）
        // 如果不是则说明是实体，则直接执行另一个方法返回实体
        return nextInstance(obj);


23. spring-core/src/main/java/org/springframework/cglib/proxy/Enhancer.java 类 （ 回到 return data.newInstance(argumentTypes, arguments, callbacks); ）
        return data.newInstance(argumentTypes, arguments, callbacks);


24. spring-debug/src/main/java/com/mashibing/proxy/cglib/MyTest.java 类 （ myCalculator.add(1,1); 点进去 ）
        //通过代理对象调用目标方法
        myCalculator.add(1,1);


25. spring-debug/src/main/java/com/mashibing/proxy/cglib/MyCglib.java 类 （ 执行 Object o1 = methodProxy.invokeSuper(o, objects); 方法 ）
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            Object o1 = methodProxy.invokeSuper(o, objects);
            return o1;
        }