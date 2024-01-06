# 解析：
    spring-debug/src/main/java/com/mashibing/aop/xml
    spring-debug/src/main/java/com/mashibing/aop/xml/TestAop.java

    参考：mjj_spring总结_重要/aop动态代理和拦截器.jpg


LogUtil 代码：
        package com.mashibing.aop.xml.util;

        import org.aspectj.lang.JoinPoint;
        import org.aspectj.lang.ProceedingJoinPoint;
        import org.aspectj.lang.Signature;

        import java.util.Arrays;

        //@Aspect
        //@Component
        //@Order(200)
        public class LogUtil {

        //    @Pointcut("execution(public Integer com.mashibing.service.MyCalculator.*(Integer,Integer))")
            public void myPointCut(){}

        //    @Pointcut("execution(* *(..))")
            public void myPointCut1(){}

        //    @Before(value = "myPointCut()")
            private int start(JoinPoint joinPoint){
                //获取方法签名
                Signature signature = joinPoint.getSignature();
                //获取参数信息
                Object[] args = joinPoint.getArgs();
                System.out.println("log Before ---"+signature.getName()+"方法开始执行：参数是"+Arrays.asList(args));
                return 100;
            }

        //    @AfterReturning(value = "myPointCut()",returning = "result")
            public static void stop(JoinPoint joinPoint,Object result){
                Signature signature = joinPoint.getSignature();
                System.out.println("log AfterReturning ---"+signature.getName()+"方法执行结束，结果是："+result);
            }

        //    @AfterThrowing(value = "myPointCut()",throwing = "e")
            public static void logException(JoinPoint joinPoint,Exception e){
                Signature signature = joinPoint.getSignature();
                System.out.println("log AfterThrowing ---"+signature.getName()+"方法抛出异常："+e.getMessage());
            }

        //    @After("myPointCut()")
            public static void logFinally(JoinPoint joinPoint){
                Signature signature = joinPoint.getSignature();
                System.out.println("log After ---"+signature.getName()+"方法执行结束。。。。。over");

            }

        //     @Around("myPointCut()")
            public Object around(ProceedingJoinPoint pjp) throws Throwable {
                Signature signature = pjp.getSignature();
                Object[] args = pjp.getArgs();
                Object result = null;
                try {
                    System.out.println("log Around ---环绕通知start："+signature.getName()+"方法开始执行，参数为："+Arrays.asList(args));
                    //通过反射的方式调用目标的方法，相当于执行method.invoke(),可以自己修改结果值
                    result = pjp.proceed(args);
        //            result=100;
                    System.out.println("log Around ---环绕通知stop"+signature.getName()+"方法执行结束");
                } catch (Throwable throwable) {
                    System.out.println("log Around ---环绕异常通知："+signature.getName()+"出现异常");
                    throw throwable;
                }finally {
                    System.out.println("log Around ---环绕返回通知："+signature.getName()+"方法返回结果是："+result);
                }
                return result;
            }
        }


TestAop 代码：
            public static void main(String[] args) throws Exception {
        //        saveGeneratedCGlibProxyFiles(System.getProperty("user.dir")+"/proxy");
                ApplicationContext ac = new ClassPathXmlApplicationContext("aop.xml");
                MyCalculator bean = ac.getBean(MyCalculator.class);
                System.out.println(bean.toString());
                bean.add(1,1);
                bean.sub(1,1);

            }


aop.xml代码：
        <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:aop="http://www.springframework.org/schema/aop"
               xsi:schemaLocation="http://www.springframework.org/schema/beans
               http://www.springframework.org/schema/beans/spring-beans.xsd
               http://www.springframework.org/schema/aop
               http://www.springframework.org/schema/aop/spring-aop.xsd
        ">
            <bean class="com.mashibing.MyBeanFactoryPostProcessorBySelf" ></bean>
            <bean id="logUtil" class="com.mashibing.aop.xml.util.LogUtil" ></bean>
            <bean id="myCalculator" class="com.mashibing.aop.xml.service.MyCalculator" ></bean>
            <aop:config>
                <aop:aspect ref="logUtil">
                    <aop:pointcut id="myPoint" expression="execution( Integer com.mashibing.aop.xml.service.MyCalculator.*  (..))"/>
                    <aop:around method="around" pointcut-ref="myPoint"></aop:around>
                    <aop:before method="start" pointcut-ref="myPoint"></aop:before>
                    <aop:after method="logFinally" pointcut-ref="myPoint"></aop:after>
                    <aop:after-returning method="stop" pointcut-ref="myPoint" returning="result"></aop:after-returning>
                    <aop:after-throwing method="logException" pointcut-ref="myPoint" throwing="e"></aop:after-throwing>
                </aop:aspect>
            </aop:config>
            <aop:aspectj-autoproxy></aop:aspectj-autoproxy>
        </beans>




# 源码解析流程：（需要自己 一步步 debug ）
1. spring-debug/src/main/java/com/mashibing/aop/xml/TestAop.java 类 （ bean.add(1,1); debug进去 ）
    bean.add(1,1);


2. CglibAopProxy 类 （ .proceed(); 点进去 ）
        // We need to create a method invocation...
        // 通过cglibMethodInvocation来启动advice通知
        retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();


3. CglibAopProxy 类 （ return super.proceed(); 点进去 ）
        try {
        	return super.proceed();
        }


4. ReflectiveMethodInvocation 类 （ .invoke(this); debug进去 ）
        // It's an interceptor, so we just invoke it: The pointcut will have
        // been evaluated statically before this object was constructed.
        // 普通拦截器，直接调用拦截器，将this作为参数传递以保证当前实例中调用链的执行
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);


5. ExposeInvocationInterceptor 类 （ return mi.proceed(); 点进去 ）
        try {
        	return mi.proceed();
        }


6. 递归 回到 CglibAopProxy 类 （ return super.proceed(); 点进去 ）
        try {
        	return super.proceed();
        }


7. 和 4 步 一样


8. AspectJAfterThrowingAdvice 类 （ return mi.proceed(); 点进去 ）
        try {
        	// 执行下一个通知/拦截器  methodInvocation
        	return mi.proceed();
        }


9. 递归 回到 CglibAopProxy 类 （ return super.proceed(); 点进去 ）
           try {
           	return super.proceed();
           }


10. 和 4 步 一样


11. AfterReturningAdviceInterceptor 类 （ Object retVal = mi.proceed(); 点进去 ）
        // 执行下一个通知/拦截器
        Object retVal = mi.proceed();


12. 递归 回到 CglibAopProxy 类 （ return super.proceed(); 点进去 ）
           try {
           	return super.proceed();
           }


13. 和 4 步 一样


14. AspectJAfterAdvice 类 （ return mi.proceed(); 点进去 ）
        try {
        	// 执行下一个通知/拦截器
        	return mi.proceed();
        }


15. 递归 回到 CglibAopProxy 类 （ return super.proceed(); 点进去 ）
           try {
           	return super.proceed();
           }


16. 和 4 步 一样



17. AspectJAroundAdvice 类 （ return invokeAdviceMethod(pjp, jpm, null, null); debug进去 ）
        return invokeAdviceMethod(pjp, jpm, null, null);


18. AbstractAspectJAdvice 类 （ invokeAdviceMethodWithGivenArgs( 点进去 ）
        return invokeAdviceMethodWithGivenArgs(argBinding(jp, jpMatch, returnValue, t));


19. AbstractAspectJAdvice 类 （ return this.aspectJAdviceMethod.invoke( 点进去       this.aspectJAdviceMethod 为 public java.lang.Object com.mashibing.aop.xml.util.LogUtil.around(org.aspectj.lang.ProceedingJoinPoint) throws java.lang.Throwable ）
        // 反射调用通知方法
        // this.aspectInstanceFactory.getAspectInstance()获取的是切面的实例
        return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), actualArgs);


19-1. java.lang.reflect.Method 类 （ return ma.invoke(obj, args); 点进去 ）
        return ma.invoke(obj, args); 

19-2. sun.reflect.DelegatingMethodAccessorImpl 类 （ return delegate.invoke(obj, args); 点进去 ）
        return delegate.invoke(obj, args); 

19-3. sun.reflect.NativeMethodAccessorImpl 类 （ return invoke0(method, obj, args); 点进去 ）
        return invoke0(method, obj, args);

19-4. 执行到 spring-debug/src/main/java/com/mashibing/aop/xml/util/LogUtil.java 类
        执行到 LogUtil 类的这一步了 ， Signature signature = pjp.getSignature();


20. 控制台上打印日志
        log Around ---环绕通知start：add方法开始执行，参数为：[1, 1]


21. 执行到 spring-debug/src/main/java/com/mashibing/aop/xml/util/LogUtil.java 类 （ result = pjp.proceed(args); debug进去 ）
        public Object around(ProceedingJoinPoint pjp) throws Throwable {
            Signature signature = pjp.getSignature();
            Object[] args = pjp.getArgs();
            Object result = null;
            try {
                System.out.println("log Around ---环绕通知start："+signature.getName()+"方法开始执行，参数为："+Arrays.asList(args));
                //通过反射的方式调用目标的方法，相当于执行method.invoke(),可以自己修改结果值
                result = pjp.proceed(args);


22. MethodInvocationProceedingJoinPoint 类 （ .proceed(); debug进去 ）
        return this.methodInvocation.invocableClone(arguments).proceed();// mi.proceed()  CglibMethodInvocation


23. 递归 回到 CglibAopProxy 类 （ return super.proceed(); 点进去 ）
           try {
           	return super.proceed();
           }


24. 和 4 步 一样


25. MethodBeforeAdviceInterceptor 类 （ this.advice.before( debug进去 ）
        // 执行前置通知的方法
        this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());


25-1. AspectJMethodBeforeAdvice 类 （ invokeAdviceMethod( debug进去 ）
        invokeAdviceMethod(getJoinPointMatch(), null, null);

25-2. AbstractAspectJAdvice 类 （ invokeAdviceMethodWithGivenArgs( debug进去 ）
        return invokeAdviceMethodWithGivenArgs(argBinding(getJoinPoint(), jpMatch, returnValue, ex));

25-3. AbstractAspectJAdvice 类 （ return this.aspectJAdviceMethod.invoke( debug进去 ）
        return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), actualArgs);

25-4. java.lang.reflect.Method 类 （ return ma.invoke(obj, args); 点进去 ）
        return ma.invoke(obj, args);

25-5. sun.reflect.DelegatingMethodAccessorImpl 类 （ return delegate.invoke(obj, args); 点进去 ）
        return delegate.invoke(obj, args);

25-6. sun.reflect.NativeMethodAccessorImpl 类 （ return invoke0(method, obj, args); 点进去 ）
        return invoke0(method, obj, args);

25-7. 执行到 spring-debug/src/main/java/com/mashibing/aop/xml/util/LogUtil.java 类 （ 执行以下逻辑 ）
        private int start(JoinPoint joinPoint){
            //获取方法签名
            Signature signature = joinPoint.getSignature();
            //获取参数信息
            Object[] args = joinPoint.getArgs();
            System.out.println("log Before ---"+signature.getName()+"方法开始执行：参数是"+Arrays.asList(args));
            return 100;
        }

25-8. 控制台打印 log Before ---add方法开始执行：参数是[1, 1]


26. 回到 MethodBeforeAdviceInterceptor 类 （ this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis()); 这个已经执行完了，执行下一步 return mi.proceed(); ）
        // 执行前置通知的方法
    	this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        // 执行下一个通知/拦截器，但是该拦截器是最后一个了，所以会调用目标方法
        return mi.proceed();


27. 递归 回到 CglibAopProxy 类
               try {
               	return super.proceed();
               }


28. 和 4 步 一样


29. 回到 spring-debug/src/main/java/com/mashibing/aop/xml/util/LogUtil.java 类 （ 回到 result = pjp.proceed(args); 这一步 ）
                result = pjp.proceed(args);
        //            result=100;
                System.out.println("log Around ---环绕通知stop"+signature.getName()+"方法执行结束");
            } catch (Throwable throwable) {
                System.out.println("log Around ---环绕异常通知："+signature.getName()+"出现异常");
                throw throwable;
            }finally {
                System.out.println("log Around ---环绕返回通知："+signature.getName()+"方法返回结果是："+result);
            }
            return result;
        }


30. 执行后续的 spring-debug/src/main/java/com/mashibing/aop/xml/util/LogUtil.java 类 中的方法



最终执行结果：
        log Around ---环绕通知start：add方法开始执行，参数为：[1, 1]
        log Before ---add方法开始执行：参数是[1, 1]
        log Around ---环绕通知stopadd方法执行结束
        log Around ---环绕返回通知：add方法返回结果是：2
        log After ---add方法执行结束。。。。。over
        log AfterReturning ---add方法执行结束，结果是：2



# 注解方式 执行AOP 
## 第一种 
    spring-debug/src/main/java/com/mashibing/aop/annotation
    和以上一样的 debug 方式 
    spring-debug/src/main/java/com/mashibing/aop/annotation/util/LogUtil.java
    代码方法执行顺序
    @Around
    @Before
    @After
    @AfterReturning
    @AfterThrowing
最终执行结果：
        log Around---环绕通知start：add方法开始执行，参数为：[1, 1]
        log Before---add方法开始执行：参数是[1, 1]
        log AfterReturning---add方法执行结束，结果是：2
        log After---add方法执行结束。。。。。over
        log Around---环绕通知stopadd方法执行结束
        log Around---环绕返回通知：add方法返回结果是：2

## 第二种 （ @Order注解并不影响执行顺序 ）
    spring-debug/src/main/java/com/mashibing/aop/annotation
    和以上一样的 debug 方式 
    spring-debug/src/main/java/com/mashibing/aop/annotation/util/LogUtil.java
    代码方法执行顺序

    @Around
    @Order(4)

    @Before
    @Order(5)

    @After
    @Order(3)

    @AfterReturning
    @Order(2)

    @AfterThrowing
    @Order(1)

最终执行结果：
        log Around---环绕通知start：add方法开始执行，参数为：[1, 1]
        log Before---add方法开始执行：参数是[1, 1]
        log AfterReturning---add方法执行结束，结果是：2
        log After---add方法执行结束。。。。。over
        log Around---环绕通知stopadd方法执行结束
        log Around---环绕返回通知：add方法返回结果是：2