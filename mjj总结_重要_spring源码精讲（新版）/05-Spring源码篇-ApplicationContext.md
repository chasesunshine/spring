# Spring源码篇-ApplicationContext

&emsp;&emsp;前面通过手写IoC，DI、AOP和Bean的配置。到最后ApplicationContext的门面处理，对于Spring相关的核心概念应该会比较清楚了。接下来我们就看看在Spring源码中，对于的核心组件是如何实现的。

# 一、ApplicationContext

&emsp;&emsp;ApplicationContext到底是什么？字面含义是应用的上下文。这块我们需要看看ApplicationContext的具体的结构。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/2073d4d4cf364fce80bf25926f431a50.png)

&emsp;&emsp;通过ApplicationContext实现的相关接口来分析，ApplicationContext接口在具备BeanFactory的功能的基础上还扩展了 `应用事件发布`,`资源加载`,`环境参数`和 `国际化`的能力。然后我们来看看ApplicationContext接口的实现类的情况。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/5c6a9bd5d8fd471b825197fcb17efe5f.png)

&emsp;&emsp;在ApplicationContext的实现类中有两个比较重要的分支 `AbstractRefreshableApplicationContext`和 `GenericApplicationContext`.

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/c5121e3a0e414a2295959361d15886a2.png)

# 二、BeanFactory

&emsp;&emsp;上面分析了 `ApplicationContext`接口的结构。然后我们来看看 `BeanFactory`在ApplicationContext中具体的实现是怎么样的

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/6901d78727684f83a703421b851ed606.png)

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/896721ac8ec14822a6ca13840f5c36bc.png)

可以看到具体的实现是 DefaultListableBeanFactory .然后我们来看看他的体系结构

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/9a10e19545d34e419bc3d369ccc7158e.png)

BeanFactory的继承体系

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/5c929a82079444d292c6ed13d4ca640e.png)

# 三、BeanDefinition

&emsp;&emsp;然后我们来了解下ApplicationContext是如何来加载Bean定义的。具体代码我们需要分为XML配置文件和基于注解的两种方式来看。

## 1.基于XML方式

&emsp;&emsp;我们先定义对应的application.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd">

	<bean id="beanE" class="com.study.spring.sample.config.BeanE" />

	<bean id="beanF" class="com.study.spring.sample.config.BeanF" ></bean>

	<context:annotation-config/>

	<context:component-scan base-package="com.study.spring.sample.config" ></context:component-scan>

</beans>
```

然后我们的测试类代码

```java
public class XMLConfigMain {

	public static void main(String[] args) {
		ApplicationContext context = new GenericXmlApplicationContext(
				"classpath:com/study/spring/sample/config/application.xml");
		BeanF bf = context.getBean(BeanF.class);
		bf.do1();
	}
}
```

处理的过程  解析XML --> BeanDefinition --> BeanDefinitionRegistry --> BeanFactory

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/8874383e92a64d58aa15a61044c75bff.png)

## 2.基于注解方式

&emsp;&emsp;然后来看看基于注解方式的使用的情况。首先是我们的配置类

```java
@Configuration
@ComponentScan("com.study.spring.sample.config")
public class JavaBasedMain {

	@Bean
	public BeanH getBeanH() {
		return new BeanH();
	}

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(JavaBasedMain.class);

		BeanH bh = context.getBean(BeanH.class);
		bh.doH();
	}
}
```

然后是我们的测试类

```java
public class AnnotationMain {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext("com.study.spring.sample.config");

		BeanG bg = context.getBean(BeanG.class);
		bg.dog();
	}
}
```

注解使用有两种方法：

1. 配置扫描路径
2. 配置@Configuration的注解类

### 2.1 this构造方法

&emsp;&emsp;在this的构造方法中会完成相关的配置处理。

```java
	public AnnotationConfigApplicationContext() {
		this.reader = new AnnotatedBeanDefinitionReader(this);
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}
```

首先是AnnotatedBeanDefinitionReader(this)方法。会完成核心的ConfigurationClassPostProcessor的注入。ConfigurationClassPostProcessor 会完成@Configuration相关的注解的解析

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/153f600faf8548988034cd9086c037dc.png)

this.scanner其实就是创建了一个对应的扫描器

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/2c843dd30d06465394c0fc7aac2f4a36.png)

### 2.2 扫描实现

&emsp;&emsp;扫描就需要进入到scan方法中。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/4765d5ce6c7c4ae0867743bf5c630695.png)

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/41f2da6eeaad48189a77e4423189c83f.png)

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/5cf16dbf453b430eba6edbcf5363c585.png)

完成相关的注册

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/f043f37857bf40abaed8df8e5f943079.png)

### 2.3 @Configuration

&emsp;&emsp;@Configuration的解析其实是在refresh方法中来实现的。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/e236f11207b84d93a708792bb8713d25.png)

## 3.小结

&emsp;&emsp;通过上面的分析其实我们已经对Bean定义的扫描，解析和注册过程有了一定的了解。归纳为：

1. reader解析XML，完成xml方法配置的bean定义
2. scanner扫描指定包下的类，找出带有@Component注解的类，注册成Bean定义
3. 通过ConfigurationClassPostProcessor对带有@Configuration注解的类进行处理，解析它上面的注解，以及类中带有@Bean 注解，加入这些的Bean的定义。

## 4.BeanDefinition

&emsp;&emsp;;然后我们来看看BeanDefinition的继承结构

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/f68b78ebeb3046efb97eb028925a4611.png)

&emsp;&emsp;继承属性访问器和元数据接口，增加了Bean定义操作，实现了数据和操作解耦。属性访问器和元数据接口接着往下看。

### 4.1 BeanMetadataElement

&emsp;&emsp;BeanMetadataElement提供了获取数据源的方式，也就是可以指导Bean是来自哪个类。

```java
public interface BeanMetadataElement {

	/**
	 * Return the configuration source {@code Object} for this metadata element
	 * (may be {@code null}).
	 */
	@Nullable
	default Object getSource() {
		return null;
	}

}
```

### 4.2 BeanMetadataAttribute元数据属性

&emsp;&emsp;实现了元数据接口，增加了属性的名字和值。。

```java

public class BeanMetadataAttribute implements BeanMetadataElement {

	private final String name;

	@Nullable
	private final Object value;

	@Nullable
	private Object source;

}
```

### 4.3 AttributeAccessor属性访问器

&emsp;&emsp;AttributeAccessor用来给Bean定义了增删改查属性的功能

```java
public interface AttributeAccessor {

	/**
	 * Set the attribute defined by {@code name} to the supplied {@code value}.
	 * If {@code value} is {@code null}, the attribute is {@link #removeAttribute removed}.
	 * <p>In general, users should take care to prevent overlaps with other
	 * metadata attributes by using fully-qualified names, perhaps using
	 * class or package names as prefix.
	 * @param name the unique attribute key
	 * @param value the attribute value to be attached
	 */
	void setAttribute(String name, @Nullable Object value);

	/**
	 * Get the value of the attribute identified by {@code name}.
	 * Return {@code null} if the attribute doesn't exist.
	 * @param name the unique attribute key
	 * @return the current value of the attribute, if any
	 */
	@Nullable
	Object getAttribute(String name);

	/**
	 * Remove the attribute identified by {@code name} and return its value.
	 * Return {@code null} if no attribute under {@code name} is found.
	 * @param name the unique attribute key
	 * @return the last value of the attribute, if any
	 */
	@Nullable
	Object removeAttribute(String name);

	/**
	 * Return {@code true} if the attribute identified by {@code name} exists.
	 * Otherwise return {@code false}.
	 * @param name the unique attribute key
	 */
	boolean hasAttribute(String name);

	/**
	 * Return the names of all attributes.
	 */
	String[] attributeNames();

}
```

### 4.4 AttributeAccessorSupport属性访问抽象实现类

&emsp;&emsp;内部定义了1个map来存放属性。

```java
public abstract class AttributeAccessorSupport implements AttributeAccessor, Serializable {

	/** Map with String keys and Object values. */
	private final Map<String, Object> attributes = new LinkedHashMap<>();


	@Override
	public void setAttribute(String name, @Nullable Object value) {
		Assert.notNull(name, "Name must not be null");
		if (value != null) {
			this.attributes.put(name, value);
		}
		else {
			removeAttribute(name);
		}
	}
    //  ......
}
```

### 4.5 BeanMetadataAttributeAccessor元数据属性访问器

&emsp;&emsp;继承AttributeAccessorSupport具备属性访问功能，实现BeanMetadataElement具备获取元数据功能。 **AbstractBeanDefinition就继承于它，使得同时具有属性访问和元数据访问的功能 **。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/165ad58366864a0fa1d99f6dbc28d9d1.png)

结合AbstractBeanDefinition.来看看他们的类图结构

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/75c60614f1084c178655684cee110a1d.png)

## 5. BeanDefinition继承体系

### 5.1 AnnotatedBeanDefinition

&emsp;&emsp;增加了2个方法，获取bean所在类的注解元数据和工厂方法元数据，这些数据在进行解析处理的时候需要用到。

```java
public interface AnnotatedBeanDefinition extends BeanDefinition {

	/**
	 * Obtain the annotation metadata (as well as basic class metadata)
	 * for this bean definition's bean class.
	 * @return the annotation metadata object (never {@code null})
	 */
	AnnotationMetadata getMetadata();

	/**
	 * Obtain metadata for this bean definition's factory method, if any.
	 * @return the factory method metadata, or {@code null} if none
	 * @since 4.1.1
	 */
	@Nullable
	MethodMetadata getFactoryMethodMetadata();

}
```

&emsp;&emsp;该注解有三个具体的实现。ScannedGenericBeanDefinition、AnnotatedGenericBeanDefinition、ConfigurationClassBeanDefinition。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/b7c4c4b9d92344b0aac34a2dca493f34.png)

### 5.2 AbstractBeanDefinition模板类

&emsp;&emsp;AbstractBeanDefinition我们可以称之为BeanDefinition的模板类。结构我们上面其实有梳理

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/be5bc378391e4466a545c507f63f3b45.png)

&emsp;&emsp;通过上面我们可以看到AbstractBeanDefinition 具备了 Bean元数据的获取和属性相关的操作。同时AbstractBeanDefinition的继承结构

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1462/1662614737047/460a569a796340a5aea01ea57d973d88.png)

### 5.3 RootBeanDefinition根bean定义

&emsp;&emsp;它主要用在spring内部的bean定义、把不同类型的bean定义合并成RootBeanDefinition（getMergedLocalBeanDefinition方法）。没有实现BeanDefinition接口的设置获取父bean定义方法，不支持设置父子beanDefinition。

### 5.4 ConfigurationClassBeanDefinition

&emsp;&emsp;用作ConfigurationClassPostProcessor解析过程中封装配置类的bean定义。

### 5.5 GenericBeanDefinition

&emsp;&emsp;GenericBeanDefinition通用Bean的定义。

### 5.6 ScannedGenericBeanDefinition

&emsp;&emsp;@ComponentScan扫描的bean定义使用。

### 5.7 AnnotatedGenericBeanDefinition
