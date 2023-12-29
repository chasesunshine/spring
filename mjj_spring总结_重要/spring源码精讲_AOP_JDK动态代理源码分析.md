# 解析：
    spring-debug/src/main/java/com/mashibing/proxy/jdk

# 源码解析流程：
1. spring-debug/src/main/java/com/mashibing/proxy/jdk/Test.java 类 （ Calculator proxy = CalculatorProxy.getProxy(new MyCalculator()); 点进去 ）
        Calculator proxy = CalculatorProxy.getProxy(new MyCalculator());


2. spring-debug/src/main/java/com/mashibing/proxy/jdk/CalculatorProxy.java 类 （ Object proxy = Proxy.newProxyInstance(loader, interfaces, h); 点进去 ）
        Object proxy = Proxy.newProxyInstance(loader, interfaces, h);


3. Proxy 类 （ Class<?> cl = getProxyClass0(loader, intfs); 点进去 ）
        Class<?> cl = getProxyClass0(loader, intfs);


4. Proxy 类 （ return proxyClassCache.get(loader, interfaces); 点进去 ）
        private static Class<?> getProxyClass0(ClassLoader loader,
                                               Class<?>... interfaces) {
            if (interfaces.length > 65535) {
                throw new IllegalArgumentException("interface limit exceeded");
            }

            // If the proxy class defined by the given loader implementing
            // the given interfaces exists, this will simply return the cached copy;
            // otherwise, it will create the proxy class via the ProxyClassFactory
            return proxyClassCache.get(loader, interfaces);
        }


5. WeakCache 类 （ V value = supplier.get(); 点进去 ）
        if (supplier != null) {
            // supplier might be a Factory or a CacheValue<V> instance
            V value = supplier.get();
            if (value != null) {
                return value;
            }
        }


6. WeakCache 类 （ valueFactory.apply(key, parameter) 点进去 ）
        try {
            value = Objects.requireNonNull(valueFactory.apply(key, parameter));
        } finally {
            if (value == null) { // remove us on failure
                valuesMap.remove(subKey, this);
            }
        }


7. Proxy 类 （ 产生代理类 ）
        byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
            proxyName, interfaces, accessFlags);


8. 执行完回到第三步


9. Proxy 类 （ return cons.newInstance(new Object[]{h}); 点进去 ）
        return cons.newInstance(new Object[]{h});


10. Constructor 类 （ 这地方就是生成代理对象 ）
        T inst = (T) ca.newInstance(initargs);


11. 执行完整体流程，回到 spring-debug/src/main/java/com/mashibing/proxy/jdk/Test.java 类


12. spring-debug/src/main/java/com/mashibing/proxy/jdk/Test.java 类 （ proxy.add(1,1); 点进去 ）
        proxy.add(1,1);


13. 回调 spring-debug/src/main/java/com/mashibing/proxy/jdk/CalculatorProxy.java 类 （ 执行 result = method.invoke(calculator, args); ）
        InvocationHandler h = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                try {
                    result = method.invoke(calculator, args);
                } catch (Exception e) {
                } finally {
                }
                return result;
            }
        };


14. spring-debug/src/main/java/com/mashibing/proxy/jdk/MyCalculator.java 类 （ 执行 add 方法 ， 并返回结果 ）
        public int add(int i, int j) {
            int result = i + j;
            return result;
        }


















